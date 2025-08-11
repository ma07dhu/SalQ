"use client";

import * as React from "react";
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
import { Search } from "lucide-react";
import { Input } from "@/components/ui/input";
import {
    Select,
    SelectContent,
    SelectItem,
    SelectTrigger,
    SelectValue,
} from "@/components/ui/select"
import { Calendar } from "@/components/ui/calendar"
import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover";
import { Calendar as CalendarIcon } from 'lucide-react';
import { format } from "date-fns";


const auditLogs = [
  { user: "Admin", action: "Processed May 2024 salary", details: "For all departments", ip: "192.168.1.1", time: "2024-06-01 10:00 AM" },
  { user: "John Doe", action: "Updated salary structure", details: "Senior Developer role", ip: "203.0.113.25", time: "2024-06-01 09:30 AM" },
  { user: "Jane Smith", action: "Imported 50 users via CSV", details: "New joiners batch", ip: "198.51.100.10", time: "2024-05-31 02:00 PM" },
  { user: "Olivia Martin", action: "Added new staff member", details: "John Doe (EMP002)", ip: "198.51.100.12", time: "2024-05-31 11:00 AM" },
  { user: "System", action: "Sent salary slip notifications", details: "May 2024", ip: "N/A", time: "2024-06-01 10:05 AM" },
];


export default function AuditLogsPage() {
    const [date, setDate] = React.useState<Date>()
  return (
    <Card>
      <CardHeader>
        <CardTitle>Audit Logs</CardTitle>
        <CardDescription>
          Track all system activities and changes for security and compliance.
        </CardDescription>
      </CardHeader>
      <CardContent>
        <div className="flex flex-col sm:flex-row items-center justify-between gap-4 mb-6">
            <div className="relative w-full sm:max-w-sm">
                <Search className="absolute left-2.5 top-2.5 h-4 w-4 text-muted-foreground" />
                <Input placeholder="Search by user or action..." className="pl-8" />
            </div>
            <div className="flex items-center gap-2 w-full sm:w-auto">
                 <Popover>
                    <PopoverTrigger asChild>
                      <Button
                        variant={"outline"}
                        className="w-full sm:w-[240px] justify-start text-left font-normal"
                      >
                        <CalendarIcon className="mr-2 h-4 w-4" />
                        {date ? format(date, "PPP") : <span>Pick a date</span>}
                      </Button>
                    </PopoverTrigger>
                    <PopoverContent className="w-auto p-0" align="start">
                      <Calendar
                        mode="single"
                        selected={date}
                        onSelect={setDate}
                        initialFocus
                      />
                    </PopoverContent>
                  </Popover>
            </div>
        </div>
        <div className="rounded-md border">
            <Table>
                <TableHeader>
                    <TableRow>
                        <TableHead>Timestamp</TableHead>
                        <TableHead>User</TableHead>
                        <TableHead className="hidden md:table-cell">Action</TableHead>
                        <TableHead>Details</TableHead>
                        <TableHead className="hidden lg:table-cell text-right">IP Address</TableHead>
                    </TableRow>
                </TableHeader>
                <TableBody>
                    {auditLogs.map((log, index) => (
                        <TableRow key={index}>
                            <TableCell className="font-medium">{log.time}</TableCell>
                            <TableCell>{log.user}</TableCell>
                            <TableCell className="hidden md:table-cell">{log.action}</TableCell>
                            <TableCell>{log.details}</TableCell>
                            <TableCell className="hidden lg:table-cell text-right">{log.ip}</TableCell>
                        </TableRow>
                    ))}
                </TableBody>
            </Table>
        </div>
         <div className="flex justify-end items-center space-x-2 pt-4">
            <Button variant="outline" size="sm">Previous</Button>
            <Button variant="outline" size="sm">Next</Button>
        </div>
      </CardContent>
    </Card>
  );
}
