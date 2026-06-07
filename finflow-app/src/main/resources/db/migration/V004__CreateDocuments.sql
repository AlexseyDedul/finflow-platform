CREATE TABLE documents (
    id UUID PRIMARY KEY,
    owner_id UUID NOT NULL,
    owner_type VARCHAR(100) NOT NULL,
    bucket VARCHAR(255) NOT NULL,
    object_key VARCHAR(1000) NOT NULL,
    original_filename VARCHAR(500) NOT NULL,
    content_type VARCHAR(255) NOT NULL,
    size_bytes BIGINT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,

    CONSTRAINT chk_documents_size_positive CHECK (size_bytes > 0)
);

CREATE INDEX idx_documents_owner ON documents(owner_id, owner_type);