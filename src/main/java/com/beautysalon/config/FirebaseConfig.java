package com.beautysalon.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

@Configuration
public class FirebaseConfig {

    @Value("${FIREBASE_CONFIG}")
    private String firebaseConfigJson;

    @PostConstruct
    public void initialize() {

        try {

            if (FirebaseApp.getApps().isEmpty()) {

                ByteArrayInputStream serviceAccount =
                        new ByteArrayInputStream(firebaseConfigJson.getBytes(StandardCharsets.UTF_8));

                FirebaseOptions options =
                        FirebaseOptions.builder()
                                .setCredentials(
                                        GoogleCredentials.fromStream(serviceAccount)
                                )
                                .setStorageBucket(
                                        "beautysalon-aa4cf.firebasestorage.app"
                                )
                                .build();

                FirebaseApp.initializeApp(options);

                System.out.println("Firebase spojen!");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}