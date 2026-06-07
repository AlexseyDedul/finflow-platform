package com.dedul.finflow.app.finflowapp.document.domain;

import java.time.Instant;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class StoredDocument {
  private final UUID id;
  private final UUID ownerId;
  private final DocumentOwnerType ownerType;
  private final String bucket;
  private final String objectKey;
  private final String originalFilename;
  private final String contentType;
  private final long sizeBytes;
  private final Instant createdAt;

  public static StoredDocument create(
      UUID ownerId,
      DocumentOwnerType ownerType,
      String bucket,
      String objectKey,
      String originalFilename,
      String contentType,
      long sizeBytes) {
    validate(ownerId, ownerType, bucket, objectKey, originalFilename, contentType, sizeBytes);

    return new StoredDocument(
        UUID.randomUUID(),
        ownerId,
        ownerType,
        bucket.trim(),
        objectKey.trim(),
        originalFilename.trim(),
        contentType.trim(),
        sizeBytes,
        Instant.now());
  }

  public static StoredDocument restore(
      UUID id,
      UUID ownerId,
      DocumentOwnerType ownerType,
      String bucket,
      String objectKey,
      String originalFilename,
      String contentType,
      long sizeBytes,
      Instant createdAt) {

    if (id == null) {
      throw new IllegalArgumentException("id must not be null");
    }
    if (createdAt == null) {
      throw new IllegalArgumentException("createdAt must not be null");
    }

    validate(ownerId, ownerType, bucket, objectKey, originalFilename, contentType, sizeBytes);

    return new StoredDocument(
        id,
        ownerId,
        ownerType,
        bucket.trim(),
        objectKey.trim(),
        originalFilename.trim(),
        contentType.trim(),
        sizeBytes,
        createdAt);
  }

  private static void validate(
      UUID ownerId,
      DocumentOwnerType ownerType,
      String bucket,
      String objectKey,
      String originalFilename,
      String contentType,
      long sizeBytes) {

    if (ownerId == null) {
      throw new IllegalArgumentException("ownerId must not be null");
    }

    if (ownerType == null) {
      throw new IllegalArgumentException("ownerType must not be null");
    }
    requireText(bucket, "bucket");
    requireText(objectKey, "objectKey");
    requireText(originalFilename, "originalFilename");
    requireText(contentType, "contentType");

    if (sizeBytes <= 0) {
      throw new IllegalArgumentException("document size must be positive");
    }
  }

  private static void requireText(String value, String field) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException(field + " must not be blank");
    }
  }
}
