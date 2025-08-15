"use client";
import React, { useEffect, useState } from "react";
import { fetchWithAuth } from "@/utils/api"; // ⬅️ Make sure this exists

import {
    Card,
    CardContent,
    CardDescription,
    CardHeader,
    CardTitle,
} from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import {
    Table,
    TableBody,
    TableCell,
    TableHead,
    TableHeader,
    TableRow,
} from "@/components/ui/table";
import { FilePlus2, FileText, ShieldCheck, Users, IndianRupee } from "lucide-react";
import Link from 'next/link';

// Your existing sample summary cards
const summaryCards = [
    { title: "Total Staff", value: "1,254", icon: <Users className="h-5 w-5 text-muted-foreground" /> },
    { title: "Net Salary Paid", value: "₹45,231.89", description: "+20.1% from last month", icon: <IndianRupee className="h-5 w-5 text-muted-foreground" /> },
    { title: "New Hires", value: "+12", description: "in this month", icon: <Users className="h-5 w-5 text-muted-foreground" /> },
    { title: "Relieved Staff", value: "+3", description: "in this month", icon: <Users className="h-5 w-5 text-muted-foreground" /> },
];

// Your existing sample activities
const recentActivities = [
    { user: "Olivia Martin", action: "added a new staff member", details: "John Doe", time: "5m ago" },
    { user: "John Doe", action: "updated salary structure", details: "Senior Developer", time: "1h ago" },
    { user: "Admin", action: "processed May 2024 salary", details: "For all departments", time: "1d ago" },
    { user: "Jane Smith", action: "imported 50 users via CSV", details: "New joiners batch", time: "2d ago" },
    { user: "System", action: "sent salary slip notifications", details: "May 2024", time: "2d ago" },
];

export default function AdminDashboard() {
    const [backendMessage, setBackendMessage] = useState("");

    useEffect(() => {
        async function loadBackendMessage() {
            try {
                const res = await fetchWithAuth("http://localhost:8080/api/admin/dashboard");
                const text = await res.text();
                setBackendMessage(text);
            } catch (err) {
                console.error("Error fetching admin dashboard data", err);
            }
        }
        loadBackendMessage();
    }, []);

    return (
        <div className="flex flex-col gap-6">
            {/* ✅ Show backend auth confirmation if present */}
            {backendMessage && (
                <div className="p-4 bg-green-100 text-green-800 rounded">
                    {backendMessage}
                </div>
            )}

            <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
                {summaryCards.map((card, index) => (
                    <Card key={index}>
                        <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                            <CardTitle className="text-sm font-medium">{card.title}</CardTitle>
                            {card.icon}
                        </CardHeader>
                        <CardContent>
                            <div className="text-2xl font-bold">{card.value}</div>
                            {card.description && (
                                <p className="text-xs text-muted-foreground">{card.description}</p>
                            )}
                        </CardContent>
                    </Card>
                ))}
            </div>

            <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-3">
                <Card className="lg:col-span-2">
                    <CardHeader>
                        <CardTitle>Recent Activity</CardTitle>
                        <CardDescription>An overview of recent actions in the system.</CardDescription>
                    </CardHeader>
                    <CardContent>
                        <Table>
                            <TableHeader>
                                <TableRow>
                                    <TableHead>User</TableHead>
                                    <TableHead>Action</TableHead>
                                    <TableHead>Details</TableHead>
                                    <TableHead className="text-right">Time</TableHead>
                                </TableRow>
                            </TableHeader>
                            <TableBody>
                                {recentActivities.map((activity, index) => (
                                    <TableRow key={index}>
                                        <TableCell className="font-medium">{activity.user}</TableCell>
                                        <TableCell>{activity.action}</TableCell>
                                        <TableCell className="hidden md:table-cell">{activity.details}</TableCell>
                                        <TableCell className="text-right text-muted-foreground">{activity.time}</TableCell>
                                    </TableRow>
                                ))}
                            </TableBody>
                        </Table>
                    </CardContent>
                </Card>

                <div className="flex flex-col gap-6">
                    <Card>
                        <CardHeader>
                            <CardTitle>Quick Actions</CardTitle>
                        </CardHeader>
                        <CardContent className="grid gap-2">
                            <Button asChild>
                                <Link href="/admin/staff"><FilePlus2 className="mr-2 h-4 w-4" /> Add New Staff</Link>
                            </Button>
                            <Button asChild variant="secondary">
                                <Link href="/admin/audit-logs"><ShieldCheck className="mr-2 h-4 w-4" /> View Audit Logs</Link>
                            </Button>
                            <Button asChild variant="secondary">
                                <Link href="/admin/reports"><FileText className="mr-2 h-4 w-4" /> Generate Report</Link>
                            </Button>
                        </CardContent>
                    </Card>

                    <Card>
                        <CardHeader className="pb-2">
                            <CardDescription>Next Salary Processing</CardDescription>
                            <CardTitle className="text-3xl">July 5, 2024</CardTitle>
                        </CardHeader>
                        <CardContent>
                            <div className="text-xs text-muted-foreground">for the month of June</div>
                        </CardContent>
                    </Card>
                </div>
            </div>
        </div>
    );
}
