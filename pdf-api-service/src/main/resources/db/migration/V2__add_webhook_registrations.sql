CREATE TABLE webhook_registrations (
    id         UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    owner_id   VARCHAR(255)  NOT NULL,
    url        VARCHAR(2048) NOT NULL,
    secret     VARCHAR(512)  NOT NULL,
    active     BOOLEAN       NOT NULL DEFAULT true,
    created_at TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP     NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_webhook_registrations_owner
    ON webhook_registrations(owner_id);

CREATE TRIGGER update_webhook_registrations_updated_at
    BEFORE UPDATE ON webhook_registrations
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

ALTER TABLE processing_jobs
    DROP COLUMN webhook_url,
    DROP COLUMN webhook_status,
    DROP COLUMN webhook_attempts,
    ADD COLUMN webhook_registration_id UUID REFERENCES webhook_registrations(id),
    ADD COLUMN webhook_status          VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    ADD COLUMN webhook_attempts        INT         NOT NULL DEFAULT 0;