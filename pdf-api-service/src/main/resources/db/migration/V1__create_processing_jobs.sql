CREATE TABLE processing_jobs (
    id               UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    status           VARCHAR(50)   NOT NULL DEFAULT 'PENDING',
    file_key         VARCHAR(512)  NOT NULL,
    expected_name    VARCHAR(255)  NOT NULL,
    name_found       BOOLEAN,
    webhook_url      VARCHAR(2048) NOT NULL,
    webhook_status   VARCHAR(50)   NOT NULL DEFAULT 'PENDING',
    webhook_attempts INT           NOT NULL DEFAULT 0,
    attempts         INT           NOT NULL DEFAULT 0,
    error_message    TEXT,
    created_at       TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMP     NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_processing_jobs_status
    ON processing_jobs(status);

CREATE INDEX idx_processing_jobs_created_at
    ON processing_jobs(created_at);

CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_processing_jobs_updated_at
    BEFORE UPDATE ON processing_jobs
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();