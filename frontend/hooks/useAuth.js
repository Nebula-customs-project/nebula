'use client';

import { useState, useEffect } from 'react';

export function useAuth() {
  const [user, setUser] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    // Check if user is logged in (from localStorage or session)
    const checkAuth = () => {
      try {
        const token = localStorage.getItem('authToken');
        const userData = localStorage.getItem('user');

        if (token && userData) {
          const parsedUser = JSON.parse(userData);
          setUser(parsedUser);
        } else {
          // No user logged in
          setUser(null);
        }
      } catch (err) {
        console.error('Auth check error:', err);
        setUser(null);
      }
      setIsLoading(false);
    };

    checkAuth();

    // Listen for storage changes (login/logout from other tabs or components)
    const handleStorageChange = (e) => {
      if (e.key === 'user' || e.key === 'authToken') {
        checkAuth();
      }
    };

    // Listen for custom auth events
    const handleAuthChange = () => {
      checkAuth();
    };

    window.addEventListener('storage', handleStorageChange);
    window.addEventListener('auth-change', handleAuthChange);

    return () => {
      window.removeEventListener('storage', handleStorageChange);
      window.removeEventListener('auth-change', handleAuthChange);
    };
  }, []);

  const login = (userDataOrUsername, tokenOrPassword) => {
    let userData;
    let token;

    // Check if first argument is an object (new API) or string (legacy mock login)
    if (typeof userDataOrUsername === 'object' && userDataOrUsername !== null) {
      // New API: login(userData, token)
      userData = userDataOrUsername;
      token = tokenOrPassword;
    } else {
      // Legacy mock login: login(username, password)
      userData = {
        id: 1,
        username: userDataOrUsername,
        email: `${userDataOrUsername}@nebula.com`,
        role: userDataOrUsername === 'admin' ? 'ADMIN' : 'USER',
      };
      token = 'mock-token-' + Date.now();
    }

    localStorage.setItem('user', JSON.stringify(userData));
    localStorage.setItem('authToken', token);
    setUser(userData);

    // Trigger auth change event for other components
    window.dispatchEvent(new Event('auth-change'));

    return userData;
  };

  const logout = async () => {
    const token = localStorage.getItem('authToken');

    // Call backend logout API to invalidate token
    if (token) {
      try {
        const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/api';
        await fetch(`${API_BASE_URL}/users/logout`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
          },
          body: JSON.stringify({ token })
        });
      } catch (error) {
        console.debug('Logout API call failed:', error.message);
        // Continue with local logout even if API call fails
      }
    }

    localStorage.removeItem('user');
    localStorage.removeItem('authToken');
    setUser(null);

    // Trigger auth change event for other components
    window.dispatchEvent(new Event('auth-change'));
  };

  return {
    user,
    isLoading,
    error,
    login,
    logout,
    isAuthenticated: !!user,
  };
}
