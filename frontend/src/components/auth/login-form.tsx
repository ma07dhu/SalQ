
"use client";

import { Button } from "@/components/ui/button";
import {
  Card,
  CardContent,
  CardFooter,
} from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import Link from "next/link";
import { useRouter } from 'next/navigation';
import { useAuth, UserRole } from "@/context/auth-context";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import React from "react";

export function LoginForm() {
  const router = useRouter();
  const { login } = useAuth();
  const [role, setRole] = React.useState<UserRole>('admin');

  const handleLogin = (e: React.FormEvent) => {
    e.preventDefault();
    login(role);
    router.push('/dashboard');
  }

  return (
    <form onSubmit={handleLogin}>
      <Card className="border-none shadow-none">
        <CardContent className="space-y-4">
          <div className="space-y-2">
              <Label htmlFor="role">Role</Label>
               <Select onValueChange={(value) => setRole(value as UserRole)} defaultValue={role}>
                  <SelectTrigger>
                      <SelectValue placeholder="Select a role" />
                  </SelectTrigger>
                  <SelectContent>
                      <SelectItem value="admin">Admin</SelectItem>
                      <SelectItem value="hr">HR</SelectItem>
                      <SelectItem value="employee">Employee</SelectItem>
                  </SelectContent>
              </Select>
          </div>
          <div className="space-y-2">
            <Label htmlFor="email">Email</Label>
            <Input
              id="email"
              type="email"
              placeholder="user@example.com"
              required
              defaultValue="user@example.com"
            />
          </div>
          <div className="space-y-2">
            <div className="flex items-center justify-between">
              <Label htmlFor="password">Password</Label>
              <Link
                href="#"
                className="text-sm text-primary hover:underline"
              >
                Forgot password?
              </Link>
            </div>
            <Input id="password" type="password" required defaultValue="password" />
          </div>
        </CardContent>
        <CardFooter className="flex flex-col gap-4">
          <Button type="submit" className="w-full">Sign In</Button>
        </CardFooter>
      </Card>
    </form>
  );
}
