package com.dedul.finflow.app.finflowapp.document.infrastructure.persistence;

import com.dedul.finflow.app.finflowapp.document.domain.DocumentOwnerType;
import com.dedul.finflow.app.finflowapp.document.domain.StoredDocument;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class StoredDocumentRepository {
  private final StoredDocumentJpaRepository jpaRepository;

  public StoredDocument save(StoredDocument document) {
    StoredDocumentEntity saved = jpaRepository.save(toEntity(document));
    return toDomain(saved);
  }

  public Optional<StoredDocument> findById(UUID id) {
    return jpaRepository.findById(id).map(this::toDomain);
  }

  public List<StoredDocument> findAllByOwner(UUID ownerId, DocumentOwnerType ownerType) {
    return jpaRepository.findAllByOwnerIdAndOwnerType(ownerId, ownerType).stream()
        .map(this::toDomain)
        .toList();
  }

  private StoredDocumentEntity toEntity(StoredDocument document) {
    return new StoredDocumentEntity(
        document.getId(),
        document.getOwnerId(),
        document.getOwnerType(),
        document.getBucket(),
        document.getObjectKey(),
        document.getOriginalFilename(),
        document.getContentType(),
        document.getSizeBytes(),
        document.getCreatedAt());
  }

  private StoredDocument toDomain(StoredDocumentEntity entity) {
    return StoredDocument.restore(
        entity.getId(),
        entity.getOwnerId(),
        entity.getOwnerType(),
        entity.getBucket(),
        entity.getObjectKey(),
        entity.getOriginalFilename(),
        entity.getContentType(),
        entity.getSizeBytes(),
        entity.getCreatedAt());
  }
}
