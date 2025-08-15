"use client";
import React, { useEffect, useState } from "react";
import { fetchWithAuth } from "@/utils/api";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { FileText, User } from "lucide-react";
import Link from 'next/link';

export default function EmployeeDashboard() {
  const [backendMessage, setBackendMessage] = useState("");

  useEffect(() => {
    async function loadData() {
      try {
        const res = await fetchWithAuth("http://localhost:8080/api/staff/dashboard");
        const text = await res.text();
        setBackendMessage(text);
      } catch (err) {
        console.error("Failed to load employee dashboard", err);
      }
    }
    loadData();
  }, []);

  return (
      <div className="grid gap-6 md:grid-cols-2">
        {backendMessage && (
            <div className="p-4 bg-blue-100 text-blue-800 rounded col-span-2">
              {backendMessage}
            </div>
        )}
        <Card>
          <CardHeader>
            <CardTitle>My Salary Slips</CardTitle>
            <CardDescription>
              Access your recent and past salary slips.
            </CardDescription>
          </CardHeader>
          <CardContent>
            <Button asChild>
              <Link href="/employee/salary-slips">
                <FileText className="mr-2 h-4 w-4" /> View Salary Slips
              </Link>
            </Button>
          </CardContent>
        </Card>
        <Card>
          <CardHeader>
            <CardTitle>My Profile</CardTitle>
            <CardDescription>
              View and manage your personal information.
            </CardDescription>
          </CardHeader>
          <CardContent>
            <Button asChild>
              <Link href="/profile">
                <User className="mr-2 h-4 w-4" /> Go to Profile
              </Link>
            </Button>
          </CardContent>
        </Card>
      </div>
  );
}