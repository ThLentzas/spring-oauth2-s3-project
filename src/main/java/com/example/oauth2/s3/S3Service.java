package com.example.oauth2.s3;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Duration;

import com.example.oauth2.exception.ServerErrorException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class S3Service {
    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    @Value("${aws.s3.bucket}")
    private String bucket;
    private static final Logger logger = LoggerFactory.getLogger(S3Service.class);
    private static final String SERVER_ERROR_MSG = "The server encountered an internal error and was unable to complete your request. Please try again later";

    // Exception handling: https://github.com/awsdocs/aws-doc-sdk-examples/blob/b1949eadb8097ab0d5b797f3f5a0625c6ede238c/javav2/example_code/s3/src/main/java/com/example/s3/PutObject.java
    public void upload(String objectKey, MultipartFile profileImage) {
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(objectKey)
                .build();
        try {
            // Alternative: RequestBody.fromBytes(profileImage.getBytes());
            s3Client.putObject(request, RequestBody.fromInputStream(profileImage.getInputStream(), profileImage.getSize()));
        } catch (S3Exception s3e) {
            logger.info("Failed to create object with key: {} in bucket: {}. Error: {}", objectKey, bucket, s3e.awsErrorDetails().errorMessage());
            throw new ServerErrorException(SERVER_ERROR_MSG);
        } catch (IOException ioe) {
            logger.info("Failed to create object with key: {} in bucket: {}. Error: {}", objectKey, bucket, ioe.getMessage());
            throw new ServerErrorException(SERVER_ERROR_MSG);
        }
    }

    // Exception handling: https://github.com/awsdocs/aws-doc-sdk-examples/blob/b1949eadb8097ab0d5b797f3f5a0625c6ede238c/javav2/example_code/s3/src/main/java/com/example/s3/DeleteObjects.java
    public void delete(String objectKey) {
        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(objectKey)
                .build();
        try {
            this.s3Client.deleteObject(request);
        } catch (S3Exception s3e) {
            logger.info("Failed to delete object with key: {} from bucket: {}. Error: {}", objectKey, bucket, s3e.awsErrorDetails().errorMessage());
            throw new ServerErrorException(SERVER_ERROR_MSG);
        }
    }

    // S3Presigner also needs credentials and Region set like S3Client
    // https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/examples-s3-presign.html
    public String createPreSignedGetUrl(String objectKey) {
        try {
            GetObjectRequest objectRequest = GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(objectKey)
                    .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(10))
                    .getObjectRequest(objectRequest)
                    .build();

            PresignedGetObjectRequest presignedRequest = this.s3Presigner.presignGetObject(presignRequest);
            logger.info("Presigned URL: [{}]", presignedRequest.url());
            logger.info("HTTP method: [{}]", presignedRequest.httpRequest().method());

            return presignedRequest.url().toExternalForm();
        } finally {
            this.s3Presigner.close();
        }
    }
}
