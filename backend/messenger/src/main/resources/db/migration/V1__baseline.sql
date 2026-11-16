CREATE TABLE app_info (
    key        VARCHAR(50) PRIMARY KEY,
    value      VARCHAR(255) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

INSERT INTO app_info (key, value) VALUES ('schema_version_note', 'baseline');