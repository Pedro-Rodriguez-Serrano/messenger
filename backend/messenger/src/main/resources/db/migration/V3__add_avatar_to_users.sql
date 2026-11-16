-- Módulo: identity
-- Añade la foto de perfil de cada usuario.
-- Se guarda solo la ubicación (URL o ruta), nunca la imagen en la BDD.
-- Es opcional: si es NULL, el cliente muestra un avatar por defecto.

ALTER TABLE users ADD COLUMN avatar_url VARCHAR(500);