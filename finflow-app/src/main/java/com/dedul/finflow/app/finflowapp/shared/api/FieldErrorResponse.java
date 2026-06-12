package com.dedul.finflow.app.finflowapp.shared.api;

public record FieldErrorResponse(String field, String message, Object rejectedValue) {}
