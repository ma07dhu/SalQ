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
import { Download } from "lucide-react";
import {
    Select,
    SelectContent,
    SelectItem,
    SelectTrigger,
    SelectValue,
} from "@/components/ui/select"

const salarySlips = [
  { month: "May 2024", gross: "₹85,000", deductions: "₹12,000", net: "₹73,000" },
  { month: "April 2024", gross: "₹85,000", deductions: "₹12,000", net: "₹73,000" },
  { month: "March 2024", gross: "₹82,500", deductions: "₹11,500", net: "₹71,000" },
  { month: "February 2024", gross: "₹82,500", deductions: "₹11,500", net: "₹71,000" },
  { month: "January 2024", gross: "₹82,500", deductions: "₹11,500", net: "₹71,000" },
];

export default function SalarySlipsPage() {
  return (
    <Card>
      <CardHeader>
        <CardTitle>My Salary Slips</CardTitle>
        <CardDescription>
          View and download your salary slips from previous months.
        </CardDescription>
      </CardHeader>
      <CardContent>
        <div className="flex items-center gap-2 mb-6">
            <Select defaultValue="2024">
                <SelectTrigger className="w-[120px]">
                    <SelectValue placeholder="Year" />
                </SelectTrigger>
                <SelectContent>
                    <SelectItem value="2024">2024</SelectItem>
                    <SelectItem value="2023">2023</SelectItem>
                </SelectContent>
            </Select>
            <Select defaultValue="may">
                <SelectTrigger className="w-[140px]">
                    <SelectValue placeholder="Month" />
                </SelectTrigger>
                <SelectContent>
                    <SelectItem value="may">May</SelectItem>
                    <SelectItem value="april">April</SelectItem>
                    <SelectItem value="march">March</SelectItem>
                </SelectContent>
            </Select>
        </div>
        <div className="rounded-md border">
            <Table>
                <TableHeader>
                    <TableRow>
                        <TableHead>Month</TableHead>
                        <TableHead className="text-right">Gross Earnings</TableHead>
                        <TableHead className="text-right">Deductions</TableHead>
                        <TableHead className="text-right">Net Salary</TableHead>
                        <TableHead className="text-center">Action</TableHead>
                    </TableRow>
                </TableHeader>
                <TableBody>
                    {salarySlips.map((slip, index) => (
                        <TableRow key={index}>
                            <TableCell className="font-medium">{slip.month}</TableCell>
                            <TableCell className="text-right">{slip.gross}</TableCell>
                            <TableCell className="text-right text-destructive">{slip.deductions}</TableCell>
                            <TableCell className="text-right font-semibold">{slip.net}</TableCell>
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
      </CardContent>
    </Card>
  );
}
