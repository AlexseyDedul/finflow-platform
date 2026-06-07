package com.dedul.finflow.app.finflowapp.document.api.dto;

import com.dedul.finflow.app.finflowapp.document.domain.DocumentOwnerType;
import java.time.Instant;
import java.util.UUID;

public record DocumentResponse(
    UUID id,
    UUID ownerId,
    DocumentOwnerType ownerType,
    String originalFilename,
    String contentType,
    long sizeBytes,
    Instant createdAt) {}
