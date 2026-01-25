"use client";

import { useState, useEffect, useCallback } from "react";
import { usePathname } from "next/navigation";

export function useAuth() {
  const [user, setUser] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const pathname = usePathname();

  // Clear all browser storage (localStorage and sessionStorage)
  const clearAllStorage = () => {
    localStorage.removeItem("user");
    localStorage.removeItem("authToken");
    sessionStorage.clear();
  };

  // Check if token is blacklisted by calling user-service's blacklist endpoint
  const isTokenBlacklisted = async (token) => {
    try {
      const API_BASE_URL =
        process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080/api";

      const response = await fetch(`${API_BASE_URL}/users/blacklist/check`, {
        method: "GET",
        headers: {
          "X-Token-Check": token,
          "Content-Type": "application/json",
        },
      });

      if (response.ok) {
        const isBlacklisted = await response.json();
        return isBlacklisted === true;
      }
      return false;
    } catch {
      return false;
    }
  };

  // Validate token with backend
  const validateTokenWithBackend = async (token, userId) => {
    try {
      const API_BASE_URL =
        process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080/api";

      // First check if token is blacklisted
      const blacklisted = await isTokenBlacklisted(token);
      if (blacklisted) {
        return false;
      }

      // Then validate the token is still valid
      const response = await fetch(`${API_BASE_URL}/users/${userId}`, {
        method: "GET",
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
      });

      return response.ok;
    } catch {
      return false;
    }
  };

  // Validate on initial mount and route changes
  useEffect(() => {
    let isMounted = true;

    const checkAuth = async () => {
      try {
        const token = localStorage.getItem("authToken");
        const userData = localStorage.getItem("user");

        if (token && userData) {
          const parsedUser = JSON.parse(userData);

          // Validate token with backend
          const isValid = await validateTokenWithBackend(token, parsedUser.id);

          if (isMounted) {
            if (isValid) {
              setUser(parsedUser);
            } else {
              // Token is invalid or blacklisted - clear storage and logout
              clearAllStorage();
              setUser(null);
            }
          }
        } else {
          if (isMounted) {
            setUser(null);
          }
        }
      } catch {
        if (isMounted) {
          clearAllStorage();
          setUser(null);
        }
      }
      if (isMounted) {
        setIsLoading(false);
      }
    };

    // Run validation
    checkAuth();

    // Listen for storage changes (login/logout from other tabs)
    const handleStorageChange = (e) => {
      if (e.key === "user" || e.key === "authToken") {
        checkAuth();
      }
    };

    // Listen for custom auth events
    const handleAuthChange = () => {
      checkAuth();
    };

    window.addEventListener("storage", handleStorageChange);
    window.addEventListener("auth-change", handleAuthChange);

    return () => {
      isMounted = false;
      window.removeEventListener("storage", handleStorageChange);
      window.removeEventListener("auth-change", handleAuthChange);
    };
  }, [pathname]); // Re-run on route changes

  // Store user data and token after successful backend authentication
  const login = useCallback((userData, token) => {
    if (!userData || !token) {
      throw new Error("Invalid login: userData and token are required");
    }

    localStorage.setItem("user", JSON.stringify(userData));
    localStorage.setItem("authToken", token);
    setUser(userData);

    window.dispatchEvent(new Event("auth-change"));

    return userData;
  }, []);

  const logout = useCallback(async () => {
    const token = localStorage.getItem("authToken");

    if (token) {
      try {
        const API_BASE_URL =
          process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080/api";
        await fetch(`${API_BASE_URL}/users/logout`, {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
          body: JSON.stringify({ token }),
        });
      } catch {
        // Continue with local logout even if API call fails
      }
    }

    clearAllStorage();
    setUser(null);

    window.dispatchEvent(new Event("auth-change"));
  }, []);

  return {
    user,
    isLoading,
    login,
    logout,
    isAuthenticated: !!user,
  };
}
