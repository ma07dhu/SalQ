import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
  CardFooter
} from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Separator } from "@/components/ui/separator";

function ProfileField({ label, value }: { label: string; value: string }) {
    return (
        <div className="grid grid-cols-3 gap-4 items-center">
            <span className="text-muted-foreground text-sm">{label}</span>
            <span className="col-span-2 font-medium">{value}</span>
        </div>
    )
}

export default function ProfilePage() {
  return (
    <div className="max-w-4xl mx-auto">
        <Card>
            <CardHeader>
                <CardTitle>My Profile</CardTitle>
                <CardDescription>
                This is your personal and employment information on record.
                </CardDescription>
            </CardHeader>
            <CardContent className="space-y-8">
                <div>
                    <h3 className="text-lg font-semibold mb-4 text-foreground">Personal Information</h3>
                     <div className="grid md:grid-cols-2 gap-x-8 gap-y-4">
                        <ProfileField label="Full Name" value="Aarav Sharma" />
                        <ProfileField label="Email" value="aarav.sharma@salq.com" />
                        <ProfileField label="Phone" value="+91 98765 43210" />
                        <ProfileField label="Date of Birth" value="15th August, 1990" />
                        <ProfileField label="Address" value="123, Tech Park, Bangalore, KA" />
                     </div>
                </div>
                <Separator />
                 <div>
                    <h3 className="text-lg font-semibold mb-4 text-foreground">Official Information</h3>
                     <div className="grid md:grid-cols-2 gap-x-8 gap-y-4">
                        <ProfileField label="Employee ID" value="EMP001" />
                        <ProfileField label="Department" value="Engineering" />
                        <ProfileField label="Role" value="Sr. Software Engineer" />
                        <ProfileField label="Date of Joining" value="1st January, 2020" />
                        <ProfileField label="Reporting Manager" value="Diya Patel" />
                     </div>
                </div>
            </CardContent>
            <CardFooter className="border-t pt-6">
                <p className="text-sm text-muted-foreground flex-1">
                    For any changes, please raise a request with HR.
                </p>
                <Button>Request Changes</Button>
            </CardFooter>
        </Card>
    </div>
  );
}
