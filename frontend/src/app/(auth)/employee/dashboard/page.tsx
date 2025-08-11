
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { FileText, User } from "lucide-react";
import Link from 'next/link';

export default function EmployeeDashboard() {
  return (
    <div className="grid gap-6 md:grid-cols-2">
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
