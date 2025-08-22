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
import { FileText, Download, PieChart, Users, Calendar as CalendarIcon, CalendarRange } from "lucide-react";
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
} from "@/components/ui/select"
import { fetchWithAuth } from "@/utils/api";

const reports = [
    { title: "Monthly Salary Statement", description: "Detailed report of all salaries paid for a selected month.", icon: <FileText className="h-8 w-8 text-primary" /> },
    { title: "Department-wise Pay", description: "Breakdown of salary expenses across different departments.", icon: <PieChart className="h-8 w-8 text-primary" /> },
    { title: "Yearly Financial Summary", description: "An overview of payroll expenses for the entire financial year.", icon: <CalendarIcon className="h-8 w-8 text-primary" /> },
];

const departments = ["All", "Administration", "Computer Science", "ECE", "EE", "CIVIL", "Mechanical", "MBA", "MCA", "Placement"];

interface EmployeeSalaryData {
  id: string;
  name: string;
  email: string;
  department: string;
  lop: number;
  otherDeductions: number;
  incomeTax: number;
}

interface MonthlyReportDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  onReportGenerated: () => void;
}

function MonthlyReportDialog({ open, onOpenChange, onReportGenerated }: MonthlyReportDialogProps) {
  const [selectedMonth, setSelectedMonth] = React.useState<Date>(() => {
    const date = new Date();
    date.setMonth(date.getMonth() - 1); // Default to previous month
    return date;
  });
  const [employees, setEmployees] = React.useState<EmployeeSalaryData[]>([]);
  const [isLoading, setIsLoading] = React.useState(false);
  const [isSubmitting, setIsSubmitting] = React.useState(false);
  const [error, setError] = React.useState<string | null>(null);

  // Fetch employees with salary data for the selected month
  const fetchEmployeeData = async () => {
    setIsLoading(true);
    setError(null);
    try {
      const year = selectedMonth.getFullYear();
      const month = selectedMonth.getMonth() + 1; // JavaScript months are 0-indexed
      
      // Format the first day of the selected month for the API query
      const firstDayOfMonth = new Date(year, month - 1, 1);
      
      const response = await fetchWithAuth(
        `${process.env.NEXT_PUBLIC_API_URL}/api/admin/is-staff-active-before?beforeDate=${firstDayOfMonth.toISOString()}`
      );
      if (!response.ok) {
        throw new Error('Failed to fetch employee data');
      }
      
      const data = await response.json();
      setEmployees(data.map((emp: any) => ({
        id: emp.id,
        name: emp.name,
        email: emp.email,
        department: emp.department || 'N/A',
        lop: 0,
        otherDeductions: 0,
        incomeTax: 0
      })));
    } catch (err) {
      console.error('Error fetching employee data:', err);
      setError('Failed to load employee data. Please try again.');
    } finally {
      setIsLoading(false);
    }
  };

  // Handle form field changes
  const handleInputChange = (id: string, field: 'lop' | 'otherDeductions' | 'incomeTax', value: number) => {
    setEmployees(employees.map(emp => 
      emp.id === id ? { ...emp, [field]: Number(value) || 0 } : emp
    ));
  };

  // Handle form submission
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsSubmitting(true);
    setError(null);
    
    try {
      const year = selectedMonth.getFullYear();
      const month = selectedMonth.getMonth() + 1;
      
      // First, update the salary transactions with LOP and deductions
      const updateResponse = await fetchWithAuth(
        `${process.env.NEXT_PUBLIC_API_URL}/api/admin/salary-transactions/process-monthly-transactions`,
        {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            year,
            month,
            employeeData: employees.map(({ id, lop, otherDeductions, incomeTax }) => ({
              employeeId: id,
              lop,
              otherDeductions,
              incomeTax
            }))
          })
        }
      );
      
      if (!updateResponse.ok) {
        throw new Error('Failed to update salary data');
      }else{
        console.log("Updated the records!");
      }
      
      // Then, trigger report generation
      const reportResponse = await fetch(
        `${process.env.NEXT_PUBLIC_API_URL}/api/admin/reports/generate-monthly?year=${year}&month=${month}`,
        { method: 'POST' }
      );
      
      if (!reportResponse.ok) {
        throw new Error('Failed to generate report');
      }
      
      // TO BE MODIFIED BY THE ACCORDING TO REPORT GENERATION LOGIC !!!!!!!!!!!!!!!!!!!!!!!!!  
      // Get the report file
      const blob = await reportResponse.blob();
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `salary-report-${year}-${month.toString().padStart(2, '0')}.pdf`;
      document.body.appendChild(a);
      a.click();
      window.URL.revokeObjectURL(url);
      a.remove();
      
      onReportGenerated();
      onOpenChange(false);
    } catch (err) {
      console.error('Error generating report:', err);
      setError('Failed to generate report. Please try again.');
    } finally {
      setIsSubmitting(false);
    }
  };

  // Load employee data when dialog opens
  React.useEffect(() => {
    if (open) {
      fetchEmployeeData();
    }
  }, [open]);

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="max-w-4xl max-h-[80vh] flex flex-col">
        <DialogHeader>
          <DialogTitle>Generate Monthly Salary Report</DialogTitle>
          <DialogDescription>
            Review and update employee salary details for the selected month.
          </DialogDescription>
        </DialogHeader>
        
        <div className="flex items-center gap-4 mb-4">
          <Label htmlFor="month" className="whitespace-nowrap">
            Select Month:
          </Label>
          <Popover>
            <PopoverTrigger asChild>
              <Button
                variant="outline"
                className="w-full justify-start text-left font-normal"
              >
                <CalendarIcon className="mr-2 h-4 w-4" />
                {format(selectedMonth, 'MMMM yyyy')}
              </Button>
            </PopoverTrigger>
            <PopoverContent className="w-auto p-0">
              <Calendar
                mode="single"
                selected={selectedMonth}
                onSelect={(date) => date && setSelectedMonth(date)}
                initialFocus
                defaultMonth={selectedMonth}
                toDate={new Date()}
                fromYear={2020}
                toYear={new Date().getFullYear()}
              />
            </PopoverContent>
          </Popover>
          <Button 
            onClick={fetchEmployeeData}
            disabled={isLoading}
            variant="outline"
            size="sm"
          >
            {isLoading ? 'Loading...' : 'Refresh'}
          </Button>
        </div>
        
        {error && (
          <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded relative" role="alert">
            <strong className="font-bold">Error: </strong>
            <span className="block sm:inline">{error}</span>
          </div>
        )}
        
        <div className="flex-1 overflow-y-auto border rounded-lg">
          <form onSubmit={handleSubmit} className="divide-y">
          <div className="grid grid-cols-12 gap-4 p-4 bg-gray-50 font-medium">
            <div className="col-span-3">Employee</div>
            <div className="col-span-2 text-left">Department</div>
            <div className="col-span-1 text-left">LOP (Days)</div>
            <div className="col-span-2 text-left">Other Deductions (₹)</div>
            <div className="col-span-2 text-left">Income Tax (₹)</div>
            <div className="col-span-2"></div>
        </div>
            
            {isLoading ? (
              <div className="flex justify-center items-center p-8">
                <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary"></div>
              </div>
            ) : employees.length === 0 ? (
              <div className="text-center p-8 text-gray-500">
                No employee data found for the selected month.
              </div>
            ) : (
              <div className="divide-y">
                {employees.map((employee) => (
                  <div key={employee.id} className="grid grid-cols-12 gap-4 p-4 items-center hover:bg-gray-50">
                    <div className="col-span-3">
                      <div className="font-medium">{employee.name}</div>
                      <div className="text-sm text-gray-500">{employee.email}</div>
                    </div>
                    <div className="col-span-2">
                      {employee.department}
                    </div>
                    <div className="col-span-1">
                      <input
                        type="number"
                        min="0"
                        max="31"
                        className="w-full p-2 border rounded text-right"
                        value={employee.lop || ''}
                        onChange={(e) => handleInputChange(employee.id, 'lop', Number(e.target.value))}
                        disabled={isSubmitting}
                      />
                    </div>
                    <div className="col-span-2">
                      <div className="relative">
                        <span className="absolute left-3 top-2">₹</span>
                        <input
                          type="number"
                          min="0"
                          className="w-full p-2 border rounded text-right pl-8"
                          value={employee.otherDeductions || ''}
                          onChange={(e) => handleInputChange(employee.id, 'otherDeductions', Number(e.target.value))}
                          disabled={isSubmitting}
                        />
                      </div>
                    </div>
                    <div className="col-span-2">
                      <div className="relative">
                        <span className="absolute left-3 top-2">₹</span>
                        <input
                          type="number"
                          min="0"
                          className="w-full p-2 border rounded text-right pl-8"
                          value={employee.incomeTax || ''}
                          onChange={(e) => handleInputChange(employee.id, 'incomeTax', Number(e.target.value))}
                          disabled={isSubmitting}
                        />
                      </div>
                    </div>
                    <div className="col-span-2 text-right">
                      <Button
                        type="button"
                        variant="outline"
                        size="sm"
                        onClick={() => {
                          // Reset values for this employee
                          handleInputChange(employee.id, 'lop', 0);
                          handleInputChange(employee.id, 'otherDeductions', 0);
                          handleInputChange(employee.id, 'incomeTax', 0);
                        }}
                        disabled={isSubmitting}
                      >
                        Reset
                      </Button>
                    </div>
                  </div>
                ))}
              </div>
            )}
            
            <div className="p-4 bg-gray-50 border-t">
              <div className="flex justify-between items-center">
                <Button
                  type="button"
                  variant="outline"
                  onClick={() => {
                    // Reset all values
                    setEmployees(employees.map(emp => ({
                      ...emp,
                      lop: 0,
                      otherDeductions: 0,
                      incomeTax: 0
                    })));
                  }}
                  disabled={isLoading || isSubmitting}
                >
                  Reset All
                </Button>
                <Button 
                  type="submit" 
                  disabled={isLoading || isSubmitting || employees.length === 0}
                >
                  {isSubmitting ? (
                    <>
                      <svg className="animate-spin -ml-1 mr-2 h-4 w-4 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                        <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                        <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                      </svg>
                      Processing...
                    </>
                  ) : (
                    <>
                      <FileText className="mr-2 h-4 w-4" />
                      Generate & Download Report
                    </>
                  )}
                </Button>
              </div>
            </div>
          </form>
        </div>
      </DialogContent>
    </Dialog>
  );
}

function DepartmentReportCard({ title, description, icon }: { title: string, description: string, icon: React.ReactNode }) {
  const [isDialogOpen, setIsDialogOpen] = React.useState(false);
  
  if (title === "Monthly Salary Statement") {
    return (
      <Card className="flex flex-col">
        <CardHeader className="flex-1">
          <div className="flex items-start gap-4">
            <div className="bg-primary/10 p-3 rounded-lg">
              {icon}
            </div>
            <div>
              <CardTitle className="text-lg">{title}</CardTitle>
              <CardDescription className="mt-2">{description}</CardDescription>
            </div>
          </div>
        </CardHeader>
        <CardContent>
          <MonthlyReportDialog 
            open={isDialogOpen} 
            onOpenChange={setIsDialogOpen}
            onReportGenerated={() => {
              // You can add a toast notification here if needed
              console.log('Report generated successfully');
            }}
          />
          <Button 
            className="w-full" 
            onClick={() => setIsDialogOpen(true)}
          >
            <Download className="mr-2 h-4 w-4" />
            Generate & Download
          </Button>
        </CardContent>
      </Card>
    );
  }
  
  // Original implementation for other report types
  return (
    <Card className="flex flex-col">
      <CardHeader className="flex-1">
        <div className="flex items-start gap-4">
          <div className="bg-primary/10 p-3 rounded-lg">
            {icon}
          </div>
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
              <DialogDescription>
                Select a department for your report.
              </DialogDescription>
            </DialogHeader>
            <div className="grid gap-4 py-4">
              <div className="grid grid-cols-4 items-center gap-4">
                <Label htmlFor="department" className="text-right">
                  Department
                </Label>
                <Select>
                  <SelectTrigger className="col-span-3">
                    <SelectValue placeholder="Select a department" />
                  </SelectTrigger>
                  <SelectContent>
                    {departments.map(dep => <SelectItem key={dep} value={dep.toLowerCase().replace(' ', '-')}>{dep}</SelectItem>)}
                  </SelectContent>
                </Select>
              </div>
            </div>
            <DialogFooter>
              <Button type="submit">
                <Download className="mr-2 h-4 w-4" />
                Generate & Download
              </Button>
            </DialogFooter>
          </DialogContent>
        </Dialog>
      </CardContent>
    </Card>
  )
}

function DateRangeReportCard() {
    const [startDate, setStartDate] = React.useState<Date | undefined>();
    const [endDate, setEndDate] = React.useState<Date | undefined>();

    return (
        <Card className="flex flex-col">
            <CardHeader className="flex-1">
                <div className="flex items-start gap-4">
                    <div className="bg-primary/10 p-3 rounded-lg">
                        <CalendarRange className="h-8 w-8 text-primary" />
                    </div>
                    <div>
                        <CardTitle className="text-lg">Date Range Based Report</CardTitle>
                        <CardDescription className="mt-2">Generate a custom report for a specific period.</CardDescription>
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
                                Select a start and end date for your report.
                            </DialogDescription>
                        </DialogHeader>
                        <div className="grid gap-4 py-4">
                            <div className="grid grid-cols-4 items-center gap-4">
                                <Label htmlFor="start-date" className="text-right">
                                    Start Date
                                </Label>
                                <Popover>
                                    <PopoverTrigger asChild>
                                        <Button
                                            id="start-date"
                                            variant={"outline"}
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
                            <div className="grid grid-cols-4 items-center gap-4">
                                <Label htmlFor="end-date" className="text-right">
                                    End Date
                                </Label>
                                 <Popover>
                                    <PopoverTrigger asChild>
                                        <Button
                                            id="end-date"
                                            variant={"outline"}
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
                        </div>
                        <DialogFooter>
                            <Button type="submit">
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
