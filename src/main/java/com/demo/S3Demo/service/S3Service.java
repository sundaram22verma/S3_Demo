package com.demo.S3Demo.service;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.*;

import java.io.File;
import java.net.URL;
import java.time.Duration;

@Service
public class S3Service {

    private final String bucketName = "BUCKET_NAME_HERE"; // Replace with your bucket name
    private final Region region = Region.US_EAST_1;

    private final S3Client s3 = S3Client.builder()
            .region(region)
            .credentialsProvider(ProfileCredentialsProvider.create())
            .build();

    public String uploadFile(String keyName, String filePath) {
        File file = new File(filePath);
        if(!file.exists()){
            throw new RuntimeException("File not found: " + filePath);
        }

        // Uploading the file
        s3.putObject(PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(keyName)
                        .build(),
                file.toPath());

        return "✅ File uploaded: " + keyName;
    }

    // Generate presigned URL
    public String generatePresignedUrl(String keyName, int minutes) {

        // Create a presigner for S3
        try (S3Presigner s3Presigner = S3Presigner.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build()) {

            //getting object from bucket
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(keyName)
                    .build();

            // Generating the presigned URL
            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .getObjectRequest(getObjectRequest)
                    .signatureDuration(Duration.ofMinutes(minutes))
                    .build();

            // Generate the presigned URL
            PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);
            URL url = presignedRequest.url();
            return url.toString();
        }
    }
}
