package com.beautysalon.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void initialize() {

        try {

            if (FirebaseApp.getApps().isEmpty()) {

                FileInputStream serviceAccount =
                        new FileInputStream(
                                "src/main/resources/firebase/firebase-key.json"
                        );

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