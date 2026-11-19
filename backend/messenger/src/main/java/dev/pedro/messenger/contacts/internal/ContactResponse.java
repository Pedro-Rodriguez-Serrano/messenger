package dev.pedro.messenger.contacts.internal;

import dev.pedro.messenger.identity.UserSummary;
import java.time.Instant;

/** Un amigo y desde cuándo. Respuesta de GET /contacts. */
public record ContactResponse(UserSummary user, Instant since) {}