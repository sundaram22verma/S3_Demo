package com.demo.S3Demo.controller;

import com.demo.S3Demo.service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@RestController
@RequestMapping("/s3")
public class S3Controller {

    @Autowired
    private S3Service s3Service;

    // Upload endpoint using MultipartFile
    @PostMapping("/upload")
    public String uploadFile(@RequestParam String keyName, @RequestParam("file") MultipartFile file) {
        try {
            // Save temporarily
            File tempFile = File.createTempFile("upload-", file.getOriginalFilename());
            file.transferTo(tempFile);

            // Upload to S3
            return s3Service.uploadFile(keyName, tempFile.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
            return "❌ Upload failed: " + e.getMessage();
        }
    }

    // Get presigned URL endpoint
    @GetMapping("/download-url")
    public String getPresignedUrl(@RequestParam String keyName, @RequestParam(defaultValue = "5") int minutes) {
        return s3Service.generatePresignedUrl(keyName, minutes);
    }
}
