package com.dedul.finflow.app.finflowapp.document.infrastructure.persistence;

import com.dedul.finflow.app.finflowapp.document.domain.DocumentOwnerType;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoredDocumentJpaRepository extends JpaRepository<StoredDocumentEntity, UUID> {
  List<StoredDocumentEntity> findAllByOwnerIdAndOwnerType(
      UUID ownerId, DocumentOwnerType ownerType);
}
