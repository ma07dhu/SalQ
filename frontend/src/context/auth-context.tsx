"use client";

import { useRouter } from 'next/navigation';
import React, { createContext, useContext, useState, useEffect, ReactNode, useMemo } from 'react';

export type UserRole = 'admin' | 'hr' | 'employee';

interface AuthContextType {
  role: UserRole | null;
  token: string | null;
  login: (role: UserRole, token: string) => void;
  logout: () => void;
  getToken: () => string | null;
  isLoading: boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [role, setRole] = useState<UserRole | null>(null);
  const [token, setToken] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);
  const router = useRouter();

  useEffect(() => {
    const storedRole = localStorage.getItem('userRole') as UserRole;
    const storedToken = localStorage.getItem('jwtToken');
    if (storedRole && storedToken) {
      setRole(storedRole);
      setToken(storedToken);
    }
    setLoading(false);
  }, []);

  const login = (userRole: UserRole, userToken: string) => {
    setRole(userRole);
    setToken(userToken);
    localStorage.setItem('userRole', userRole);
    localStorage.setItem('jwtToken', userToken);
    router.push(`/${userRole}/dashboard`);
  };

  const logout = () => {
    setRole(null);
    setToken(null);
    localStorage.removeItem('userRole');
    localStorage.removeItem('jwtToken');
    document.cookie = "token=; Max-Age=0; path=/"; // Clear cookie if stored there
    router.push('/login');
  };

  const getToken = () => {
      return token;
  };


  const value = useMemo(
    () => ({ 
      role, 
      token,
      login, 
      logout,
      getToken,
      isLoading: loading,
    }), 
    [role, token,loading]
  );

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
}
