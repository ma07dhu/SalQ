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
import React, { useEffect, useState } from "react";
import { useToast } from "@/hooks/use-toast";
import { fetchWithAuth } from "@/utils/api";
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
} from "@/components/ui/alert-dialog";

type BaseComponent = {
  componentId: number;
  componentName: string;
  componentType: 'Earning' | 'Deduction';
  valueType: 'Fixed' | 'Percentage';
  value: number | string;
  effectiveFrom: string; // ISO date string
  effectiveTo?: string | null; // ISO date string
  isNew?: boolean;
  isModified?: boolean;
};

type Earning = BaseComponent & { componentType: 'Earning' };
type Deduction = BaseComponent & { componentType: 'Deduction' };

type ComponentType = Earning | Deduction;

interface ComponentProps<T extends ComponentType> {
  component: T;
  onUpdate: (id: number, data: Partial<T>) => void;
  onDelete: (id: number) => void;
}

function DeleteConfirmationDialog({
  isOpen,
  onConfirm,
  onCancel,
  componentName,
}: {
  isOpen: boolean;
  onConfirm: () => void;
  onCancel: () => void;
  componentName: string;
}) {
  return (
    <AlertDialog open={isOpen}>
      <AlertDialogContent>
        <AlertDialogHeader>
          <AlertDialogTitle>Are you sure?</AlertDialogTitle>
          <AlertDialogDescription>
            This will permanently delete the component "{componentName}". This action cannot be undone.
          </AlertDialogDescription>
        </AlertDialogHeader>
        <AlertDialogFooter>
          <AlertDialogCancel onClick={onCancel}>Cancel</AlertDialogCancel>
          <AlertDialogAction onClick={onConfirm} className="bg-destructive text-destructive-foreground hover:bg-destructive/90">
            Delete
          </AlertDialogAction>
        </AlertDialogFooter>
      </AlertDialogContent>
    </AlertDialog>
  );
}

function EarningComponent({ component, onUpdate, onDelete }: ComponentProps<Earning>) {
  const [date, setDate] = useState<Date | undefined>(component.effectiveFrom ? new Date(component.effectiveFrom) : undefined);
  const [showDeleteConfirm, setShowDeleteConfirm] = useState(false);

  const handleDateSelect = (newDate: Date | undefined) => {
    if (!newDate) return;
    setDate(newDate);
    onUpdate(component.componentId, { effectiveFrom: newDate.toISOString() });
  };

  return (
    <>
      <div className="grid grid-cols-12 gap-4 items-center">
        <div className="col-span-4">
          <Input 
            placeholder="e.g., House Rent Allowance" 
            value={component.componentName} 
            onChange={(e) => onUpdate(component.componentId, { componentName: e.target.value })}
            className="w-full"
          />
        </div>
        <div className="col-span-2">
          <Select 
            value={component.valueType} 
            onValueChange={(value: 'Fixed' | 'Percentage') => onUpdate(component.componentId, { valueType: value })}
          >
            <SelectTrigger className="w-full"><SelectValue placeholder="Type" /></SelectTrigger>
            <SelectContent>
              <SelectItem value="Fixed">Fixed</SelectItem>
              <SelectItem value="Percentage">Percentage</SelectItem>
            </SelectContent>
          </Select>
        </div>
        <div className="col-span-2">
          <Input 
            type="number" 
            placeholder="Value" 
            value={component.value} 
            onChange={(e) => onUpdate(component.componentId, { value: e.target.value })}
            className="w-full"
          />
        </div>
        <div className="col-span-3">
          <Popover>
            <PopoverTrigger asChild>
              <Button
                variant={"outline"}
                className="w-full justify-start text-left font-normal"
              >
                <CalendarIcon className="mr-2 h-4 w-4" />
                {date ? format(date, "PPP") : <span>Effective Date</span>}
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
        <div className="col-span-1 flex justify-center">
          <Button 
            variant="ghost" 
            size="icon" 
            className="h-8 w-8"
            onClick={() => setShowDeleteConfirm(true)}
          >
            <Trash2 className="h-4 w-4 text-destructive" />
          </Button>
        </div>
      </div>
      
      <DeleteConfirmationDialog
        isOpen={showDeleteConfirm}
        onConfirm={() => {
          onDelete(component.componentId);
          setShowDeleteConfirm(false);
        }}
        onCancel={() => setShowDeleteConfirm(false)}
        componentName={component.componentName}
      />
    </>
  )
}

function DeductionComponent({ component, onUpdate, onDelete }: ComponentProps<Deduction>) {
  const [date, setDate] = useState<Date | undefined>(component.effectiveFrom ? new Date(component.effectiveFrom) : undefined);
  const [showDeleteConfirm, setShowDeleteConfirm] = useState(false);

  const handleDateSelect = (newDate: Date | undefined) => {
    if (!newDate) return;
    setDate(newDate);
    onUpdate(component.componentId, { effectiveFrom: newDate.toISOString() });
  };

  return (
    <>
      <div className="grid grid-cols-12 gap-4 items-center">
        <div className="col-span-4">
          <Input 
            placeholder="e.g., Provident Fund" 
            value={component.componentName} 
            onChange={(e) => onUpdate(component.componentId, { componentName: e.target.value })} 
            className="w-full"
          />
        </div>
        <div className="col-span-2">
          <Select 
            value={component.valueType} 
            onValueChange={(value: 'Fixed' | 'Percentage') => onUpdate(component.componentId, { valueType: value })}
          >
            <SelectTrigger className="w-full"><SelectValue placeholder="Type" /></SelectTrigger>
            <SelectContent>
              <SelectItem value="Fixed">Fixed</SelectItem>
              <SelectItem value="Percentage">Percentage</SelectItem>
            </SelectContent>
          </Select>
        </div>
        <div className="col-span-2">
          <Input 
            type="number" 
            placeholder="Value" 
            value={component.value} 
            onChange={(e) => onUpdate(component.componentId, { value: e.target.value })} 
            className="w-full"
          />
        </div>
        <div className="col-span-3">
          <Popover>
            <PopoverTrigger asChild>
              <Button
                variant={"outline"}
                className="w-full justify-start text-left font-normal"
              >
                <CalendarIcon className="mr-2 h-4 w-4" />
                {date ? format(date, "PPP") : <span>Effective Date</span>}
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
        <div className="col-span-1 flex justify-center">
          <Button 
            variant="ghost" 
            size="icon" 
            className="h-8 w-8"
            onClick={() => setShowDeleteConfirm(true)}
          >
            <Trash2 className="h-4 w-4 text-destructive" />
          </Button>
        </div>
      </div>
      
      <DeleteConfirmationDialog
        isOpen={showDeleteConfirm}
        onConfirm={() => {
          onDelete(component.componentId);
          setShowDeleteConfirm(false);
        }}
        onCancel={() => setShowDeleteConfirm(false)}
        componentName={component.componentName}
      />
    </>
  )
}


export default function SalaryConfigurationPage() {
  const { toast } = useToast();
  const [isLoading, setIsLoading] = useState(true);
  const [hasChanges, setHasChanges] = useState(false);
  const [earnings, setEarnings] = useState<Earning[]>([]);
  const [deductions, setDeductions] = useState<Deduction[]>([]);
  const [originalData, setOriginalData] = useState<{
    earnings: Earning[];
    deductions: Deduction[];
  }>({ earnings: [], deductions: [] });

  // Fetch data from backend
  useEffect(() => {
    const fetchData = async () => {
      try {
        setIsLoading(true);
        const earningsRes = await fetchWithAuth(
          `${process.env.NEXT_PUBLIC_API_URL}/api/admin/salary-components?type=earning&effectiveTo=null`
        );
        const earningsData = await earningsRes.json();
        const deductionsRes = await fetchWithAuth(
          `${process.env.NEXT_PUBLIC_API_URL}/api/admin/salary-components?type=deduction&effectiveTo=null`
        );
        const deductionsData = await deductionsRes.json();

        const processedEarnings = earningsData.map((e: any) => ({
          ...e,
          componentType: 'earning' as const,
          isNew: false,
          isModified: false
        }));

        const processedDeductions = deductionsData.map((d: any) => ({
          ...d,
          componentType: 'deduction' as const,
          isNew: false,
          isModified: false
        }));

        setEarnings(processedEarnings);
        setDeductions(processedDeductions);
        setOriginalData({
          earnings: JSON.parse(JSON.stringify(processedEarnings)),
          deductions: JSON.parse(JSON.stringify(processedDeductions))
        });
      } catch (error) {
        console.error('Error fetching data:', error);
        toast({
          title: "Error",
          description: "Failed to load salary components. Please try again.",
          variant: "destructive",
        });
      } finally {
        setIsLoading(false);
      }
    };

    fetchData();
  }, []);

  // Check for changes whenever earnings or deductions change
  useEffect(() => {
    if (!isLoading) {
      const hasEarningsChanged = JSON.stringify(earnings) !== JSON.stringify(originalData.earnings);
      const hasDeductionsChanged = JSON.stringify(deductions) !== JSON.stringify(originalData.deductions);
      setHasChanges(hasEarningsChanged || hasDeductionsChanged);
    }
  }, [earnings, deductions, originalData, isLoading]);

  const addEarning = () => {
    const newEarning: Earning = {
      componentId: Date.now(),
      componentName: "",
      componentType: 'Earning',
      valueType: 'Fixed',
      value: '',
      effectiveFrom: new Date().toISOString(),
      isNew: true,
      isModified: true
    };
    setEarnings([...earnings, newEarning]);
  };

  const updateEarning = (id: number, data: Partial<Earning>) => {
    setEarnings(earnings.map(e => 
      e.componentId === id 
        ? { ...e, ...data, isModified: true }
        : e
    ));
  };
  
  const deleteEarning = (id: number) => {
    setEarnings(earnings.filter(e => e.componentId !== id));
  };

  const addDeduction = () => {
    const newDeduction: Deduction = {
      componentId: Date.now(),
      componentName: "",
      componentType: 'Deduction',
      valueType: 'Fixed',
      value: '',
      effectiveFrom: new Date().toISOString(),
      isNew: true,
      isModified: true
    };
    setDeductions([...deductions, newDeduction]);
  };

  const updateDeduction = (id: number, data: Partial<Deduction>) => {
    setDeductions(deductions.map(d => 
      d.componentId === id 
        ? { ...d, ...data, isModified: true }
        : d
    ));
  };

  const deleteDeduction = (id: number) => {
    setDeductions(deductions.filter(d => d.componentId !== id));
  };

  const handleSave = async () => {
    try {
      setIsLoading(true);
      
      // Prepare data for API
      const updates = [];
      
      // Process earnings
      for (const earning of earnings) {
        if (earning.isNew) {
          // New earning - POST request
          updates.push(
            fetchWithAuth(`${process.env.NEXT_PUBLIC_API_URL}/api/admin/salary-components`, {
              method: 'POST',
              headers: { 'Content-Type': 'application/json' },
              body: JSON.stringify({
                name: earning.componentName,
                type: earning.valueType,
                value: earning.value,
                componentType: 'Earning',
                effectiveFrom: earning.effectiveFrom
              })
            })
          );
        } else if (earning.isModified) {
          // Update existing earning - PUT request
          updates.push(
            fetchWithAuth(`${process.env.NEXT_PUBLIC_API_URL}/api/admin/salary-components/${earning.componentId}`, {
              method: 'PUT',
              headers: { 'Content-Type': 'application/json' },
              body: JSON.stringify({
                name: earning.componentName,
                type: earning.valueType,
                value: earning.value,
                effectiveFrom: earning.effectiveFrom
              })
            })
          );
        }
      }
      
      // Process deletions (items that were in original but not in current)
      const deletedEarnings = originalData.earnings.filter(
        oe => !earnings.some(e => e.componentId === oe.componentId)
      );
      
      for (const deleted of deletedEarnings) {
        updates.push(
          fetchWithAuth(`${process.env.NEXT_PUBLIC_API_URL}/api/admin/salary-components/${deleted.componentId}`, {
            method: 'DELETE'
          })
        );
      }

      // Process deductions (similar to earnings)
      // ... (similar implementation for deductions)

      // Wait for all API calls to complete
      await Promise.all(updates);
      
      // Refresh data
      const [earningsRes, deductionsRes] = await Promise.all([
        fetchWithAuth(`${process.env.NEXT_PUBLIC_API_URL}/api/admin/salary-components?type=earning&effectiveTo=null`),
        fetchWithAuth(`${process.env.NEXT_PUBLIC_API_URL}/api/admin/salary-components?type=deduction&effectiveTo=null`)
      ]);
      
      const updatedEarnings = await earningsRes.json();
      const updatedDeductions = await deductionsRes.json();
      
      setEarnings(updatedEarnings.map((e: any) => ({
        ...e,
        componentType: 'Earning' as const,
        isNew: false,
        isModified: false
      })));
      
      setDeductions(updatedDeductions.map((d: any) => ({
        ...d,
        componentType: 'Deduction' as const,
        isNew: false,
        isModified: false
      })));
      
      setOriginalData({
        earnings: JSON.parse(JSON.stringify(updatedEarnings)),
        deductions: JSON.parse(JSON.stringify(updatedDeductions))
      });
      
      toast({
        title: "Success",
        description: "Salary components updated successfully.",
      });
      
    } catch (error) {
      console.error('Error saving data:', error);
      toast({
        title: "Error",
        description: "Failed to save changes. Please try again.",
        variant: "destructive",
      });
    } finally {
      setIsLoading(false);
    }
  };

  if (isLoading && earnings.length === 0 && deductions.length === 0) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-primary"></div>
      </div>
    );
  }

  return (
    <div className="max-w-5xl mx-auto space-y-6">
      <div className="flex justify-between items-center">
        <h1 className="text-2xl font-bold">Salary Configuration</h1>
        <Button 
          onClick={handleSave} 
          disabled={!hasChanges || isLoading}
        >
          {isLoading ? 'Saving...' : 'Save Configuration'}
        </Button>
      </div>

      <Tabs defaultValue="Earnings" className="space-y-4">
        <TabsList>
          <TabsTrigger value="Earnings">Earnings</TabsTrigger>
          <TabsTrigger value="Deductions">Deductions</TabsTrigger>
        </TabsList>
        
        <TabsContent value="Earnings">
          <Card>
            <CardHeader>
              <CardTitle>Earning Components</CardTitle>
              <CardDescription>
                Define all possible components that form the earnings part of a salary.
              </CardDescription>
            </CardHeader>
            <CardContent className="space-y-6">
              <div className="grid grid-cols-12 gap-4 font-medium">
                <Label className="col-span-4">Component Name</Label>
                <Label className="col-span-2">Type</Label>
                <Label className="col-span-2">Value</Label>
                <Label className="col-span-3">Effective Date</Label>
              </div>
              
              {earnings.map(earning => (
                <EarningComponent 
                  key={earning.componentId} 
                  component={earning} 
                  onUpdate={updateEarning} 
                  onDelete={deleteEarning} 
                />
              ))}
              
              <Button 
                variant="outline" 
                className="w-full mt-4" 
                onClick={addEarning}
                disabled={isLoading}
              >
                <PlusCircle className="mr-2 h-4 w-4" /> Add Earning Component
              </Button>
            </CardContent>
          </Card>
        </TabsContent>
        
        <TabsContent value="Deductions">
          <Card>
            <CardHeader>
              <CardTitle>Deduction Components</CardTitle>
              <CardDescription>
                Define all possible deductions from the gross salary.
              </CardDescription>
            </CardHeader>
            <CardContent className="space-y-6">
              <div className="grid grid-cols-12 gap-4 font-medium">
                <Label className="col-span-4">Component Name</Label>
                <Label className="col-span-2">Type</Label>
                <Label className="col-span-2">Value</Label>
                <Label className="col-span-3">Effective Date</Label>
              </div>
              
              {deductions.map(deduction => (
                <DeductionComponent 
                  key={deduction.componentId} 
                  component={deduction} 
                  onUpdate={updateDeduction} 
                  onDelete={deleteDeduction} 
                />
              ))}
              
              <Button 
                variant="outline" 
                className="w-full mt-4"
                onClick={addDeduction}
                disabled={isLoading}
              >
                <PlusCircle className="mr-2 h-4 w-4" /> Add Deduction Component
              </Button>
            </CardContent>
          </Card>
        </TabsContent>
      </Tabs>
    </div>
  );
}