CREATE TABLE users (
    id             UUID PRIMARY KEY,
    email          VARCHAR(255) NOT NULL UNIQUE,
    password_hash  VARCHAR(255) NOT NULL,
    kdf_salt       BYTEA        NOT NULL,
    failed_attempts INTEGER     NOT NULL DEFAULT 0,
    locked_until   TIMESTAMPTZ
);

CREATE INDEX idx_users_email ON users (email);
