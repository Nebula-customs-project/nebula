"use client";

import { useState, useEffect, useCallback } from "react";
import { usePathname, useRouter } from "next/navigation";

export function useAuth() {
  const [user, setUser] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const pathname = usePathname();
  const router = useRouter();

  // Clear all browser storage (localStorage and sessionStorage)
  const clearAllStorage = useCallback(() => {
    localStorage.removeItem("user");
    localStorage.removeItem("authToken");
    sessionStorage.clear();
  }, []);

  // Force logout and redirect to login page
  const forceLogout = useCallback(() => {
    clearAllStorage();
    setUser(null);
    setIsLoading(false);
    router.push("/login");
  }, [clearAllStorage, router]);

  // Validate token with backend - single call to /users/{id}
  // Gateway checks: 1) Token signature 2) Token expiry 3) Token blacklist
  // Returns 401/403 if any check fails
  const validateTokenWithBackend = useCallback(async (token, userId) => {
    try {
      const API_BASE_URL =
        process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080/api";

      const response = await fetch(`${API_BASE_URL}/users/${userId}`, {
        method: "GET",
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
      });

      // Gateway returns 401/403 for invalid, expired, or blacklisted tokens
      return response.ok;
    } catch {
      // Network error - cannot validate
      return false;
    }
  }, []);

  // Validate on mount and route changes
  useEffect(() => {
    let isMounted = true;

    const checkAuth = async () => {
      const token = localStorage.getItem("authToken");
      const userData = localStorage.getItem("user");

      if (!token || !userData) {
        if (isMounted) {
          setUser(null);
          setIsLoading(false);
        }
        return;
      }

      try {
        const parsedUser = JSON.parse(userData);
        const isValid = await validateTokenWithBackend(token, parsedUser.id);

        if (!isMounted) return;

        if (isValid) {
          setUser(parsedUser);
        } else {
          forceLogout();
          return;
        }
      } catch {
        if (isMounted) {
          forceLogout();
          return;
        }
      }

      if (isMounted) {
        setIsLoading(false);
      }
    };

    checkAuth();

    const handleStorageChange = (e) => {
      if (e.key === "user" || e.key === "authToken") {
        checkAuth();
      }
    };

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
  }, [pathname, validateTokenWithBackend, forceLogout]);

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
  }, [clearAllStorage]);

  return {
    user,
    isLoading,
    login,
    logout,
    isAuthenticated: !!user,
  };
}
