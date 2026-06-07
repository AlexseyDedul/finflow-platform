package com.dedul.finflow.app.finflowapp.shared.infrastructure.aws;

import java.net.URI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ObjectUtils;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;

@Configuration
public class AwsS3Config {

  @Bean
  public AwsCredentialsProvider awsCredentialsProvider(
      @Value("${app.aws.s3.access-key}") String accessKey,
      @Value("${app.aws.s3.access-secret}") String secretKey) {
    if (!ObjectUtils.isEmpty(accessKey)) {
      return StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey));
    }

    return DefaultCredentialsProvider.create();
  }

  @Bean
  public S3Client s3Client(
      AwsCredentialsProvider awsCredentialsProvider,
      @Value("${app.aws.region}") String region,
      @Value("${app.aws.s3.endpoint}") String endpoint,
      @Value("${app.aws.s3.force-path-style:false}") boolean forcePathStyle) {
    S3ClientBuilder builder =
        S3Client.builder()
            .region(Region.of(region))
            .credentialsProvider(awsCredentialsProvider)
            .forcePathStyle(forcePathStyle);

    if (!ObjectUtils.isEmpty(endpoint)) {
      builder.endpointOverride(URI.create(endpoint));
    }

    return builder.build();
  }
}
