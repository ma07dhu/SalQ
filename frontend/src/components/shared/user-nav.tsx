
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

  const userDetails = {
    admin: { name: "Admin", fallback: "AD" },
    hr: { name: "HR", fallback: "HR" },
    employee: { name: "Employee", fallback: "EM" },
  }

  const currentUser = userDetails[role || 'employee'];

  return (
    <TooltipProvider>
      <Tooltip>
        <TooltipTrigger asChild>
           <Avatar className="h-9 w-9">
              <AvatarFallback>{currentUser.fallback}</AvatarFallback>
            </Avatar>
        </TooltipTrigger>
        <TooltipContent>
          <p>{currentUser.name}</p>
        </TooltipContent>
      </Tooltip>
    </TooltipProvider>
  );
}
