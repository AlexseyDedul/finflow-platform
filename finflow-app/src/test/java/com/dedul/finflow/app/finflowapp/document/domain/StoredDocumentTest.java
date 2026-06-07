package com.dedul.finflow.app.finflowapp.document.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class StoredDocumentTest {

  @Test
  void shouldCreateStoredDocument() {
    StoredDocument document =
        StoredDocument.create(
            UUID.randomUUID(),
            DocumentOwnerType.EXPENSE_CLAIM,
            "finflow-documents",
            "expense_claim/test/file.txt",
            "file.txt",
            "text/plain",
            100);

    assertThat(document.getId()).isNotNull();
    assertThat(document.getOwnerType()).isEqualTo(DocumentOwnerType.EXPENSE_CLAIM);
    assertThat(document.getBucket()).isEqualTo("finflow-documents");
    assertThat(document.getObjectKey()).isEqualTo("expense_claim/test/file.txt");
    assertThat(document.getSizeBytes()).isEqualTo(100);
    assertThat(document.getCreatedAt()).isNotNull();
  }

  @Test
  void shouldRejectZeroFileSize() {
    assertThatThrownBy(
            () ->
                StoredDocument.create(
                    UUID.randomUUID(),
                    DocumentOwnerType.EXPENSE_CLAIM,
                    "finflow-documents",
                    "key",
                    "file.txt",
                    "text/plain",
                    0))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("document size must be positive");
  }

  @Test
  void shouldRejectBlankObjectKey() {
    assertThatThrownBy(
            () ->
                StoredDocument.create(
                    UUID.randomUUID(),
                    DocumentOwnerType.EXPENSE_CLAIM,
                    "finflow-documents",
                    " ",
                    "file.txt",
                    "text/plain",
                    100))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("objectKey must not be blank");
  }
}
