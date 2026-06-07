package com.dedul.finflow.app.finflowapp.document.application;

import com.dedul.finflow.app.finflowapp.document.api.dto.DocumentResponse;
import com.dedul.finflow.app.finflowapp.document.domain.DocumentOwnerType;
import com.dedul.finflow.app.finflowapp.document.domain.StoredDocument;
import com.dedul.finflow.app.finflowapp.document.infrastructure.persistence.StoredDocumentRepository;
import com.dedul.finflow.app.finflowapp.document.infrastructure.storage.S3DocumentStorage;
import com.dedul.finflow.app.finflowapp.shared.exception.BusinessRuleViolationException;
import com.dedul.finflow.app.finflowapp.shared.exception.NotFoundException;
import java.io.IOException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

@Service
@RequiredArgsConstructor
public class DocumentService {

  private static final long MAX_FILE_SIZE_BYTES = 10L * 1024L * 1024L;

  private final S3DocumentStorage storage;
  private final StoredDocumentRepository repository;
  private final DocumentMapper mapper;

  @Transactional
  public DocumentResponse upload(UUID ownerId, DocumentOwnerType ownerType, MultipartFile file) {
    validateFile(file);

    try {
      String originalFilename = file.getOriginalFilename();
      String contentType = file.getContentType();
      byte[] content = file.getBytes();

      var storedObject =
          storage.upload(ownerType.name(), ownerId, originalFilename, contentType, content);

      StoredDocument document =
          StoredDocument.create(
              ownerId,
              ownerType,
              storedObject.bucket(),
              storedObject.objectKey(),
              originalFilename,
              contentType,
              content.length);

      return mapper.toResponse(repository.save(document));
    } catch (IOException e) {
      throw new IllegalStateException("Failed to read uploaded file", e);
    }
  }

  @Transactional(readOnly = true)
  public DocumentResponse getById(UUID documentId) {
    return repository
        .findById(documentId)
        .map(mapper::toResponse)
        .orElseThrow(() -> new NotFoundException("Document not found: " + documentId));
  }

  @Transactional(readOnly = true)
  public DownloadedDocument download(UUID documentId) {
    StoredDocument document =
        repository
            .findById(documentId)
            .orElseThrow(() -> new NotFoundException("Document not found: " + documentId));

    ResponseInputStream<GetObjectResponse> stream =
        storage.download(document.getBucket(), document.getObjectKey());

    return new DownloadedDocument(
        document.getOriginalFilename(),
        document.getContentType(),
        document.getSizeBytes(),
        new InputStreamResource(stream));
  }

  private void validateFile(MultipartFile file) {
    if (file == null || file.isEmpty()) {
      throw new BusinessRuleViolationException("Uploaded file must not be empty");
    }

    if (file.getSize() > MAX_FILE_SIZE_BYTES) {
      throw new BusinessRuleViolationException("Uploaded file must not exceed 10MB");
    }

    if (file.getOriginalFilename() == null || file.getOriginalFilename().isBlank()) {
      throw new BusinessRuleViolationException("Original filename must not be blank");
    }

    if (file.getContentType() == null || file.getContentType().isBlank()) {
      throw new BusinessRuleViolationException("Content type must not be blank");
    }
  }

  public record DownloadedDocument(
      String filename, String contentType, long sizeBytes, InputStreamResource resource) {}
}
