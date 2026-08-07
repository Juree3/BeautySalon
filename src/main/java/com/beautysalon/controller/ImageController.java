package com.beautysalon.controller;

import com.google.firebase.cloud.StorageClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/images")
public class ImageController {

    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(
            @RequestParam("file") MultipartFile file
    ) {

        try {
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

            StorageClient.getInstance()
                    .bucket()
                    .create(
                            fileName,
                            file.getBytes(),
                            file.getContentType()
                    );

            String imageUrl = String.format(
                    "https://storage.googleapis.com/%s/%s",
                    StorageClient.getInstance().bucket().getName(),
                    fileName
            );

            return ResponseEntity.ok(imageUrl);

        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body("Greška kod uploada slike: " + e.getMessage());
        }
    }
}