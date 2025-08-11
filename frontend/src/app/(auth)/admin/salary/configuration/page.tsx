
"use client"
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { PlusCircle, Trash2, Calendar as CalendarIcon } from "lucide-react";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover";
import { Calendar } from "@/components/ui/calendar";
import { format } from "date-fns";
import React from "react";
import { useToast } from "@/hooks/use-toast";

type Earning = {
    id: number;
    name: string;
    shortCode: string;
    effectiveDate?: Date;
}

type Deduction = {
    id: number;
    name: string;
    type: 'fixed' | 'percentage';
    value: number | string;
}

function EarningComponent({ earning, onUpdate, onDelete }: { earning: Earning, onUpdate: (id: number, data: Partial<Earning>) => void, onDelete: (id: number) => void }) {
    const [date, setDate] = React.useState<Date|undefined>(earning.effectiveDate)

    const handleDateSelect = (newDate?: Date) => {
        setDate(newDate);
        onUpdate(earning.id, { effectiveDate: newDate });
    }
    
    return (
        <div className="grid grid-cols-10 gap-4 items-center">
            <div className="col-span-4">
                <Input placeholder="e.g., House Rent Allowance" defaultValue={earning.name} onChange={(e) => onUpdate(earning.id, { name: e.target.value })} />
            </div>
            <div className="col-span-2">
                <Input placeholder="e.g., HRA" defaultValue={earning.shortCode} onChange={(e) => onUpdate(earning.id, { shortCode: e.target.value })} />
            </div>
            <div className="col-span-3">
                 <Popover>
                    <PopoverTrigger asChild>
                      <Button
                        variant={"outline"}
                        className="w-full justify-start text-left font-normal"
                      >
                        <CalendarIcon className="mr-2 h-4 w-4" />
                        {date ? format(date, "PPP") : <span>Pick a date</span>}
                      </Button>
                    </PopoverTrigger>
                    <PopoverContent className="w-auto p-0" align="start">
                      <Calendar
                        mode="single"
                        selected={date}
                        onSelect={handleDateSelect}
                        initialFocus
                      />
                    </PopoverContent>
                  </Popover>
            </div>
            <div className="col-span-1 flex justify-end">
                <Button variant="ghost" size="icon" onClick={() => onDelete(earning.id)}><Trash2 className="h-4 w-4 text-destructive" /></Button>
            </div>
        </div>
    )
}

function DeductionComponent({ deduction, onUpdate, onDelete }: { deduction: Deduction, onUpdate: (id: number, data: Partial<Deduction>) => void, onDelete: (id: number) => void }) {
    return (
        <div className="grid grid-cols-10 gap-4 items-center">
            <div className="col-span-4">
                <Input placeholder="e.g., Provident Fund" defaultValue={deduction.name} onChange={(e) => onUpdate(deduction.id, { name: e.target.value })} />
            </div>
            <div className="col-span-2">
                 <Select defaultValue={deduction.type} onValueChange={(value: 'fixed' | 'percentage') => onUpdate(deduction.id, { type: value })}>
                    <SelectTrigger><SelectValue placeholder="Type" /></SelectTrigger>
                    <SelectContent>
                        <SelectItem value="fixed">Fixed</SelectItem>
                        <SelectItem value="percentage">Percentage</SelectItem>
                    </SelectContent>
                </Select>
            </div>
             <div className="col-span-3">
                <Input type="number" placeholder="Value" defaultValue={deduction.value} onChange={(e) => onUpdate(deduction.id, { value: e.target.value })} />
            </div>
            <div className="col-span-1 flex justify-end">
                <Button variant="ghost" size="icon" onClick={() => onDelete(deduction.id)}><Trash2 className="h-4 w-4 text-destructive" /></Button>
            </div>
        </div>
    )
}

export default function SalaryConfigurationPage() {
  const { toast } = useToast();
  const [earnings, setEarnings] = React.useState<Earning[]>([
    { id: 1, name: "Basic Pay", shortCode: "BASIC", effectiveDate: new Date() },
    { id: 2, name: "House Rent Allowance", shortCode: "HRA", effectiveDate: new Date() },
  ]);

  const [deductions, setDeductions] = React.useState<Deduction[]>([
    { id: 1, name: "Provident Fund", type: "percentage", value: 12 },
    { id: 2, name: "Professional Tax", type: "fixed", value: 200 },
  ]);

  const addEarning = () => {
    setEarnings([...earnings, { id: Date.now(), name: "", shortCode: "", effectiveDate: new Date() }]);
  };

  const updateEarning = (id: number, data: Partial<Earning>) => {
    setEarnings(earnings.map(e => e.id === id ? { ...e, ...data } : e));
  };
  
  const deleteEarning = (id: number) => {
      setEarnings(earnings.filter(e => e.id !== id));
  };

  const addDeduction = () => {
    setDeductions([...deductions, { id: Date.now(), name: "", type: "fixed", value: "" }]);
  };

  const updateDeduction = (id: number, data: Partial<Deduction>) => {
    setDeductions(deductions.map(d => d.id === id ? { ...d, ...data } : d));
  };

  const deleteDeduction = (id: number) => {
    setDeductions(deductions.filter(d => d.id !== id));
  };

  const handleSave = () => {
    // In a real app, you'd send the state to your backend here.
    console.log("Saving Earnings:", earnings);
    console.log("Saving Deductions:", deductions);
    toast({
        title: "Configuration Saved",
        description: "Your salary components have been saved successfully.",
    });
  }

  return (
    <div className="max-w-4xl mx-auto space-y-6">
      <Card>
        <CardHeader>
          <CardTitle>Salary Configuration</CardTitle>
          <CardDescription>
            Define the components and structure for salary calculations across the organization.
          </CardDescription>
        </CardHeader>
      </Card>

      <Tabs defaultValue="earnings">
        <TabsList className="grid w-full grid-cols-2">
            <TabsTrigger value="earnings">Earnings</TabsTrigger>
            <TabsTrigger value="deductions">Deductions</TabsTrigger>
        </TabsList>
        <TabsContent value="earnings">
             <Card>
                <CardHeader>
                    <CardTitle>Earning Components</CardTitle>
                    <CardDescription>Define all possible components that form the earnings part of a salary.</CardDescription>
                </CardHeader>
                <CardContent className="space-y-6">
                    <div className="grid grid-cols-10 gap-4">
                        <Label className="col-span-4">Component Name</Label>
                        <Label className="col-span-2">Short Code</Label>
                        <Label className="col-span-3">Effective Date</Label>
                    </div>
                    {earnings.map(earning => (
                        <EarningComponent key={earning.id} earning={earning} onUpdate={updateEarning} onDelete={deleteEarning} />
                    ))}
                    <Button variant="outline" className="w-full" onClick={addEarning}>
                        <PlusCircle className="mr-2 h-4 w-4" /> Add Earning Component
                    </Button>
                </CardContent>
            </Card>
        </TabsContent>
        <TabsContent value="deductions">
             <Card>
                <CardHeader>
                    <CardTitle>Deduction Components</CardTitle>
                    <CardDescription>Define all possible deductions from the gross salary.</CardDescription>
                </CardHeader>
                <CardContent className="space-y-6">
                    <div className="grid grid-cols-10 gap-4">
                        <Label className="col-span-4">Component Name</Label>
                        <Label className="col-span-2">Type</Label>
                        <Label className="col-span-3">Value</Label>
                    </div>
                   {deductions.map(deduction => (
                        <DeductionComponent key={deduction.id} deduction={deduction} onUpdate={updateDeduction} onDelete={deleteDeduction} />
                   ))}
                    <Button variant="outline" className="w-full" onClick={addDeduction}>
                        <PlusCircle className="mr-2 h-4 w-4" /> Add Deduction Component
                    </Button>
                </CardContent>
            </Card>
        </TabsContent>
      </Tabs>
      <div className="flex justify-end">
        <Button size="lg" onClick={handleSave}>Save Configuration</Button>
      </div>
    </div>
  );
}
