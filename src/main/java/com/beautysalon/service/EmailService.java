package com.beautysalon.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class EmailService {

    @Value("${RESEND_API_KEY}")
    private String resendApiKey;

    @Value("${RESEND_FROM_EMAIL:onboarding@resend.dev}")
    private String fromEmail;

    private final RestTemplate restTemplate = new RestTemplate();

    private void sendEmail(String toEmail, String subject, String text) {

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(resendApiKey);

            Map<String, Object> body = Map.of(
                    "from", fromEmail,
                    "to", new String[]{toEmail},
                    "subject", subject,
                    "text", text
            );

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            restTemplate.postForEntity("https://api.resend.com/emails", request, String.class);

        } catch (Exception e) {
            System.out.println("RESEND GRESKA: " + e.getMessage());
        }
    }

    public void sendVerificationEmail(String toEmail, String token) {

        String verificationLink = "http://localhost:5173/verify-email?token=" + token;

        sendEmail(toEmail, "Potvrdite svoju email adresu - Beauty Salon",
                "Pozdrav,\n\nMolimo potvrdite svoju email adresu klikom na sljedeći link:\n"
                        + verificationLink
                        + "\n\nAko niste vi zatražili registraciju, slobodno zanemarite ovaj email.");
    }

    public void sendPasswordResetEmail(String toEmail, String token) {

        String resetLink = "http://localhost:5173/reset-password?token=" + token;

        sendEmail(toEmail, "Zahtjev za promjenu lozinke - Beauty Salon",
                "Pozdrav,\n\nZatražili ste promjenu lozinke. Kliknite na sljedeći link da postavite novu lozinku:\n"
                        + resetLink
                        + "\n\nLink vrijedi 1 sat. Ako niste vi zatražili ovo, slobodno zanemarite ovaj email.");
    }

    public void sendBookingConfirmedEmail(String toEmail, String bookingDate) {

        sendEmail(toEmail, "Rezervacija potvrđena - Beauty Salon",
                "Pozdrav,\n\nVaša rezervacija za " + bookingDate + " je potvrđena. Veselimo se vašem dolasku!\n\nBeauty Salon tim");
    }

    public void sendBookingCancelledEmail(String toEmail, String bookingDate) {

        sendEmail(toEmail, "Rezervacija otkazana - Beauty Salon",
                "Pozdrav,\n\nVaša rezervacija za " + bookingDate + " je nažalost otkazana. Slobodno rezervirajte novi termin kad vam odgovara.\n\nBeauty Salon tim");
    }
}