package com.edu.elearning.service.impl.video;

import com.edu.elearning.exception.ElearningException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3StorageService {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.s3.presigned-url-expiry-minutes:60}")
    private long presignedUrlExpiryMinutes;

    private static final java.util.Set<String> ALLOWED_CONTENT_TYPES = java.util.Set.of(
            "video/mp4", "video/quicktime", "video/x-matroska", "video/webm"
    );

    /**
     * Uploads a video file to S3 under videos/{moduleId}/{uuid}-{originalFilename}
     * and returns the S3 object key (NOT a public URL, since the bucket is private).
     */
    public String uploadVideo(MultipartFile file, Long moduleId) {

        if (file == null || file.isEmpty()) {
            throw new ElearningException("No file was uploaded");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new ElearningException("Unsupported video type: " + contentType);
        }

        String originalName = sanitizeFileName(file.getOriginalFilename());
        String key = "videos/" + moduleId + "/" + UUID.randomUUID() + "-" + originalName;

        try {
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(key)
                            .contentType(contentType)
                            .build(),
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize())
            );
        } catch (IOException e) {
            throw new ElearningException("Failed to upload video to S3: " + e.getMessage());
        }

        return key;
    }

    /**
     * Generates a time-limited signed URL so the frontend/video player can stream
     * a video directly from the private bucket without making it public.
     */
    public String generatePlaybackUrl(String key) {

        if (key == null || key.isBlank()) {
            return null;
        }

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(presignedUrlExpiryMinutes))
                .getObjectRequest(getObjectRequest)
                .build();

        return s3Presigner.presignGetObject(presignRequest).url().toString();
    }

    public void deleteVideo(String key) {
        if (key == null || key.isBlank()) {
            return;
        }
        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build());
    }

    private String sanitizeFileName(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            return "video";
        }
        return fileName.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}
