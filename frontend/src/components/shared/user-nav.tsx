
"use client";

import { Avatar, AvatarFallback } from "@/components/ui/avatar";
import {
  Tooltip,
  TooltipContent,
  TooltipProvider,
  TooltipTrigger,
} from "@/components/ui/tooltip";
import { useAuth } from "@/context/auth-context";

export function UserNav() {
    const { role } = useAuth();

    const normalizedRole = typeof role === 'string'
        ? role.toLowerCase().replace('role_', '')
        : 'employee';

    type UserRoleKey = "admin" | "hr" | "employee";

    const userDetails: Record<UserRoleKey, { name: string; fallback: string }> = {
        admin: { name: "Admin", fallback: "AD" },
        hr: { name: "HR", fallback: "HR" },
        employee: { name: "Employee", fallback: "EM" },
    };

// Then assert in code:
    const roleKey = (normalizedRole as UserRoleKey) || "employee";
    const currentUser = userDetails[roleKey];

    return (
        <TooltipProvider>
            <Tooltip>
                <TooltipTrigger asChild>
                    <Avatar className="h-9 w-9">
                        <AvatarFallback>{currentUser.fallback}</AvatarFallback>
                    </Avatar>
                </TooltipTrigger>
                <TooltipContent><p>{currentUser.name}</p></TooltipContent>
            </Tooltip>
        </TooltipProvider>
    );
}
