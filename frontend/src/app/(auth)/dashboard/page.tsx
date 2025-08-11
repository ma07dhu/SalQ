
"use client";
import AdminDashboard from "@/app/(auth)/admin/dashboard/page";
import EmployeeDashboard from "@/app/(auth)/employee/dashboard/page";
import HrDashboard from "@/app/(auth)/hr/dashboard/page";
import { useAuth } from "@/context/auth-context";
import { useRouter } from "next/navigation";
import { useEffect } from "react";

export default function DashboardPage() {
  const { role } = useAuth();
  const router = useRouter();

  useEffect(() => {
    if (!role) {
      router.push('/login');
    }
  }, [role, router]);

  if (!role) {
    // You can render a loading spinner here
    return null;
  }

  return (
    <>
      {role === 'admin' && <AdminDashboard />}
      {role === 'hr' && <HrDashboard />}
      {role === 'employee' && <EmployeeDashboard />}
    </>
  );
}
