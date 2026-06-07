package com.dedul.finflow.app.finflowapp.document.api;

import com.dedul.finflow.app.finflowapp.document.api.dto.DocumentResponse;
import com.dedul.finflow.app.finflowapp.document.application.DocumentService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

  private final DocumentService documentService;

  @GetMapping("/{documentId}")
  public DocumentResponse getById(@PathVariable UUID documentId) {
    return documentService.getById(documentId);
  }

  @GetMapping("/{documentId}/download")
  public ResponseEntity<?> download(@PathVariable UUID documentId) {
    var document = documentService.download(documentId);

    return ResponseEntity.ok()
        .contentType(MediaType.parseMediaType(document.contentType()))
        .header(
            HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + document.filename() + "\"")
        .contentLength(document.sizeBytes())
        .body(document.resource());
  }
}
