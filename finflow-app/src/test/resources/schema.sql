CREATE TABLE IF NOT EXISTS outbox_events (
     id UUID PRIMARY KEY,
     aggregate_id UUID NOT NULL,
     event_type VARCHAR(100) NOT NULL,
    payload TEXT NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    published_at TIMESTAMP WITH TIME ZONE,
    retry_count INTEGER NOT NULL DEFAULT 0,
    last_error TEXT,
    processing_started_at TIMESTAMP WITH TIME ZONE,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS report_jobs (
   id UUID PRIMARY KEY,
   report_type VARCHAR(100) NOT NULL,
    status VARCHAR(50) NOT NULL,
    requested_month VARCHAR(7) NOT NULL,
    result_content TEXT,
    error_message TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    started_at TIMESTAMP WITH TIME ZONE,
    completed_at TIMESTAMP WITH TIME ZONE,
                               result_storage_key VARCHAR(500),
    result_content_type VARCHAR(100),
    result_filename VARCHAR(255),
    version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS processed_events (
    event_id UUID PRIMARY KEY,
    event_type VARCHAR(100) NOT NULL,
    processed_at TIMESTAMP WITH TIME ZONE NOT NULL
);