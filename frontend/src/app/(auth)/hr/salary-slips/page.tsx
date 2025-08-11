
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
import { Badge } from "@/components/ui/badge";
import { Download, Search } from "lucide-react";
import { Input } from "@/components/ui/input";
import {
    Select,
    SelectContent,
    SelectItem,
    SelectTrigger,
    SelectValue,
} from "@/components/ui/select"


const salarySlips = [
  { id: "EMP001", name: "Aarav Sharma", month: "May 2024", net: "₹73,000", status: "Sent" },
  { id: "EMP002", name: "Diya Patel", month: "May 2024", net: "₹95,000", status: "Sent" },
  { id: "EMP003", name: "Rohan Mehta", month: "May 2024", net: "₹65,000", status: "Viewed" },
  { id: "EMP004", name: "Priya Singh", month: "May 2024", net: "₹88,000", status: "Sent" },
];

const statusVariantMap: { [key: string]: "default" | "secondary" | "destructive" | "outline" } = {
    Sent: "default",
    Viewed: "secondary",
}

export default function HRSalarySlipsPage() {
  return (
    <Card>
      <CardHeader>
        <CardTitle>Salary Slips</CardTitle>
        <CardDescription>
          View, manage, and download salary slips for employees in your department.
        </CardDescription>
      </CardHeader>
      <CardContent>
        <div className="flex flex-col sm:flex-row items-center justify-between gap-4 mb-6">
            <div className="relative w-full sm:max-w-xs">
                <Search className="absolute left-2.5 top-2.5 h-4 w-4 text-muted-foreground" />
                <Input placeholder="Search by name or ID..." className="pl-8" />
            </div>
            <div className="flex items-center gap-2 w-full sm:w-auto sm:flex-1 sm:justify-end">
                <Select defaultValue="2024">
                    <SelectTrigger className="w-full sm:w-[120px]">
                        <SelectValue placeholder="Year" />
                    </SelectTrigger>
                    <SelectContent>
                        <SelectItem value="2024">2024</SelectItem>
                        <SelectItem value="2023">2023</SelectItem>
                    </SelectContent>
                </Select>
                <Select defaultValue="may">
                    <SelectTrigger className="w-full sm:w-[140px]">
                        <SelectValue placeholder="Month" />
                    </SelectTrigger>
                    <SelectContent>
                        <SelectItem value="may">May</SelectItem>
                        <SelectItem value="april">April</SelectItem>
                    </SelectContent>
                </Select>
            </div>
        </div>
        <div className="rounded-md border">
            <Table>
                <TableHeader>
                    <TableRow>
                        <TableHead>Staff ID</TableHead>
                        <TableHead>Name</TableHead>
                        <TableHead>Month</TableHead>
                        <TableHead className="text-right">Net Salary</TableHead>
                        <TableHead>Status</TableHead>
                        <TableHead className="text-center">Action</TableHead>
                    </TableRow>
                </TableHeader>
                <TableBody>
                    {salarySlips.map((slip) => (
                        <TableRow key={slip.id}>
                            <TableCell className="font-medium">{slip.id}</TableCell>
                            <TableCell>{slip.name}</TableCell>
                            <TableCell>{slip.month}</TableCell>
                            <TableCell className="text-right font-semibold">{slip.net}</TableCell>
                            <TableCell>
                                <Badge variant={statusVariantMap[slip.status] || "outline"}>{slip.status}</Badge>
                            </TableCell>
                            <TableCell className="text-center">
                                <Button variant="ghost" size="icon">
                                    <Download className="h-5 w-5" />
                                    <span className="sr-only">Download Slip</span>
                                </Button>
                            </TableCell>
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
