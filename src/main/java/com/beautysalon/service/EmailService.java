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

    public void sendPasswordResetEmail(String toEmail, String token) {

        String resetLink = "http://localhost:5173/reset-password?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Zahtjev za promjenu lozinke - Beauty Salon");
        message.setText("Pozdrav,\n\nZatražili ste promjenu lozinke. Kliknite na sljedeći link da postavite novu lozinku:\n"
                + resetLink
                + "\n\nLink vrijedi 1 sat. Ako niste vi zatražili ovo, slobodno zanemarite ovaj email.");

        mailSender.send(message);
    }

    public void sendBookingConfirmedEmail(String toEmail, String bookingDate) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Rezervacija potvrđena - Beauty Salon");
        message.setText("Pozdrav,\n\nVaša rezervacija za " + bookingDate + " je potvrđena. Veselimo se vašem dolasku!\n\nBeauty Salon tim");

        mailSender.send(message);
    }

    public void sendBookingCancelledEmail(String toEmail, String bookingDate) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Rezervacija otkazana - Beauty Salon");
        message.setText("Pozdrav,\n\nVaša rezervacija za " + bookingDate + " je nažalost otkazana. Slobodno rezervirajte novi termin kad vam odgovara.\n\nBeauty Salon tim");

        mailSender.send(message);
    }
}