package com.jobconnect.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailNotificationService {

    private final JavaMailSender mailSender;

    public EmailNotificationService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail(String to, String subject, String text) {
        if (to == null || to.isBlank()) {
            System.out.println("[EMAIL DUMMY] Skipped (no email). Subject: " + subject);
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);

            System.out.println("[EMAIL SENT] To: " + to + " | Subject: " + subject);
        } catch (Exception e) {
            System.out.println("[EMAIL ERROR] " + e.getMessage());
        }
    }
}
