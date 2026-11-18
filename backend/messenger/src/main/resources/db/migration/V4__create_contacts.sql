-- Módulo: contacts
-- Una fila por relación de amistad. Sin claves foráneas hacia users (módulos desacoplados).

CREATE TABLE contacts (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    requester_id UUID        NOT NULL,
    addressee_id UUID        NOT NULL,
    status       VARCHAR(10) NOT NULL DEFAULT 'pending',
    created_at   TIMESTAMPTZ NOT NULL DEFAULT now(),
    responded_at TIMESTAMPTZ,

    CONSTRAINT contacts_status_valid CHECK (status IN ('pending', 'accepted')),
    CONSTRAINT contacts_not_self CHECK (requester_id <> addressee_id)
);

-- Un solo registro por pareja, sin importar quién la inició
CREATE UNIQUE INDEX ux_contacts_pair
    ON contacts (LEAST(requester_id, addressee_id), GREATEST(requester_id, addressee_id));

CREATE INDEX ix_contacts_addressee ON contacts (addressee_id, status);
CREATE INDEX ix_contacts_requester ON contacts (requester_id, status);