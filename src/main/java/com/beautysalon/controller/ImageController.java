package com.beautysalon.controller;

import com.google.cloud.storage.Acl;
import com.google.cloud.storage.Blob;
import com.google.firebase.cloud.StorageClient;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/images")
public class ImageController {

    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : "";
            String fileName = UUID.randomUUID() + extension;

            Blob blob = StorageClient.getInstance()
                    .bucket()
                    .create(fileName, file.getBytes(), file.getContentType());

            // KLJUČNA LINIJA - učini fajl javno čitljivim
            blob.createAcl(Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER));

            String imageUrl = String.format(
                    "https://storage.googleapis.com/%s/%s",
                    StorageClient.getInstance().bucket().getName(),
                    fileName
            );
            return ResponseEntity.ok(imageUrl);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Greška kod uploada slike: " + e.getMessage());
        }
    }
}