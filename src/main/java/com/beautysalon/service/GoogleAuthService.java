package com.beautysalon.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.stereotype.Service;

import java.security.GeneralSecurityException;
import java.util.Collections;
import java.io.IOException;

@Service
public class GoogleAuthService {

    private static final String GOOGLE_CLIENT_ID = "243534754650-e59cuhah3puq3r87aev1uh02fer6nq3g.apps.googleusercontent.com";
    public GoogleUserInfo verifyToken(String idTokenString) {

        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(), GsonFactory.getDefaultInstance())
                    .setAudience(Collections.singletonList(GOOGLE_CLIENT_ID))
                    .build();

            GoogleIdToken idToken = verifier.verify(idTokenString);

            if (idToken == null) {
                throw new RuntimeException("Nevažeći Google token");
            }

            GoogleIdToken.Payload payload = idToken.getPayload();

            String email = payload.getEmail();
            String fullName = (String) payload.get("name");

            return new GoogleUserInfo(email, fullName);

        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException("Greška pri verifikaciji Google tokena");
        }
    }

    public static class GoogleUserInfo {
        private final String email;
        private final String fullName;

        public GoogleUserInfo(String email, String fullName) {
            this.email = email;
            this.fullName = fullName;
        }

        public String getEmail() {
            return email;
        }

        public String getFullName() {
            return fullName;
        }
    }
}