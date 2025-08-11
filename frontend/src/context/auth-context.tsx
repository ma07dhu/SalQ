
"use client";

import { useRouter } from 'next/navigation';
import React, { createContext, useContext, useState, useEffect, ReactNode, useMemo } from 'react';

export type UserRole = 'admin' | 'hr' | 'employee';

interface AuthContextType {
  role: UserRole | null;
  login: (role: UserRole) => void;
  logout: () => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [role, setRole] = useState<UserRole | null>(null);
  const router = useRouter();

  useEffect(() => {
    const storedRole = localStorage.getItem('userRole') as UserRole;
    if (storedRole) {
      setRole(storedRole);
    }
  }, []);

  const login = (userRole: UserRole) => {
    setRole(userRole);
    localStorage.setItem('userRole', userRole);
  };

  const logout = () => {
    setRole(null);
    localStorage.removeItem('userRole');
    router.push('/login');
  };

  const value = useMemo(() => ({ role, login, logout }), [role]);

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
