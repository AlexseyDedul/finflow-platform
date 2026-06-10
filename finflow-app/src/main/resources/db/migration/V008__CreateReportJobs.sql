CREATE TABLE report_jobs (
     id UUID PRIMARY KEY,
     report_type VARCHAR(100) NOT NULL,
     status VARCHAR(50) NOT NULL,
     requested_month VARCHAR(7) NOT NULL,
     result_content TEXT,
     error_message TEXT,
     created_at TIMESTAMP WITH TIME ZONE NOT NULL,
     started_at TIMESTAMP WITH TIME ZONE,
     completed_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX idx_report_jobs_status_created_at
    ON report_jobs(status, created_at);