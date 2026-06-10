package com.dedul.finflow.app.finflowapp.reporting.infrastructure.storage;

import com.dedul.finflow.app.finflowapp.reporting.application.ReportStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Component
@RequiredArgsConstructor
public class S3ReportStorageService implements ReportStorageService {

  private final S3Client s3Client;

  @Value("${app.aws.s3.documents-bucket}")
  private String bucket;

  @Override
  public void upload(String key, String contentType, byte[] content) {
    s3Client.putObject(
        PutObjectRequest.builder()
            .bucket(bucket)
            .key(key)
            .contentType(contentType)
            .contentLength((long) content.length)
            .build(),
        RequestBody.fromBytes(content)
    );
  }

  @Override
  public byte[] download(String key) {
    ResponseBytes<GetObjectResponse> response = s3Client.getObjectAsBytes(
        GetObjectRequest.builder()
            .bucket(bucket)
            .key(key)
            .build()
    );

    return response.asByteArray();
  }
}
