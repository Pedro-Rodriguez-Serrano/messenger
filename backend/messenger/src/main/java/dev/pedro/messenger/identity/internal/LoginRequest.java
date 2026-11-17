package dev.pedro.messenger.identity.internal;

import jakarta.validation.constraints.NotBlank;

/** Cuerpo de POST /api/v1/auth/login. */
public record LoginRequest(@NotBlank String identifier, @NotBlank String password) {}