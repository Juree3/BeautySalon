package com.beautysalon.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerificationEmail(String toEmail, String token) {

        String verificationLink = "http://localhost:5173/verify-email?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Potvrdite svoju email adresu - Beauty Salon");
        message.setText("Pozdrav,\n\nMolimo potvrdite svoju email adresu klikom na sljedeći link:\n"
                + verificationLink
                + "\n\nAko niste vi zatražili registraciju, slobodno zanemarite ovaj email.");

        mailSender.send(message);
    }
}