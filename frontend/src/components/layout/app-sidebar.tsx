
"use client";

import {
  SidebarContent,
  SidebarHeader,
  SidebarMenu,
  SidebarMenuItem,
  SidebarMenuButton,
  SidebarMenuSub,
  SidebarMenuSubButton,
  SidebarFooter,
  SidebarSeparator,
  SidebarGroup,
  SidebarGroupLabel,
  SidebarGroupContent,
  SidebarClose,
} from '@/components/ui/sidebar';
import {
  LayoutDashboard,
  Users,
  Briefcase,
  Settings,
  ShieldCheck,
  FileText,
  Wallet,
  Building2,
  ChevronDown,
  User,
  LogOut,
  LifeBuoy,
} from 'lucide-react';
import { Collapsible, CollapsibleContent, CollapsibleTrigger } from '@/components/ui/collapsible';
import Link from 'next/link';
import { useAuth } from '@/context/auth-context';
import { usePathname } from 'next/navigation';

export function AppSidebar() {
  const { role, logout } = useAuth();
  const pathname = usePathname();
  
  const adminLinks = [
    { href: "/admin/dashboard", icon: <LayoutDashboard />, text: "Dashboard" },
    { href: "/admin/staff", icon: <Users />, text: "Staff Management" },
    { href: "/admin/user-accounts", icon: <Briefcase />, text: "User Accounts" },
    { 
      text: "Salary",
      icon: <Wallet />,
      subLinks: [
        { href: "/admin/salary/configuration", text: "Configuration" },
        // { href: "/admin/salary/processing", text: "Processing" },
      ]
    },
    { href: "/admin/reports", icon: <FileText />, text: "Reports" },
    { href: "/admin/audit-logs", icon: <ShieldCheck />, text: "Audit Logs" },
    { href: "/admin/settings", icon: <Settings />, text: "Settings" },
  ];

  const hrLinks = [
    { href: "/hr/dashboard", icon: <LayoutDashboard />, text: "Dashboard" },
    { href: "/hr/salary-slips", icon: <FileText />, text: "Salary Slips" },
    { href: "/hr/reports", icon: <FileText />, text: "Reports" },
    { href: "/change-password", icon: <Settings />, text: "Settings" },
  ];

  const employeeLinks = [
    { href: "/employee/dashboard", icon: <LayoutDashboard />, text: "Dashboard" },
    { href: "/employee/salary-slips", icon: <FileText />, text: "Salary Slips" },
    { href: "/profile", icon: <User />, text: "My Profile" },
    { href: "/change-password", icon: <Settings />, text: "Settings" },
  ];

  const isLinkActive = (href: string) => pathname === href;
  const isSubMenuOpen = (subLinks: { href: string }[]) => subLinks.some(link => pathname.startsWith(link.href));


  return (
    <>
      <SidebarHeader>
        <div className="flex items-center gap-3">
            <div className="bg-primary/10 p-2 rounded-lg">
                <Building2 className="w-6 h-6 text-primary" />
            </div>
            <h1 className="text-xl font-bold text-foreground">SalQ</h1>
        </div>
         <SidebarClose />
      </SidebarHeader>
      <SidebarContent>
        {/* Admin Section */}
        {role === 'admin' && (
          <SidebarGroup>
              <SidebarGroupContent>
                  <SidebarMenu>
                    {adminLinks.map((link, index) => {
                       if (link.subLinks) {
                            return (
                                <Collapsible key={index} defaultOpen={isSubMenuOpen(link.subLinks)}>
                                    <SidebarMenuItem>
                                        <CollapsibleTrigger asChild>
                                            <SidebarMenuButton tooltip={link.text}>
                                                {link.icon}
                                                <span>{link.text}</span>
                                                <ChevronDown className="ml-auto h-4 w-4 transition-transform group-data-[state=open]:rotate-180" />
                                            </SidebarMenuButton>
                                        </CollapsibleTrigger>
                                    </SidebarMenuItem>
                                    <CollapsibleContent asChild>
                                        <SidebarMenuSub>
                                            {link.subLinks.map((subLink, subIndex) => (
                                                <SidebarMenuItem key={subIndex}>
                                                    <SidebarMenuSubButton asChild isActive={isLinkActive(subLink.href)}>
                                                        <Link href={subLink.href}>{subLink.text}</Link>
                                                    </SidebarMenuSubButton>
                                                </SidebarMenuItem>
                                            ))}
                                        </SidebarMenuSub>
                                    </CollapsibleContent>
                                </Collapsible>
                            )
                       }
                       return (
                            <SidebarMenuItem key={index}>
                                <SidebarMenuButton asChild isActive={isLinkActive(link.href!)} tooltip={link.text}>
                                    <Link href={link.href!}>
                                        {link.icon}
                                        <span>{link.text}</span>
                                    </Link>
                                </SidebarMenuButton>
                            </SidebarMenuItem>
                       )
                    })}
                  </SidebarMenu>
              </SidebarGroupContent>
          </SidebarGroup>
        )}

        {/* HR Section */}
        {role === 'hr' && (
          <SidebarGroup>
              <SidebarGroupContent>
                  <SidebarMenu>
                      {hrLinks.map((link, index) => (
                          <SidebarMenuItem key={index}>
                            <SidebarMenuButton asChild isActive={isLinkActive(link.href)} tooltip={link.text}>
                                <Link href={link.href}>
                                    {link.icon}
                                    <span>{link.text}</span>
                                </Link>
                            </SidebarMenuButton>
                        </SidebarMenuItem>
                      ))}
                  </SidebarMenu>
              </SidebarGroupContent>
          </SidebarGroup>
        )}
        
        {/* Employee Section */}
        {role === 'employee' && (
          <SidebarGroup>
              <SidebarGroupContent>
                  <SidebarMenu>
                     {employeeLinks.map((link, index) => (
                          <SidebarMenuItem key={index}>
                            <SidebarMenuButton asChild isActive={isLinkActive(link.href)} tooltip={link.text}>
                                <Link href={link.href}>
                                    {link.icon}
                                    <span>{link.text}</span>
                                </Link>
                            </SidebarMenuButton>
                        </SidebarMenuItem>
                      ))}
                  </SidebarMenu>
              </SidebarGroupContent>
          </SidebarGroup>
        )}
      </SidebarContent>
      <SidebarFooter>
        <SidebarSeparator />
         <SidebarMenu>
            <SidebarMenuItem>
                <SidebarMenuButton asChild tooltip="Support" isActive={isLinkActive('/support')}>
                    <Link href="/support">
                        <LifeBuoy />
                        <span>Support</span>
                    </Link>
                </SidebarMenuButton>
            </SidebarMenuItem>
            <SidebarMenuItem>
                <SidebarMenuButton asChild tooltip="Logout" onClick={() => logout()}>
                    <Link href="/login">
                        <LogOut />
                        <span>Logout</span>
                    </Link>
                </SidebarMenuButton>
            </SidebarMenuItem>
        </SidebarMenu>
      </SidebarFooter>
    </>
  );
}
