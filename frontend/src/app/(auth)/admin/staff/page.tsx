"use client";

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
import { MoreHorizontal, PlusCircle, Upload, Search, FileDown, Calendar as CalendarIcon } from "lucide-react";
import { Input } from "@/components/ui/input";
import {
    DropdownMenu,
    DropdownMenuContent,
    DropdownMenuItem,
    DropdownMenuLabel,
    DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import {
    Select,
    SelectContent,
    SelectItem,
    SelectTrigger,
    SelectValue,
} from "@/components/ui/select";
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
import Link from "next/link";
import React from "react";
import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover";
import { Calendar } from "@/components/ui/calendar";
import { format } from "date-fns";
import { cn } from "@/lib/utils";
import { useAuth } from "@/context/auth-context";

interface ImportResult {
    successCount: number;
    errorCount: number;
    errors: string[];
}

const staffList = [
  { id: "EMP001", name: "Aarav Sharma", department: "Engineering", role: "Sr. Software Engineer", status: "Active", joinedDate: "2020-01-15", basePay: 75000, bankAccount: "1234567890", email: "aarav.sharma@example.com", mobile: "9876543210" },
  { id: "EMP002", name: "Diya Patel", department: "Product", role: "Product Manager", status: "Active", joinedDate: "2019-07-22", basePay: 90000, bankAccount: "0987654321", email: "diya.patel@example.com", mobile: "9876543211" },
  { id: "EMP003", name: "Rohan Mehta", department: "Design", role: "UX Designer", status: "Active", joinedDate: "2021-03-10", basePay: 60000, bankAccount: "1122334455", email: "rohan.mehta@example.com", mobile: "9876543212" },
  { id: "EMP004", name: "Priya Singh", department: "HR", role: "HR Manager", status: "Active", joinedDate: "2018-11-05", basePay: 80000, bankAccount: "2233445566", email: "priya.singh@example.com", mobile: "9876543213" },
  { id: "EMP005", name: "Vikram Kumar", department: "Engineering", role: "DevOps Engineer", status: "Relieved", joinedDate: "2020-05-20", basePay: 70000, bankAccount: "3344556677", email: "vikram.kumar@example.com", mobile: "9876543214" },
  { id: "EMP006", name: "Ananya Gupta", department: "Marketing", role: "Digital Marketer", status: "Active", joinedDate: "2022-08-01", basePay: 55000, bankAccount: "4455667788", email: "ananya.gupta@example.com", mobile: "9876543215" },
];

type Staff = typeof staffList[0];

const statusVariantMap: { [key: string]: "default" | "secondary" | "destructive" | "outline" } = {
    Active: "default",
    Relieved: "destructive",
}

const departments = ["Engineering", "Product", "Design", "HR", "Marketing", "Administration", "Computer Science", "ECE", "EE", "CIVIL", "Mechanical", "MBA", "MCA", "Placement"];
const statuses = ["Active", "Relieved"];

function EditStaffDialog({ staff, onUpdate }: { staff: Staff, onUpdate: (updatedStaff: Staff) => void }) {
    const [joinedDate, setJoinedDate] = React.useState<Date | undefined>(new Date(staff.joinedDate));

    const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        const formData = new FormData(e.currentTarget);
        const updatedStaff = {
            ...staff,
            name: formData.get("name") as string,
            email: formData.get("email") as string,
            mobile: formData.get("mobile") as string,
            department: formData.get("department") as string,
            role: formData.get("designation") as string,
            basePay: Number(formData.get("base-pay")),
            bankAccount: formData.get("bank-account") as string,
            joinedDate: joinedDate ? format(joinedDate, "yyyy-MM-dd") : staff.joinedDate,
        };
        onUpdate(updatedStaff);
    };

    return (
        <Dialog>
            <DialogTrigger asChild>
                <DropdownMenuItem onSelect={(e) => e.preventDefault()}>Edit</DropdownMenuItem>
            </DialogTrigger>
            <DialogContent className="sm:max-w-[480px]">
                <form onSubmit={handleSubmit}>
                    <DialogHeader>
                        <DialogTitle>Edit Staff Member</DialogTitle>
                        <DialogDescription>
                            Update the details for {staff.name}.
                        </DialogDescription>
                    </DialogHeader>
                    <div className="grid gap-4 py-4">
                         <div className="grid grid-cols-4 items-center gap-4">
                            <Label htmlFor="staff-id" className="text-right">Staff ID</Label>
                            <Input id="staff-id" defaultValue={staff.id} className="col-span-3" disabled />
                        </div>
                        <div className="grid grid-cols-4 items-center gap-4">
                            <Label htmlFor="name" className="text-right">Full Name</Label>
                            <Input id="name" name="name" defaultValue={staff.name} className="col-span-3" />
                        </div>
                         <div className="grid grid-cols-4 items-center gap-4">
                            <Label htmlFor="email" className="text-right">Email</Label>
                            <Input id="email" name="email" type="email" defaultValue={staff.email} className="col-span-3" />
                        </div>
                         <div className="grid grid-cols-4 items-center gap-4">
                            <Label htmlFor="mobile" className="text-right">Mobile Number</Label>
                            <Input id="mobile" name="mobile" defaultValue={staff.mobile} className="col-span-3" />
                        </div>
                        <div className="grid grid-cols-4 items-center gap-4">
                            <Label htmlFor="department" className="text-right">Department</Label>
                            <Select name="department" defaultValue={staff.department}>
                                <SelectTrigger className="col-span-3">
                                    <SelectValue placeholder="Select a department" />
                                </SelectTrigger>
                                <SelectContent>
                                    {departments.map(dep => <SelectItem key={dep} value={dep}>{dep}</SelectItem>)}
                                </SelectContent>
                            </Select>
                        </div>
                         <div className="grid grid-cols-4 items-center gap-4">
                            <Label htmlFor="designation" className="text-right">Designation</Label>
                            <Input id="designation" name="designation" defaultValue={staff.role} className="col-span-3" />
                        </div>
                         <div className="grid grid-cols-4 items-center gap-4">
                            <Label htmlFor="joined-date" className="text-right">Joined Date</Label>
                            <Popover>
                                <PopoverTrigger asChild>
                                    <Button
                                        id="joined-date"
                                        variant={"outline"}
                                        className={cn(
                                            "col-span-3 justify-start text-left font-normal",
                                            !joinedDate && "text-muted-foreground"
                                        )}
                                    >
                                        <CalendarIcon className="mr-2 h-4 w-4" />
                                        {joinedDate ? format(joinedDate, "PPP") : <span>Pick a date</span>}
                                    </Button>
                                </PopoverTrigger>
                                <PopoverContent className="w-auto p-0">
                                    <Calendar
                                        mode="single"
                                        selected={joinedDate}
                                        onSelect={setJoinedDate}
                                        initialFocus
                                    />
                                </PopoverContent>
                            </Popover>
                        </div>
                         <div className="grid grid-cols-4 items-center gap-4">
                            <Label htmlFor="base-pay" className="text-right">Base Pay</Label>
                            <Input id="base-pay" name="base-pay" type="number" defaultValue={staff.basePay} className="col-span-3" />
                        </div>
                         <div className="grid grid-cols-4 items-center gap-4">
                            <Label htmlFor="bank-account" className="text-right">Bank Account No.</Label>
                            <Input id="bank-account" name="bank-account" defaultValue={staff.bankAccount} className="col-span-3" />
                        </div>
                    </div>
                    <DialogFooter>
                        <Button type="submit">Save Changes</Button>
                    </DialogFooter>
                </form>
            </DialogContent>
        </Dialog>
    )
}

function ChangeStatusDialog({ staff, onUpdate }: { staff: Staff, onUpdate: (updatedStaff: Staff) => void }) {
    const [newStatus, setNewStatus] = React.useState(staff.status);
    return (
        <Dialog>
            <DialogTrigger asChild>
                <DropdownMenuItem onSelect={(e) => e.preventDefault()}>Change Status</DropdownMenuItem>
            </DialogTrigger>
             <DialogContent className="sm:max-w-[425px]">
                <DialogHeader>
                    <DialogTitle>Change Status for {staff.name}</DialogTitle>
                    <DialogDescription>
                        Select a new status for this staff member.
                    </DialogDescription>
                </DialogHeader>
                <div className="grid gap-4 py-4">
                     <div className="grid grid-cols-4 items-center gap-4">
                        <Label htmlFor="status" className="text-right">
                            Status
                        </Label>
                        <Select onValueChange={setNewStatus} defaultValue={staff.status}>
                            <SelectTrigger className="col-span-3">
                                <SelectValue placeholder="Select a status" />
                            </SelectTrigger>
                            <SelectContent>
                                {statuses.map(s => <SelectItem key={s} value={s}>{s}</SelectItem>)}
                            </SelectContent>
                        </Select>
                    </div>
                </div>
                <DialogFooter>
                    <Button onClick={() => onUpdate({...staff, status: newStatus})}>Update Status</Button>
                </DialogFooter>
            </DialogContent>
        </Dialog>
    )
}

function ViewHistoryDialog({ staff }: { staff: Staff }) {
    return (
        <Dialog>
            <DialogTrigger asChild>
                <DropdownMenuItem onSelect={(e) => e.preventDefault()}>View History</DropdownMenuItem>
            </DialogTrigger>
             <DialogContent className="sm:max-w-[425px]">
                <DialogHeader>
                    <DialogTitle>History for {staff.name}</DialogTitle>
                    <DialogDescription>
                       Showing action history for this staff member.
                    </DialogDescription>
                </DialogHeader>
                <div className="py-4">
                    <p className="text-sm text-muted-foreground">History functionality is not yet implemented.</p>
                </div>
                <DialogFooter>
                    <DialogTrigger asChild><Button variant="outline">Close</Button></DialogTrigger>
                </DialogFooter>
            </DialogContent>
        </Dialog>
    )
}

export default function StaffManagementPage() {
    const [joinedDate, setJoinedDate] = React.useState<Date>()
    const [masterStaffList] = React.useState(staffList);
    const [filteredStaff, setFilteredStaff] = React.useState(staffList);
    const [searchQuery, setSearchQuery] = React.useState("");
    const [statusFilter, setStatusFilter] = React.useState("all");
    const [file, setFile] = React.useState<File | null>(null);
    const [importResult, setImportResult] = React.useState<ImportResult | null>(null);
    const [isImporting, setIsImporting] = React.useState(false);
    const [importDialogOpen, setImportDialogOpen] = React.useState(false);
    const [importDialogTitle, setImportDialogTitle] = React.useState("");
    const [importDialogMessage, setImportDialogMessage] = React.useState<string | React.ReactNode>("");
    const { getToken } = useAuth();

    const handleFileChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        if (event.target.files && event.target.files.length > 0) {
            setFile(event.target.files[0]);
        }
    };
    
    const handleUpdateStaff = (updatedStaff: Staff) => {
        const newMasterList = masterStaffList.map(s => s.id === updatedStaff.id ? updatedStaff : s);
        // In a real app, you'd update masterStaffList state here, e.g. `setMasterStaffList(newMasterList)`
        // For now, we'll just update the filtered list to show the change
        const newFilteredList = filteredStaff.map(s => s.id === updatedStaff.id ? updatedStaff : s);
        setFilteredStaff(newFilteredList);
    }
    const handleBulkImport = async (e: React.FormEvent) => {
        e.preventDefault();
        
        if (!file) {
            setImportDialogTitle("Error");
            setImportDialogMessage("Please select a file to upload");
            setImportDialogOpen(true);
            return;
        }
    
        const token = getToken();
        if (!token) {
            setImportDialogTitle("Authentication Required");
            setImportDialogMessage("Please log in again to continue");
            setImportDialogOpen(true);
            return;
        }
    
        const formData = new FormData();
        formData.append('file', file);
        setIsImporting(true);
        setImportResult(null);
    
        try {
            const response = await fetch('http://localhost:8080/api/staff/import', {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${token}`,
                },
                body: formData,
            });
    
            const result = await response.json();
            
            // Close the import dialog first
            const dialog = document.getElementById('import-dialog')?.closest('[role="dialog"]') as HTMLElement;
            if (dialog) {
                const closeButton = dialog.querySelector('button[data-radix-dropdown-menu-trigger]') as HTMLButtonElement;
                closeButton?.click();
            }
    
            // Then show the result
            if (!response.ok) {
                const errorMessage = result.message || 'Failed to import staff';
                const errorDetails = result.errors ? (
                    <div className="mt-2 text-sm">
                        <p>{errorMessage}</p>
                        <div className="mt-2 p-2 bg-red-50 rounded-md">
                            <ul className="list-disc pl-5 space-y-1">
                                {result.errors.map((error: string, index: number) => (
                                    <li key={index}>{error}</li>
                                ))}
                            </ul>
                        </div>
                    </div>
                ) : null;
                
                setImportDialogTitle("Import Failed");
                setImportDialogMessage(errorDetails || errorMessage);
                setImportDialogOpen(true);
                return;
            }
    
            setImportResult(result);
            if (result.errorCount > 0) {
                const message = (
                    <div className="space-y-2">
                        <p>Successfully imported {result.successCount} staff members.</p>
                        <div className="mt-2 p-2 bg-yellow-50 rounded-md">
                            <p className="font-medium">Encountered {result.errorCount} error(s):</p>
                            <ul className="list-disc pl-5 mt-1 space-y-1">
                                {result.errors.map((error: string, index: number) => (
                                    <li key={index}>{error}</li>
                                ))}
                            </ul>
                        </div>
                    </div>
                );
                setImportDialogTitle("Import Completed with Warnings");
                setImportDialogMessage(message);
            } else {
                setImportDialogTitle("Import Successful");
                setImportDialogMessage(`Successfully imported ${result.successCount} staff members.`);
            }
            setImportDialogOpen(true);
            
        } catch (error) {
            console.error('Error importing staff:', error);
            setImportDialogTitle("Error");
            setImportDialogMessage(`Failed to import staff: ${error instanceof Error ? error.message : 'Unknown error'}`);
            setImportDialogOpen(true);
        } finally {
            setIsImporting(false);
            setFile(null);
        }
    };

    React.useEffect(() => {
        let newFilteredStaff = masterStaffList;

        if (statusFilter !== "all") {
            newFilteredStaff = newFilteredStaff.filter(
                (staff) => staff.status.toLowerCase() === statusFilter
            );
        }

        if (searchQuery) {
            newFilteredStaff = newFilteredStaff.filter(
                (staff) =>
                    staff.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
                    staff.id.toLowerCase().includes(searchQuery.toLowerCase())
            );
        }

        setFilteredStaff(newFilteredStaff);
    }, [searchQuery, statusFilter, masterStaffList]);


  return (
    <Card>
      <CardHeader>
        <CardTitle>Staff Management</CardTitle>
        <CardDescription>
          View, add, and manage staff members in your organization.
        </CardDescription>
      </CardHeader>
      <CardContent>
        <div className="flex flex-col sm:flex-row items-center justify-between gap-4 mb-6">
            <div className="relative w-full sm:max-w-xs">
                <Search className="absolute left-2.5 top-2.5 h-4 w-4 text-muted-foreground" />
                <Input 
                    placeholder="Search by name or ID..." 
                    className="pl-8" 
                    value={searchQuery}
                    onChange={(e) => setSearchQuery(e.target.value)}
                />
            </div>
            <div className="flex items-center gap-2 w-full sm:w-auto">
                <Select value={statusFilter} onValueChange={setStatusFilter}>
                    <SelectTrigger className="w-full sm:w-[180px]">
                        <SelectValue placeholder="Filter by status" />
                    </SelectTrigger>
                    <SelectContent>
                        <SelectItem value="all">All Statuses</SelectItem>
                        {statuses.map(s => <SelectItem key={s} value={s.toLowerCase()}>{s}</SelectItem>)}
                    </SelectContent>
                </Select>
                 <Dialog>
                    <DialogTrigger asChild>
                        <Button variant="outline"><Upload className="mr-2 h-4 w-4" /> Bulk Import</Button>
                    </DialogTrigger>
                    <DialogContent id="import-dialog" className="sm:max-w-[480px]">
                        <DialogHeader>
                            <DialogTitle>Bulk Import Staff</DialogTitle>
                            <DialogDescription>
                                Upload a CSV or Excel file to add multiple staff members at once.
                            </DialogDescription>
                        </DialogHeader>
                        <div className="grid gap-4 py-4">
                            <div className="grid w-full max-w-sm items-center gap-1.5">
                                <Label htmlFor="file">Upload File</Label>
                                <Input 
                                    id="file" 
                                    type="file" 
                                    accept=".csv, application/vnd.openxmlformats-officedocument.spreadsheetml.sheet, application/vnd.ms-excel" 
                                    onChange={handleFileChange}
                                />
                            </div>
                            <div className="text-sm text-muted-foreground">
                                Make sure your file has columns for Staff ID, Name, Department, and Role.
                                <Button variant="link" className="p-0 h-auto ml-1">
                                    <FileDown className="mr-1 h-3 w-3" />
                                    Download sample template
                                </Button>
                            </div>
                        </div>
                        <DialogFooter>
                            <Button 
                                type="submit" 
                                className="w-full sm:w-auto"
                                onClick={async (e) => {
                                    await handleBulkImport(e);
                                }}
                            >
                                <Upload className="mr-2 h-4 w-4" />
                                Upload and Import
                            </Button>
                        </DialogFooter>
                    </DialogContent>
                </Dialog>
                {/* Import Result Dialog */}
                <Dialog open={importDialogOpen} onOpenChange={setImportDialogOpen}>
                    <DialogContent>
                        <DialogHeader>
                            <DialogTitle>{importDialogTitle}</DialogTitle>
                        </DialogHeader>
                        <div className="py-4">
                            {importDialogMessage}
                        </div>
                        <DialogFooter>
                            <Button onClick={() => setImportDialogOpen(false)}>Close</Button>
                        </DialogFooter>
                    </DialogContent>
                </Dialog>
                <Dialog>
                    <DialogTrigger asChild>
                        <Button><PlusCircle className="mr-2 h-4 w-4" /> Add Staff</Button>
                    </DialogTrigger>
                    <DialogContent className="sm:max-w-[480px]">
                        <DialogHeader>
                            <DialogTitle>Add New Staff Member</DialogTitle>
                            <DialogDescription>
                                Fill in the details below to add a new staff member.
                            </DialogDescription>
                        </DialogHeader>
                        <div className="grid gap-4 py-4">
                            <div className="grid grid-cols-4 items-center gap-4">
                                <Label htmlFor="name" className="text-right">Full Name</Label>
                                <Input id="name" className="col-span-3" />
                            </div>
                             <div className="grid grid-cols-4 items-center gap-4">
                                <Label htmlFor="email" className="text-right">Email</Label>
                                <Input id="email" type="email" className="col-span-3" />
                            </div>
                             <div className="grid grid-cols-4 items-center gap-4">
                                <Label htmlFor="mobile" className="text-right">Mobile Number</Label>
                                <Input id="mobile" className="col-span-3" />
                            </div>
                            <div className="grid grid-cols-4 items-center gap-4">
                                <Label htmlFor="department" className="text-right">Department</Label>
                                <Select>
                                    <SelectTrigger className="col-span-3">
                                        <SelectValue placeholder="Select a department" />
                                    </SelectTrigger>
                                    <SelectContent>
                                        {departments.map(dep => <SelectItem key={dep} value={dep.toLowerCase().replace(' ', '-')}>{dep}</SelectItem>)}
                                    </SelectContent>
                                </Select>
                            </div>
                             <div className="grid grid-cols-4 items-center gap-4">
                                <Label htmlFor="designation" className="text-right">Designation</Label>
                                <Input id="designation" className="col-span-3" />
                            </div>
                             <div className="grid grid-cols-4 items-center gap-4">
                                <Label htmlFor="joined-date" className="text-right">Joined Date</Label>
                                <Popover>
                                    <PopoverTrigger asChild>
                                        <Button
                                            id="joined-date"
                                            variant={"outline"}
                                            className={cn(
                                                "col-span-3 justify-start text-left font-normal",
                                                !joinedDate && "text-muted-foreground"
                                            )}
                                        >
                                            <CalendarIcon className="mr-2 h-4 w-4" />
                                            {joinedDate ? format(joinedDate, "PPP") : <span>Pick a date</span>}
                                        </Button>
                                    </PopoverTrigger>
                                    <PopoverContent className="w-auto p-0">
                                        <Calendar
                                            mode="single"
                                            selected={joinedDate}
                                            onSelect={setJoinedDate}
                                            initialFocus
                                        />
                                    </PopoverContent>
                                </Popover>
                            </div>
                             <div className="grid grid-cols-4 items-center gap-4">
                                <Label htmlFor="base-pay" className="text-right">Base Pay</Label>
                                <Input id="base-pay" type="number" className="col-span-3" />
                            </div>
                             <div className="grid grid-cols-4 items-center gap-4">
                                <Label htmlFor="bank-account" className="text-right">Bank Account No.</Label>
                                <Input id="bank-account" className="col-span-3" />
                            </div>
                        </div>
                        <DialogFooter>
                            <Button type="submit" className="w-full sm:w-auto">Add Staff Member</Button>
                        </DialogFooter>
                    </DialogContent>
                </Dialog>
            </div>
        </div>
        <div className="rounded-md border">
            <Table>
                <TableHeader>
                    <TableRow>
                        <TableHead>Staff ID</TableHead>
                        <TableHead>Name</TableHead>
                        <TableHead className="hidden md:table-cell">Department</TableHead>
                        <TableHead className="hidden lg:table-cell">Role</TableHead>
                        <TableHead>Status</TableHead>
                        <TableHead><span className="sr-only">Actions</span></TableHead>
                    </TableRow>
                </TableHeader>
                <TableBody>
                    {filteredStaff.map((staff) => (
                        <TableRow key={staff.id}>
                            <TableCell className="font-medium">{staff.id}</TableCell>
                            <TableCell>{staff.name}</TableCell>
                            <TableCell className="hidden md:table-cell">{staff.department}</TableCell>
                            <TableCell className="hidden lg:table-cell">{staff.role}</TableCell>
                            <TableCell>
                                <Badge variant={statusVariantMap[staff.status] || "outline"}>{staff.status}</Badge>
                            </TableCell>
                             <TableCell>
                                <DropdownMenu>
                                    <DropdownMenuTrigger asChild>
                                        <Button aria-haspopup="true" size="icon" variant="ghost">
                                            <MoreHorizontal className="h-4 w-4" />
                                            <span className="sr-only">Toggle menu</span>
                                        </Button>
                                    </DropdownMenuTrigger>
                                    <DropdownMenuContent align="end">
                                        <DropdownMenuLabel>Actions</DropdownMenuLabel>
                                        <EditStaffDialog staff={staff} onUpdate={handleUpdateStaff} />
                                        <ViewHistoryDialog staff={staff} />
                                        <ChangeStatusDialog staff={staff} onUpdate={handleUpdateStaff} />
                                    </DropdownMenuContent>
                                </DropdownMenu>
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