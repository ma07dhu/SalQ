"use client";
import React, { useEffect, useState } from "react";
import { fetchWithAuth } from "@/utils/api";
import {
    Card, CardContent, CardDescription, CardHeader, CardTitle,
} from "@/components/ui/card";
import { Users, IndianRupee } from "lucide-react";
import {
    Table, TableBody, TableCell, TableHead, TableHeader, TableRow,
} from "@/components/ui/table";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";

export default function HrDashboard() {
    const [backendMessage, setBackendMessage] = useState("");

    useEffect(() => {
        async function loadData() {
            try {
                const res = await fetchWithAuth("http://localhost:8080/api/hr/dashboard");
                const text = await res.text();
                setBackendMessage(text);
            } catch (err) {
                console.error("Error fetching HR dashboard", err);
            }
        }
        loadData();
    }, []);

    const summaryCards = [
        { title: "Your Department Staff", value: "88", icon: <Users className="h-5 w-5 text-muted-foreground" /> },
        { title: "Department Net Pay", value: "₹2,125,300", description: "May 2024", icon: <IndianRupee className="h-5 w-5 text-muted-foreground" /> },
    ];

    const recentUpdates = [
        { staff: "Riya Singh", update: "Marked as 'On Leave'", time: "2h ago" },
        { staff: "Karan Verma", update: "Contact info updated", time: "1d ago" },
        { staff: "Sonia Rao", update: "Joined 'Engineering' team", time: "3d ago" },
    ];

    const departments = ["All", "Administration", "Computer Science", "ECE", "EE", "CIVIL", "Mechanical", "MBA", "MCA", "Placement"];

    return (
        <div className="flex flex-col gap-6">
            {backendMessage && (
                <div className="p-4 bg-green-100 text-green-800 rounded">
                    {backendMessage}
                </div>
            )}
            <div className="grid gap-4 md:grid-cols-2">
                {summaryCards.map((card, index) => (
                    <Card key={index}>
                        <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                            <CardTitle className="text-sm font-medium">{card.title}</CardTitle>
                            {card.icon}
                        </CardHeader>
                        <CardContent>
                            <div className="text-2xl font-bold">{card.value}</div>
                            {card.description && <p className="text-xs text-muted-foreground">{card.description}</p>}
                        </CardContent>
                    </Card>
                ))}
            </div>
            <div className="grid gap-6">
                <Card>
                    <CardHeader>
                        <CardTitle>Recent Staff Updates</CardTitle>
                        <CardDescription>Updates you've made for staff in your department.</CardDescription>
                    </CardHeader>
                    <CardContent>
                        <div className="flex items-center justify-end mb-4">
                            <Select defaultValue="all">
                                <SelectTrigger className="w-[200px]">
                                    <SelectValue placeholder="Select Department" />
                                </SelectTrigger>
                                <SelectContent>
                                    {departments.map(dep => (
                                        <SelectItem key={dep} value={dep.toLowerCase().replace(' ', '-')}>{dep}</SelectItem>
                                    ))}
                                </SelectContent>
                            </Select>
                        </div>
                        <Table>
                            <TableHeader>
                                <TableRow>
                                    <TableHead>Staff Member</TableHead>
                                    <TableHead>Update</TableHead>
                                    <TableHead className="text-right">Time</TableHead>
                                </TableRow>
                            </TableHeader>
                            <TableBody>
                                {recentUpdates.map((activity, index) => (
                                    <TableRow key={index}>
                                        <TableCell className="font-medium">{activity.staff}</TableCell>
                                        <TableCell>{activity.update}</TableCell>
                                        <TableCell className="text-right text-muted-foreground">{activity.time}</TableCell>
                                    </TableRow>
                                ))}
                            </TableBody>
                        </Table>
                    </CardContent>
                </Card>
            </div>
        </div>
    );
}
