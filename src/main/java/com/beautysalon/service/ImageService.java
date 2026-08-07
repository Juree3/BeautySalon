package com.beautysalon.service;

import com.google.cloud.storage.Blob;
import com.google.firebase.cloud.StorageClient;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class ImageService {

    public String uploadImage(MultipartFile file) throws IOException {

        String fileName = UUID.randomUUID()
                + "-"
                + file.getOriginalFilename();

        Blob blob = StorageClient
                .getInstance()
                .bucket()
                .create(
                        fileName,
                        file.getBytes(),
                        file.getContentType()
                );

        return String.format(
                "https://storage.googleapis.com/%s/%s",
                blob.getBucket(),
                blob.getName()
        );
    }
}