package com.salq.backend.common.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender; // Injected Spring Boot mail sender

    /**
     * Send welcome email to staff with default credentials
     *
     * @param toEmail Staff email
     * @param staffName Staff name
     * @param defaultPassword Default password
     */
    public void sendWelcomeEmail(String toEmail, String staffName, String defaultPassword) {
        String subject = "Welcome to Company! Your Account Credentials";
        String message = String.format("""
                Hello %s,
                Your staff account has been created successfully.
                Here are your login credentials:
                Email: %s
                Password: %s
                Please login and change your password immediately.

                Best regards,
                Admin Team""",
                staffName, toEmail, defaultPassword);

        sendEmail(toEmail, subject, message);
    }

    /**
     * Common method to send an email
     */
    private void sendEmail(String toEmail, String subject, String message) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(toEmail);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);
        mailSender.send(mailMessage);
    }
}
