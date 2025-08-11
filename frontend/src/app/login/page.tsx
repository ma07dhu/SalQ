import { LoginForm } from "@/components/auth/login-form";
import { Building2 } from "lucide-react";

export default function LoginPage() {
  return (
    <div className="flex min-h-screen items-center justify-center p-4">
      <div className="w-full max-w-md">
        <div className="flex justify-center mb-6">
            <div className="bg-primary/10 p-4 rounded-full">
                <Building2 className="w-10 h-10 text-primary" />
            </div>
        </div>
        <h1 className="text-2xl font-bold text-center mb-2 text-foreground">
          Welcome to SalQ
        </h1>
        <p className="text-center text-muted-foreground mb-8">
          Sign in to manage your payroll.
        </p>
        <LoginForm />
      </div>
    </div>
  );
}
