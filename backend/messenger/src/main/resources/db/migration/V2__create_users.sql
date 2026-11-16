-- Módulo: identity
-- Tabla de usuarios. Otros módulos referenciarán users.id SIN clave foránea.

CREATE TABLE users (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username         VARCHAR(30)  NOT NULL,
    email            VARCHAR(254) NOT NULL,
    password_hash    VARCHAR(255) NOT NULL,
    display_name     VARCHAR(50)  NOT NULL,
    personal_message VARCHAR(150),
    created_at       TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at       TIMESTAMPTZ  NOT NULL DEFAULT now(),

    -- Solo minúsculas, números, punto y guion bajo; entre 3 y 30 caracteres
    CONSTRAINT users_username_format CHECK (username ~ '^[a-z0-9_.]{3,30}$'),
    -- Comprobación mínima de que parece un email
    CONSTRAINT users_email_format CHECK (position('@' in email) > 1)
);

-- Unicidad sin distinguir mayúsculas/minúsculas
CREATE UNIQUE INDEX ux_users_username ON users (lower(username));
CREATE UNIQUE INDEX ux_users_email    ON users (lower(email));