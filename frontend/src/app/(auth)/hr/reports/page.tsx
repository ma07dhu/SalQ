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
  FileText,
  Download,
  PieChart,
  Calendar as CalendarIcon,
  CalendarRange,
} from "lucide-react";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import { Label } from "@/components/ui/label";
import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover";
import { Calendar } from "@/components/ui/calendar";
import { format } from "date-fns";
import { cn } from "@/lib/utils";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";

const reports = [
  {
    title: "Monthly Salary Statement",
    description: "Detailed report of all salaries paid for a selected month.",
    icon: <FileText className="h-8 w-8 text-primary" />,
  },
  // Uncomment if you want this functionality later
  // {
  //   title: "Department-wise Pay",
  //   description: "Breakdown of salary expenses across different departments.",
  //   icon: <PieChart className="h-8 w-8 text-primary" />,
  // },
  {
    title: "Yearly Financial Summary",
    description: "An overview of payroll expenses for the entire financial year.",
    icon: <CalendarIcon className="h-8 w-8 text-primary" />,
  },
];

const departments = [
  "All",
  "Admin",
  "CSE",
  "ECE",
  "EE",
  "CIVIL",
  "Mech",
  "MBA",
  "MCA",
  "T&P",
];

function DepartmentReportCard({
                                title,
                                description,
                                icon,
                              }: {
  title: string;
  description: string;
  icon: React.ReactNode;
}) {
  const [selectedDept, setSelectedDept] = React.useState<string>("");

  const handleDownload = async () => {
    if (!selectedDept) {
      alert("Please select a department");
      return;
    }
    const token = localStorage.getItem("jwtToken");
    if (!token) {
      alert("No token found, please login first.");
      return;
    }
    try {
      let url = "";
      if (title === "Monthly Salary Statement") {
        url = `http://localhost:8080/api/hr/reports/pdf?department=${encodeURIComponent(
            selectedDept
        )}&sign=true`;
      } else if (title === "Yearly Financial Summary") {
        const lastYear = new Date().getFullYear() - 1;
        url = `http://localhost:8080/api/hr/reports/pdf/annual?department=${encodeURIComponent(
            selectedDept
        )}&sign=true&year=${lastYear}`;
      } else {
        alert("Download not implemented for this report type yet.");
        return;
      }

      const response = await fetch(url, {
        method: "GET",
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      if (!response.ok) throw new Error("Failed to fetch report");

      const blob = await response.blob();
      const urlBlob = window.URL.createObjectURL(blob);
      window.open(urlBlob, "_blank");
    } catch (error) {
      console.error("Download error:", error);
      alert("Failed to generate report. Please try again.");
    }
  };

  return (
      <Card className="flex flex-col">
        <CardHeader className="flex-1">
          <div className="flex items-start gap-4">
            <div className="bg-primary/10 p-3 rounded-lg">{icon}</div>
            <div>
              <CardTitle className="text-lg">{title}</CardTitle>
              <CardDescription className="mt-2">{description}</CardDescription>
            </div>
          </div>
        </CardHeader>
        <CardContent>
          <Dialog>
            <DialogTrigger asChild>
              <Button className="w-full">
                <Download className="mr-2 h-4 w-4" />
                Generate & Download
              </Button>
            </DialogTrigger>
            <DialogContent className="sm:max-w-[425px]">
              <DialogHeader>
                <DialogTitle>Generate {title}</DialogTitle>
                <DialogDescription>Select a department for your report.</DialogDescription>
              </DialogHeader>
              <div className="grid gap-4 py-4">
                <div className="grid grid-cols-4 items-center gap-4">
                  <Label htmlFor="department" className="text-right">
                    Department
                  </Label>
                  <Select onValueChange={(val) => setSelectedDept(val)}>
                    <SelectTrigger className="col-span-3">
                      <SelectValue placeholder="Select a department" />
                    </SelectTrigger>
                    <SelectContent>
                      {departments.map((dep) => (
                          <SelectItem key={dep} value={dep}>
                            {dep}
                          </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                </div>
              </div>
              <DialogFooter>
                <Button onClick={handleDownload}>
                  <Download className="mr-2 h-4 w-4" />
                  Generate & Download
                </Button>
              </DialogFooter>
            </DialogContent>
          </Dialog>
        </CardContent>
      </Card>
  );
}

function DateRangeReportCard() {
  const [startDate, setStartDate] = React.useState<Date | undefined>();
  const [endDate, setEndDate] = React.useState<Date | undefined>();
  const [selectedDept, setSelectedDept] = React.useState<string>("");

  const handleDownload = async () => {
    if (!startDate || !endDate || !selectedDept) {
      alert("Please select start date, end date, and department");
      return;
    }
    const token = localStorage.getItem("jwtToken");
    if (!token) {
      alert("No token found, please login first.");
      return;
    }
    try {
      const response = await fetch(
          `http://localhost:8080/api/hr/reports/date-range?start=${format(
              startDate,
              "yyyy-MM-dd"
          )}&end=${format(endDate, "yyyy-MM-dd")}&department=${encodeURIComponent(
              selectedDept
          )}&sign=true`,
          {
            method: "GET",
            headers: {
              Authorization: `Bearer ${token}`,
            },
          }
      );
      if (!response.ok) throw new Error("Failed to fetch report");
      const blob = await response.blob();
      const fileURL = window.URL.createObjectURL(blob);
      window.open(fileURL, "_blank"); // Open PDF in new tab
    } catch (error) {
      console.error("Download error:", error);
      alert("Error fetching report");
    }
  };

  return (
      <Card className="flex flex-col">
        <CardHeader className="flex-1">
          <div className="flex items-start gap-4">
            <div className="bg-primary/10 p-3 rounded-lg">
              <CalendarRange className="h-8 w-8 text-primary" />
            </div>
            <div>
              <CardTitle className="text-lg">Date Range Based Report</CardTitle>
              <CardDescription className="mt-2">
                Generate a custom report for a specific period.
              </CardDescription>
            </div>
          </div>
        </CardHeader>
        <CardContent>
          <Dialog>
            <DialogTrigger asChild>
              <Button className="w-full">
                <Download className="mr-2 h-4 w-4" />
                Generate & Download
              </Button>
            </DialogTrigger>
            <DialogContent className="sm:max-w-[425px]">
              <DialogHeader>
                <DialogTitle>Generate Date Range Report</DialogTitle>
                <DialogDescription>
                  Select start date, end date, and department for your report.
                </DialogDescription>
              </DialogHeader>
              <div className="grid gap-4 py-4">
                {/* Start Date */}
                <div className="grid grid-cols-4 items-center gap-4">
                  <Label htmlFor="start-date" className="text-right">
                    Start Date
                  </Label>
                  <Popover>
                    <PopoverTrigger asChild>
                      <Button
                          id="start-date"
                          variant="outline"
                          className={cn(
                              "col-span-3 justify-start text-left font-normal",
                              !startDate && "text-muted-foreground"
                          )}
                      >
                        <CalendarIcon className="mr-2 h-4 w-4" />
                        {startDate ? format(startDate, "PPP") : <span>Pick a date</span>}
                      </Button>
                    </PopoverTrigger>
                    <PopoverContent className="w-auto p-0">
                      <Calendar
                          mode="single"
                          selected={startDate}
                          onSelect={setStartDate}
                          initialFocus
                      />
                    </PopoverContent>
                  </Popover>
                </div>
                {/* End Date */}
                <div className="grid grid-cols-4 items-center gap-4">
                  <Label htmlFor="end-date" className="text-right">
                    End Date
                  </Label>
                  <Popover>
                    <PopoverTrigger asChild>
                      <Button
                          id="end-date"
                          variant="outline"
                          className={cn(
                              "col-span-3 justify-start text-left font-normal",
                              !endDate && "text-muted-foreground"
                          )}
                      >
                        <CalendarIcon className="mr-2 h-4 w-4" />
                        {endDate ? format(endDate, "PPP") : <span>Pick a date</span>}
                      </Button>
                    </PopoverTrigger>
                    <PopoverContent className="w-auto p-0">
                      <Calendar
                          mode="single"
                          selected={endDate}
                          onSelect={setEndDate}
                          initialFocus
                      />
                    </PopoverContent>
                  </Popover>
                </div>
                {/* Department Dropdown */}
                <div className="grid grid-cols-4 items-center gap-4">
                  <Label htmlFor="department" className="text-right">
                    Department
                  </Label>
                  <Select onValueChange={(val) => setSelectedDept(val)}>
                    <SelectTrigger className="col-span-3">
                      <SelectValue placeholder="Select a department" />
                    </SelectTrigger>
                    <SelectContent>
                      {departments.map((dep) => (
                          <SelectItem key={dep} value={dep}>
                            {dep}
                          </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                </div>
              </div>
              <DialogFooter>
                <Button onClick={handleDownload}>
                  <Download className="mr-2 h-4 w-4" />
                  Generate & Download
                </Button>
              </DialogFooter>
            </DialogContent>
          </Dialog>
        </CardContent>
      </Card>
  );
}

export default function AdminReportsPage() {
  return (
      <div className="grid gap-6">
        <Card>
          <CardHeader>
            <CardTitle>Reports</CardTitle>
            <CardDescription>Generate and download various organizational reports.</CardDescription>
          </CardHeader>
        </Card>
        <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
          {reports.map((report, index) => (
              <DepartmentReportCard key={index} {...report} />
          ))}
          <DateRangeReportCard />
        </div>
      </div>
  );
}
