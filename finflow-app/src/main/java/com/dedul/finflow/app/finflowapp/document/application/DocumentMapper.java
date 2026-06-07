package com.dedul.finflow.app.finflowapp.document.application;

import com.dedul.finflow.app.finflowapp.document.api.dto.DocumentResponse;
import com.dedul.finflow.app.finflowapp.document.domain.StoredDocument;
import org.springframework.stereotype.Component;

@Component
public class DocumentMapper {
  public DocumentResponse toResponse(StoredDocument document) {
    return new DocumentResponse(
        document.getId(),
        document.getOwnerId(),
        document.getOwnerType(),
        document.getOriginalFilename(),
        document.getContentType(),
        document.getSizeBytes(),
        document.getCreatedAt());
  }
}
