package com.dedul.finflow.app.finflowapp.document.infrastructure.storage;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Component
public class S3DocumentStorage {
  private final S3Client s3Client;
  private final String bucket;

  public S3DocumentStorage(
      S3Client s3Client, @Value("${app.aws.s3.documents-bucket}") String bucket) {
    this.s3Client = s3Client;
    this.bucket = bucket;
  }

  public StoredObject upload(
      String ownerType, UUID ownerId, String originalFilename, String contentType, byte[] content) {
    String objectKey =
        ownerType.toLowerCase()
            + "/"
            + ownerId
            + "/"
            + UUID.randomUUID()
            + "-"
            + sanitizeFilename(originalFilename);

    PutObjectRequest request =
        PutObjectRequest.builder()
            .bucket(bucket)
            .key(objectKey)
            .contentType(contentType)
            .contentLength((long) content.length)
            .build();
    s3Client.putObject(request, RequestBody.fromBytes(content));

    return new StoredObject(bucket, objectKey);
  }

  public ResponseInputStream<GetObjectResponse> download(String bucket, String objectKey) {
    return s3Client.getObject(builder -> builder.bucket(bucket).key(objectKey));
  }

  private String sanitizeFilename(String filename) {
    if (filename == null || filename.isBlank()) {
      return "file";
    }

    return filename.replaceAll("[^a-zA-Z0-9._-]", "_");
  }

  public record StoredObject(String bucket, String objectKey) {}
}
