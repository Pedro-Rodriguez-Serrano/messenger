package dev.pedro.messenger.identy.internal;

import jakarta.validation.constraints.*;

/** Cuerpo de POST /api/v1/auth/register. Reglas idénticas a las del openapi.yaml. */
public record RegisterRequest(
        @NotBlank
        @Pattern(regexp = "^[a-z0-9_.]{3,30}$",
                 message = "3-30 caracteres: minúsculas, números, punto y guion bajo")
        String username,

        @NotBlank @Email @Size(max = 254)
        String email,

        @NotBlank @Size(min = 8, max = 72, message = "debe tener entre 8 y 72 caracteres")
        String password,

        @NotBlank @Size(max = 50)
        String displayName
) {}