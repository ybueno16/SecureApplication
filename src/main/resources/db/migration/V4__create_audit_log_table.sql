CREATE TYPE audit_action AS ENUM (
    'LOGIN',
    'LOGOUT',
    'CREDENTIAL_CREATE',
    'CREDENTIAL_UPDATE',
    'CREDENTIAL_DELETE',
    'CREDENTIAL_REVEAL'
);

CREATE TABLE audit_log (
    id          UUID PRIMARY KEY,
    user_id     UUID         NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    action      audit_action NOT NULL,
    resource_id UUID,
    ip_address  VARCHAR(45)  NOT NULL,
    timestamp   TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_audit_log_user_id ON audit_log (user_id);
CREATE INDEX idx_audit_log_timestamp ON audit_log (timestamp);
