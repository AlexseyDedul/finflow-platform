package com.dedul.finflow.app.finflowapp.document.infrastructure.persistence;

import com.dedul.finflow.app.finflowapp.document.domain.DocumentOwnerType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "documents")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class StoredDocumentEntity {
  @Id private UUID id;

  @Column(name = "owner_id", nullable = false)
  private UUID ownerId;

  @Enumerated(EnumType.STRING)
  @Column(name = "owner_type", nullable = false, length = 100)
  private DocumentOwnerType ownerType;

  @Column(nullable = false, length = 255)
  private String bucket;

  @Column(name = "object_key", nullable = false, length = 1000)
  private String objectKey;

  @Column(name = "original_filename", nullable = false, length = 500)
  private String originalFilename;

  @Column(name = "content_type", nullable = false, length = 255)
  private String contentType;

  @Column(name = "size_bytes", nullable = false)
  private long sizeBytes;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;
}
