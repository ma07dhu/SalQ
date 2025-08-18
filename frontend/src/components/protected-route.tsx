"use client";

import { useAuth } from "@/context/auth-context";
import { useRouter } from "next/navigation";
import { ReactNode, useEffect } from "react";

interface ProtectedRouteProps {
  allowedRoles: ("admin" | "hr" | "employee")[];
  children: ReactNode;
}

export default function ProtectedRoute({ allowedRoles, children }: ProtectedRouteProps) {
  const { role, token } = useAuth();
  const router = useRouter();

  useEffect(() => {
    if (!token) {
      router.push("/login"); // not logged in
    } else if (role && !allowedRoles.includes(role)) {
      router.push("/unauthorized"); // logged in but wrong role
    }
  }, [role, token, router, allowedRoles]);

  if (!token || (role && !allowedRoles.includes(role))) {
    return null; // or a loading spinner
  }

  return <>{children}</>;
}
