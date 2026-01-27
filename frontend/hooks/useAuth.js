"use client";

import { useState, useEffect, useCallback } from "react";
import { usePathname, useRouter } from "next/navigation";
import { authApi, setAuthState, isRefreshTokenValid, apiClient } from "../lib/api";

export function useAuth() {
  const [user, setUser] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const pathname = usePathname();
  const router = useRouter();

  // Clear user data from localStorage (tokens are in HttpOnly cookies)
  const clearUserData = useCallback(() => {
    localStorage.removeItem("user");
    sessionStorage.clear();
  }, []);

  // Force logout and redirect to login page
  const forceLogout = useCallback(() => {
    clearUserData();
    setUser(null);
    setIsLoading(false);
    router.push("/login");
  }, [clearUserData, router]);

  // Validate session by checking if we have user data and can make API calls
  // The actual token validation happens via cookies sent automatically
  const validateSession = useCallback(async (userData) => {
    try {
      // Use apiClient to take advantage of auto-refresh logic
      // If access token is expired (401), apiClient will try to refresh it automatically
      await apiClient.get(`/users/${userData.id}`);
      return true;
    } catch {
      return false;
    }
  }, []);

  // Check auth on mount and route changes
  useEffect(() => {
    let isMounted = true;

    const checkAuth = async () => {
      const userData = localStorage.getItem("user");

      if (!userData) {
        if (isMounted) {
          setUser(null);
          setIsLoading(false);
        }
        return;
      }

      try {
        const parsedUser = JSON.parse(userData);

        // Validate session with backend (cookies will be sent automatically)
        const isValid = await validateSession(parsedUser);

        if (!isMounted) return;

        if (isValid) {
          setUser(parsedUser);
        } else {
          // Session invalid - clear and redirect
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

    // Listen for auth changes from other tabs/components
    const handleAuthChange = () => {
      checkAuth();
    };

    window.addEventListener("auth-change", handleAuthChange);
    window.addEventListener("storage", (e) => {
      if (e.key === "user") {
        handleAuthChange();
      }
    });

    return () => {
      isMounted = false;
      window.removeEventListener("auth-change", handleAuthChange);
    };
  }, [pathname, validateSession, forceLogout]);

  // Login with new response format
  const login = useCallback((loginResponse) => {
    if (!loginResponse || !loginResponse.user) {
      throw new Error("Invalid login response");
    }

    const { user: userData } = loginResponse;
    // Tokens are handled by HttpOnly cookies
    setAuthState();

    // Store user in localStorage solely for persistence across reloads (until validation runs)
    localStorage.setItem("user", JSON.stringify(userData));
    setUser(userData);

    // Notify other components
    window.dispatchEvent(new Event("auth-change"));

    return userData;
  }, []);

  // Logout - calls backend to revoke tokens
  const logout = useCallback(async () => {
    try {
      await authApi.logout();
    } catch {
      // Continue with local logout even if API fails
    }

    clearUserData();
    setUser(null);
    window.dispatchEvent(new Event("auth-change"));
  }, [clearUserData]);

  return {
    user,
    isLoading,
    login,
    logout,
    isAuthenticated: !!user,
  };
}

