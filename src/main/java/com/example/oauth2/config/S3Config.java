package com.example.oauth2.config;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/*
    Setting up a s3 client and credentials:

    https://www.baeldung.com/java-aws-s3
    https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/credentials-explicit.html
    Each service has each own client. DynamoDB has each own client as in the example on the docs, and so does the S3

    For region, we can set it based on what we see in our aws account
 */
@Configuration
public class S3Config {
    @Value("${aws.access.key}")
    private String awsAccessKey;
    @Value("${aws.access.secret}")
    private String awsAccessSecret;

    @Bean
    public S3Client s3Client() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(awsAccessKey, awsAccessSecret);

        return S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .region(Region.EU_NORTH_1)
                .build();
    }

    @Bean
    public S3Presigner s3Presigner() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(awsAccessKey, awsAccessSecret);

        return S3Presigner.builder()
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .region(Region.EU_NORTH_1)
                .build();
    }
}
