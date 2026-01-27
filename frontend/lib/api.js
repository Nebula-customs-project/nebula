// API configuration and utilities

const API_BASE_URL =
  process.env.NEXT_PUBLIC_API_URL || "/api";

// In-memory tracking of auth status (tokens are in HttpOnly cookies)
let isRefreshing = false;
let refreshPromise = null;

// Helper to clear session on authentication failure
const clearSessionOnAuthError = () => {
  if (typeof window !== "undefined") {
    // Only clear user data, tokens are cleared by the backend's Set-Cookie header on logout/failure usually
    // But we should clean up local state
    localStorage.removeItem("user");
    sessionStorage.clear();
    window.dispatchEvent(new Event("auth-change"));
  }
};

// Set auth state after login/refresh
// We no longer store tokens in memory
export const setAuthState = () => {
  // No-op for tokens as they are in cookies
  // We could track expiration time if provided in response for UI logic, but logic shouldn't rely on it for security
};

// Get access token (No longer possible/needed as it's HttpOnly)
export const getAccessToken = () => null;

// Get refresh token (No longer possible/needed as it's HttpOnly)
export const getRefreshToken = () => null;

// Try to refresh the token using HttpOnly cookie
const tryRefreshToken = async () => {
  // If already refreshing, wait for that to complete
  if (isRefreshing && refreshPromise) {
    return refreshPromise;
  }

  isRefreshing = true;
  refreshPromise = (async () => {
    try {
      // Call refresh endpoint with credentials (cookies)
      // Body is empty or minimal, backend checks cookie
      const response = await fetch(`${API_BASE_URL}/users/auth/refresh`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: "{}", // Empty JSON object allowed by backend
        credentials: "include",
      });

      if (!response.ok) {
        throw new Error("Refresh failed");
      }

      // Backend sets new cookies automatically
      return true;
    } catch (error) {
      console.error("Token refresh failed:", error);
      clearSessionOnAuthError();
      return false;
    } finally {
      isRefreshing = false;
      refreshPromise = null;
    }
  })();

  return refreshPromise;
};

export const apiClient = {
  async request(endpoint, options = {}, retryCount = 0) {
    // Proactive refresh removed as we cannot check expiry of HttpOnly cookies client-side
    // We rely on 401 interception below

    const headers = {
      "Content-Type": "application/json",
      ...options.headers,
    };

    const response = await fetch(`${API_BASE_URL}${endpoint}`, {
      ...options,
      headers,
      credentials: "include", // Send cookies with requests
    });

    // Handle 401 - try refresh and retry once
    if (response.status === 401 && retryCount === 0 && !options.skipRefresh) {
      const refreshed = await tryRefreshToken();
      if (refreshed) {
        // Retry original request
        return this.request(endpoint, options, retryCount + 1);
      }
      clearSessionOnAuthError();
      throw new Error("Session expired. Please login again.");
    }

    // Handle 403 - forbidden (no retry)
    if (response.status === 403) {
      throw new Error("Access forbidden");
    }

    if (!response.ok) {
      throw new Error(`API Error: ${response.status}`);
    }

    return await response.json();
  },

  get(endpoint, options = {}) {
    return this.request(endpoint, { method: "GET", ...options });
  },

  post(endpoint, data, options = {}) {
    return this.request(endpoint, {
      method: "POST",
      body: JSON.stringify(data),
      ...options,
    });
  },

  put(endpoint, data, options = {}) {
    return this.request(endpoint, {
      method: "PUT",
      body: JSON.stringify(data),
      ...options,
    });
  },

  delete(endpoint, options = {}) {
    return this.request(endpoint, { method: "DELETE", ...options });
  },
};

// Auth API
export const authApi = {
  async login(email, password) {
    const response = await fetch(`${API_BASE_URL}/users/login`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ email, password }),
      credentials: "include", // Receive cookies
    });

    if (!response.ok) {
      throw new Error(`Login failed: ${response.status}`);
    }

    const data = await response.json();
    // Tokens are set in HttpOnly cookies by the backend
    setAuthState();
    return data;
  },

  async register(user) {
    const response = await fetch(`${API_BASE_URL}/users/register`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(user),
      credentials: "include",
    });

    if (!response.ok) {
      throw new Error(`Registration failed: ${response.status}`);
    }

    return await response.json();
  },

  async logout() {
    try {
      await fetch(`${API_BASE_URL}/users/logout`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        credentials: "include",
      });
    } catch {
      // Continue with local logout even if API call fails
    }
    clearSessionOnAuthError();
  },

  async refreshToken() {
    return tryRefreshToken();
  },
};

// Admin API
export const adminApi = {
  getUsers: () =>
    apiClient.get("/admin/users").catch(() => ({
      users: [],
    })),

  getUser: (id) =>
    apiClient.get(`/admin/users/${id}`).catch(() => ({
      user: null,
    })),

  updateUserRole: (userId, role) =>
    apiClient.put(`/admin/users/${userId}/role`, { role }),

  deleteUser: (userId) => apiClient.delete(`/admin/users/${userId}`),
};

// Vehicle API
export const vehicleApi = {
  getVehicles: () =>
    apiClient.get("/vehicles").catch(() => ({
      vehicles: [],
    })),

  getVehicle: (id) =>
    apiClient.get(`/vehicles/${id}`).catch(() => ({
      vehicle: null,
    })),

  createVehicle: (vehicle) => apiClient.post("/vehicles", vehicle),

  updateVehicle: (id, vehicle) => apiClient.put(`/vehicles/${id}`, vehicle),

  deleteVehicle: (id) => apiClient.delete(`/vehicles/${id}`),
};

// Merchandise API
export const merchandiseApi = {
  getProducts: () =>
    apiClient.get("/merchandise").catch(() => ({
      products: [],
    })),

  getProduct: (id) =>
    apiClient.get(`/merchandise/${id}`).catch(() => ({
      product: null,
    })),
};

