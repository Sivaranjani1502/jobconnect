package com.jobconnect.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.Mockito.verify;

class EmailNotificationServiceTest {

    @Mock JavaMailSender mailSender;
    @InjectMocks EmailNotificationService emailService;

    @BeforeEach void init(){ MockitoAnnotations.openMocks(this); }

    @Test
    void sendEmail_invokesMailSender() {
        emailService.sendEmail("to@a.com", "subject", "body");
        verify(mailSender).send(ArgumentMatchers.any(SimpleMailMessage.class));
    }
}
