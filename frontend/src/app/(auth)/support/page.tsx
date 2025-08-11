
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
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Textarea } from "@/components/ui/textarea";
import { Accordion, AccordionContent, AccordionItem, AccordionTrigger } from "@/components/ui/accordion";
import { LifeBuoy, MessageSquare, BookOpen } from "lucide-react";


const faqs = [
    {
        question: "How do I download my salary slip?",
        answer: "Navigate to the 'Employee' section, then 'Salary Slips'. You will see a list of your monthly slips with a download button for each."
    },
    {
        question: "How can I update my personal information?",
        answer: "You can view your current information in the 'My Profile' section. To make changes, please use the 'Request Changes' button to submit a request to the HR department."
    },
    {
        question: "I forgot my password. How can I reset it?",
        answer: "On the login page, click the 'Forgot password?' link. You will receive an email with instructions to reset your password."
    }
]

export default function SupportPage() {
  return (
    <div className="grid md:grid-cols-3 gap-8 max-w-6xl mx-auto">
        <div className="md:col-span-2 space-y-8">
            <Card>
                <CardHeader>
                    <CardTitle>Contact Support</CardTitle>
                    <CardDescription>
                        Have a question or need help? Fill out the form below.
                    </CardDescription>
                </CardHeader>
                <CardContent className="space-y-4">
                     <div className="space-y-2">
                        <Label htmlFor="subject">Subject</Label>
                        <Input id="subject" placeholder="e.g., Issue with my payslip" />
                    </div>
                     <div className="space-y-2">
                        <Label htmlFor="message">Message</Label>
                        <Textarea id="message" rows={6} placeholder="Please describe your issue in detail..." />
                    </div>
                </CardContent>
                <CardFooter>
                    <Button>Submit Request</Button>
                </CardFooter>
            </Card>
             <Card>
                <CardHeader>
                    <CardTitle>Frequently Asked Questions (FAQ)</CardTitle>
                    <CardDescription>
                        Find answers to common questions.
                    </CardDescription>
                </CardHeader>
                <CardContent>
                     <Accordion type="single" collapsible className="w-full">
                        {faqs.map((faq, index) => (
                             <AccordionItem value={`item-${index}`} key={index}>
                                <AccordionTrigger>{faq.question}</AccordionTrigger>
                                <AccordionContent>{faq.answer}</AccordionContent>
                            </AccordionItem>
                        ))}
                    </Accordion>
                </CardContent>
            </Card>
        </div>
        <div className="space-y-6">
             <Card className="bg-muted/50">
                <CardHeader>
                    <div className="flex items-center gap-3">
                        <LifeBuoy className="h-6 w-6 text-primary" />
                        <CardTitle>Help Center</CardTitle>
                    </div>
                </CardHeader>
                <CardContent className="space-y-4 text-sm">
                    <div className="flex items-start gap-3">
                        <MessageSquare className="h-5 w-5 mt-1 text-muted-foreground"/>
                        <div>
                            <p className="font-semibold">Live Chat</p>
                            <p className="text-muted-foreground">Chat with a support agent now.</p>
                            <Button variant="link" className="p-0 h-auto">Start Chat</Button>
                        </div>
                    </div>
                     <div className="flex items-start gap-3">
                        <BookOpen className="h-5 w-5 mt-1 text-muted-foreground"/>
                        <div>
                            <p className="font-semibold">Knowledge Base</p>
                            <p className="text-muted-foreground">Browse articles and tutorials.</p>
                             <Button variant="link" className="p-0 h-auto">Visit Knowledge Base</Button>
                        </div>
                    </div>
                </CardContent>
            </Card>
        </div>
    </div>
  );
}
