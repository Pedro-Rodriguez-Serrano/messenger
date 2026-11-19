package dev.pedro.messenger.contacts.internal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Cuerpo de POST /api/v1/contacts/requests. */
public record SendContactRequest(@NotBlank @Size(max = 30) String username) {}