ALTER TABLE report_jobs
ADD COLUMN result_storage_key VARCHAR(500),
ADD COLUMN result_content_type VARCHAR(100),
ADD COLUMN result_filename VARCHAR(255);