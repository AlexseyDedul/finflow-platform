package com.dedul.finflow.app.finflowapp.shared.infrastructure.aws;

import java.net.URI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.SqsClientBuilder;

@Slf4j
@Configuration
public class AwsSqsConfig {
  @Bean
  public SqsClient sqsClient(
      @Value("${app.aws.region}") String region,
      @Value("${app.aws.sqs.endpoint}") String sqsEndpoint,
      @Value("${app.aws.sqs.access-key}") String accessKey,
      @Value("${app.aws.sqs.access-secret}") String secretKey) {
    log.info(
        "Creating SQS client: region={}, endpoint={}, accessKeyPresent={}",
        region,
        sqsEndpoint,
        accessKey != null && !accessKey.isBlank());
    AwsCredentialsProvider awsCredentialsProvider =
        accessKey == null || accessKey.isBlank()
            ? DefaultCredentialsProvider.builder().build()
            : StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey));

    SqsClientBuilder builder =
        SqsClient.builder().region(Region.of(region)).credentialsProvider(awsCredentialsProvider);

    if (sqsEndpoint != null && !sqsEndpoint.isBlank()) {
      builder.endpointOverride(URI.create(sqsEndpoint));
    }

    return builder.build();
  }
}
