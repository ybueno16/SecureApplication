CREATE TABLE credentials (
    id           UUID PRIMARY KEY,
    user_id      UUID           NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    site_url     VARCHAR(2048)  NOT NULL,
    username_enc BYTEA          NOT NULL,
    password_enc BYTEA          NOT NULL,
    notes_enc    BYTEA,
    tags         VARCHAR(1000)  NOT NULL DEFAULT '',
    created_at   TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMPTZ    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_credentials_user_id ON credentials (user_id);
CREATE INDEX idx_credentials_site_url ON credentials (user_id, site_url);
