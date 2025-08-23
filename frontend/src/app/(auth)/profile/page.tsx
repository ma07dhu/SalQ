"use client";
import React, { useEffect, useState } from "react";
import { fetchWithAuth } from "@/utils/api";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
  CardFooter,
} from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Separator } from "@/components/ui/separator";

function ProfileField({ label, value }: { label: string; value: string | undefined }) {
  return (
    <div className="grid grid-cols-3 gap-4 items-center">
      <span className="text-muted-foreground text-sm">{label}</span>
      <span className="col-span-2 font-medium">{value || "-"}</span>
    </div>
  );
}

interface Staff {
  fullName: string;
  email: string;
  phone: string;
  dateOfBirth: string;
  address: string;
  employeeId: string;
  department: string;
  role: string;
  dateOfJoining: string;
}

export default function ProfilePage() {
  const [staff, setStaff] = useState<Staff | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function loadData() {
      try {
        const res = await fetchWithAuth("http://localhost:8080/api/staff/profile");
        if (!res.ok) throw new Error("Failed to fetch profile");
        const data = await res.json();
        setStaff(data);
      } catch (err) {
        console.error("Failed to load profile", err);
      } finally {
        setLoading(false);
      }
    }
    loadData();
  }, []);

  if (loading) {
    return <div className="p-4">Loading profile...</div>;
  }

  if (!staff) {
    return <div className="p-4 text-red-500">Failed to load profile data.</div>;
  }

  return (
    <div className="max-w-4xl mx-auto">
      <Card>
        <CardHeader>
          <CardTitle>My Profile</CardTitle>
          <CardDescription>
            This is your personal and employment information on record.
          </CardDescription>
        </CardHeader>
        <CardContent className="space-y-8">
          {/* Personal Info */}
          <div>
            <h3 className="text-lg font-semibold mb-4 text-foreground">Personal Information</h3>
            <div className="grid md:grid-cols-2 gap-x-8 gap-y-4">
              <ProfileField label="Full Name" value={staff.fullName} />
              <ProfileField label="Email" value={staff.email} />
              <ProfileField label="Phone" value={staff.phone} />
              <ProfileField label="Address" value={staff.address} />
            </div>
          </div>

          <Separator />

          {/* Official Info */}
          <div>
            <h3 className="text-lg font-semibold mb-4 text-foreground">Official Information</h3>
            <div className="grid md:grid-cols-2 gap-x-8 gap-y-4">
              <ProfileField label="Department" value={staff.department} />
              <ProfileField label="Role" value={staff.role} />
              <ProfileField label="Date of Joining" value={staff.dateOfJoining} />
            </div>
          </div>
        </CardContent>

        <CardFooter className="border-t pt-6">
          <p className="text-sm text-muted-foreground flex-1">
            For any changes, please raise a request with HR.
          </p>
          <Button>Request Changes</Button>
        </CardFooter>
      </Card>
    </div>
  );
}
