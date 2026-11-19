package dev.pedro.messenger.contacts.internal;

import dev.pedro.messenger.identity.UserSummary;
import java.time.Instant;
import java.util.UUID;

/** Una solicitud pendiente. "user" es la otra persona. */
public record ContactRequestResponse(UUID id, UserSummary user, Instant createdAt) {}