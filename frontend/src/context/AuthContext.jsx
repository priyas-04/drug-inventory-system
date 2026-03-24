import React, { createContext, useContext, useState, useEffect } from 'react';
import { authAPI } from '../services/api';

// Create Auth Context - shared across the entire app
const AuthContext = createContext(null);

/**
 * AuthProvider wraps the entire app and provides:
 * - user: current logged-in user info (username, role, etc.)
 * - login(): authenticate user and store token
 * - logout(): clear session
 * - isAdmin(), isPharmacist(), isSupplier(): role helpers
 */
export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  // On app start, restore user from localStorage (persist login across refresh)
  useEffect(() => {
    const token    = localStorage.getItem('token');
    const userData = localStorage.getItem('user');
    if (token && userData) {
      setUser(JSON.parse(userData));
    }
    setLoading(false);
  }, []);

  /**
   * Log user in, save token + user info to localStorage.
   */
  const login = async (credentials) => {
    const response = await authAPI.login(credentials);
    const { token, username, role, fullName, email } = response.data.data;
    const userData = { username, role, fullName, email };

    // Store in localStorage so session persists on refresh
    localStorage.setItem('token', token);
    localStorage.setItem('user', JSON.stringify(userData));
    setUser(userData);
    return userData;
  };

  /**
   * Log user out, clear all stored data.
   */
  const logout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    setUser(null);
  };

  // Role helper functions
  const isAdmin       = () => user?.role === 'ADMIN';
  const isPharmacist  = () => user?.role === 'PHARMACIST';
  const isSupplier    = () => user?.role === 'SUPPLIER';

  // Don't render children until we know if user is logged in
  if (loading) return <div className="loading"><div className="spinner" /></div>;

  return (
    <AuthContext.Provider value={{ user, login, logout, isAdmin, isPharmacist, isSupplier }}>
      {children}
    </AuthContext.Provider>
  );
}

/**
 * Custom hook to use auth context in any component.
 * Usage: const { user, login, logout, isAdmin } = useAuth();
 */
export function useAuth() {
  return useContext(AuthContext);
}
