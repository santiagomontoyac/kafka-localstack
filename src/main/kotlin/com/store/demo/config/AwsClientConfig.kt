package com.store.demo.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.S3Configuration
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient
import software.amazon.awssdk.services.sqs.SqsClient
import java.net.URI

@Configuration
class AwsClientConfig(
    @Value("\${localstack.aws.region}") private val region: String,
    @Value("\${localstack.aws.s3.endpoint-url}") private val s3Endpoint: String,
    @Value("\${localstack.aws.s3.access-key}") private val accessKey: String,
    @Value("\${localstack.aws.s3.secret-key}") private val secretKey: String,
    @Value("\${localstack.aws.secrets-manager.endpoint-url}") private val secretsEndpoint: String
) {

    @Bean
    fun s3Client(): S3Client {
        return S3Client.builder()
            .endpointOverride(URI.create(s3Endpoint)) // should be http://localhost:4566
            .region(Region.of(region))
            .credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(accessKey, secretKey)
                )
            )
            .serviceConfiguration(
                S3Configuration.builder()
                    .pathStyleAccessEnabled(true) // ✅ Force path-style URLs
                    .build()
            )
            .build()
    }

    @Bean
    fun secretsManagerClient(): SecretsManagerClient {
        return SecretsManagerClient.builder()
            .endpointOverride(URI.create(secretsEndpoint))
            .region(Region.of(region))
            .credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(accessKey, secretKey)
                )
            )
            .build()
    }

    @Bean
    open fun sqsClient(): SqsClient {
        return SqsClient.builder()
            .endpointOverride(URI.create(s3Endpoint)) // You can reuse the same endpoint
            .region(Region.of(region))
            .credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(accessKey, secretKey)
                )
            )
            .build()
    }
}
