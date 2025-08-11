
"use client";
import { SidebarTrigger } from "@/components/ui/sidebar";
import { FontSizeAdjuster } from "../shared/font-size-adjuster";
import { usePathname } from "next/navigation";
import { useMemo, useState, useEffect } from "react";
import { useSidebar } from "../ui/sidebar";
import { Menu, X } from "lucide-react";
import { UserNav } from "../shared/user-nav";


function getPageTitle(pathname: string): string {
    const segments = pathname.split('/').filter(Boolean);
    if (segments.length === 0) return "Dashboard";

    const lastSegment = segments[segments.length - 1];
    
    // Handle specific cases
    if (lastSegment === 'dashboard') {
        if (segments.length > 1) {
             const role = segments[0];
             if (role === 'hr') return "HR Dashboard";
             if (role === 'admin') return "Admin Dashboard";
        }
        // Default to Employee Dashboard if no specific role prefix or if it's '/employee/dashboard' or just '/dashboard'
        return "Dashboard";
    }

    if (lastSegment === 'user-accounts') return "User Accounts";

    if (lastSegment === 'audit-logs') return "Audit Logs";

    if (lastSegment === 'salary-slips') {
      if(segments[0] === 'employee') return "My Salary Slips";
      return "Salary Slips";
    }

    if (lastSegment === 'change-password') {
        return "Change Password";
    }

    // Capitalize and replace dashes
    const title = lastSegment
        .replace(/-/g, ' ')
        .replace(/\b\w/g, char => char.toUpperCase());

    return title;
}


export function AppHeader() {
  const pathname = usePathname();
  const pageTitle = useMemo(() => getPageTitle(pathname), [pathname]);
  const { open, isMobile } = useSidebar();
  const [isClient, setIsClient] = useState(false);

  useEffect(() => {
    setIsClient(true);
  }, []);


  return (
    <header className="sticky top-0 z-10 flex h-16 items-center gap-4 border-b bg-background/80 px-4 backdrop-blur-sm md:px-6">
       <SidebarTrigger>
         {isClient && open && isMobile ? <X className="h-5 w-5" /> : <Menu className="h-5 w-5" />}
      </SidebarTrigger>

      <div className="flex w-full items-center gap-4 md:ml-auto md:gap-2 lg:gap-4">
        <h1 className="text-lg font-semibold md:text-xl flex-1">{pageTitle}</h1>
        <div className="ml-auto flex items-center gap-4">
            <FontSizeAdjuster />
            <UserNav />
        </div>
      </div>
    </header>
  );
}
