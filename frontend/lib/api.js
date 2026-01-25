// API configuration and utilities

const API_BASE_URL =
  process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080/api";

// Helper to clear session on authentication failure
const clearSessionOnAuthError = () => {
  if (typeof window !== "undefined") {
    localStorage.removeItem("user");
    localStorage.removeItem("authToken");
    sessionStorage.clear();
    window.dispatchEvent(new Event("auth-change"));
  }
};

export const apiClient = {
  async request(endpoint, options = {}) {
    const token =
      typeof window !== "undefined" ? localStorage.getItem("authToken") : null;

    const headers = {
      "Content-Type": "application/json",
      ...options.headers,
    };

    if (token) {
      headers["Authorization"] = `Bearer ${token}`;
    }

    const response = await fetch(`${API_BASE_URL}${endpoint}`, {
      ...options,
      headers,
    });

    // Handle authentication errors - auto logout
    if (response.status === 401 || response.status === 403) {
      clearSessionOnAuthError();
      throw new Error(`Authentication failed: ${response.status}`);
    }

    if (!response.ok) {
      throw new Error(`API Error: ${response.status}`);
    }

    return await response.json();
  },

  get(endpoint) {
    return this.request(endpoint, { method: "GET" });
  },

  post(endpoint, data) {
    return this.request(endpoint, {
      method: "POST",
      body: JSON.stringify(data),
    });
  },

  put(endpoint, data) {
    return this.request(endpoint, {
      method: "PUT",
      body: JSON.stringify(data),
    });
  },

  delete(endpoint) {
    return this.request(endpoint, { method: "DELETE" });
  },
};

// Auth API
export const authApi = {
  async login(email, password) {
    return await apiClient.post("/users/login", { email, password });
  },

  register: (user) => apiClient.post("/users/register", user),

  logout: (token) => apiClient.post("/users/logout", { token }),
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
