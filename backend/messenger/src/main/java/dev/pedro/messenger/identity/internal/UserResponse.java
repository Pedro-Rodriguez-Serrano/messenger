package dev.pedro.messenger.identity.internal;

import java.time.Instant;
import java.util.UUID;
import java.util.Optional;

/** Datos de un usuario que se devuelven al cliente. Nunca incluye el hash. */
public record UserResponse(
        UUID id,
        String username,
        String email,
        String displayName,
        String personalMessage,
        String avatarUrl,
        Instant createdAt
) {
    static UserResponse from(User u) {
        return new UserResponse(u.getId(), u.getUsername(), u.getEmail(),
                u.getDisplayName(), u.getPersonalMessage(), u.getAvatarUrl(), u.getCreatedAt());
    }

    Optional<User> findByUsername(String username) {
        return null;
    }

    Optional<User> findByEmail(String email) {
        return null;
    }
}