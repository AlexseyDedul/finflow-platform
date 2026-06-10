package com.dedul.finflow.app.finflowapp.reporting.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ReportJobRepository extends JpaRepository<ReportJobEntity, UUID> {
}
