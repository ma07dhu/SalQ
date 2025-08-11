
"use client"
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
  CardFooter
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
import { MoreHorizontal, FileCheck2, Send, Repeat } from "lucide-react";
import {
    Select,
    SelectContent,
    SelectItem,
    SelectTrigger,
    SelectValue,
} from "@/components/ui/select"
import { Progress } from "@/components/ui/progress";
import React from "react";


const salaryStatus = [
  { department: "Engineering", staff: 54, processed: 54, status: "Completed" },
  { department: "Product", staff: 12, processed: 12, status: "Completed" },
  { department: "Design", staff: 8, processed: 0, status: "Pending" },
  { department: "HR", staff: 5, processed: 5, status: "Completed" },
  { department: "Marketing", staff: 10, processed: 10, status: "Completed" },
];

const statusVariantMap: { [key: string]: "default" | "secondary" | "destructive" | "outline" } = {
    Completed: "default",
    Pending: "secondary",
    Failed: "destructive",
}

export default function SalaryProcessingPage() {
    const [progress, setProgress] = React.useState(0)
    const [isProcessing, setIsProcessing] = React.useState(false)

    React.useEffect(() => {
        if (isProcessing) {
            const timer = setInterval(() => {
                setProgress((prev) => {
                    if (prev >= 100) {
                        clearInterval(timer);
                        setIsProcessing(false);
                        return 100;
                    }
                    return prev + 10;
                })
            }, 500);
            return () => clearInterval(timer);
        }
    }, [isProcessing])

    const handleProcess = () => {
        setProgress(0);
        setIsProcessing(true);
    }

  return (
      <div className="space-y-6">
    <Card>
        <CardHeader>
            <CardTitle>Processing Status</CardTitle>
        </CardHeader>
        <CardContent>
             {isProcessing && (
                <div className="w-full space-y-2 mb-6">
                    <Progress value={progress} />
                    <p className="text-sm text-muted-foreground text-center">{progress}% complete</p>
                </div>
            )}
            <div className="rounded-md border">
                <Table>
                    <TableHeader>
                        <TableRow>
                            <TableHead>Department</TableHead>
                            <TableHead className="text-center">Total Staff</TableHead>
                            <TableHead className="text-center">Processed</TableHead>
                            <TableHead>Status</TableHead>
                            <TableHead className="text-right">Actions</TableHead>
                        </TableRow>
                    </TableHeader>
                    <TableBody>
                        {salaryStatus.map((item) => (
                            <TableRow key={item.department}>
                                <TableCell className="font-medium">{item.department}</TableCell>
                                <TableCell className="text-center">{item.staff}</TableCell>
                                <TableCell className="text-center">{progress >= 100 ? item.staff : item.processed}</TableCell>
                                <TableCell>
                                    <Badge variant={statusVariantMap[progress >= 100 ? "Completed" : item.status] || "outline"}>
                                        {progress >= 100 ? "Completed" : item.status}
                                    </Badge>
                                </TableCell>
                                <TableCell className="text-right">
                                    <Button variant="ghost" size="icon" disabled={progress < 100 && item.status !== 'Completed'}>
                                        <MoreHorizontal className="h-4 w-4" />
                                    </Button>
                                </TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </div>
        </CardContent>
        <CardFooter className="flex justify-end gap-2">
            <Button variant="outline"><Repeat className="mr-2 h-4 w-4"/> Re-run Failed</Button>
            <Button><Send className="mr-2 h-4 w-4"/> Notify Employees</Button>
        </CardFooter>
    </Card>
    </div>
  );
}
