package dev.pedro.messenger.identity;

import java.util.UUID;

/** Datos públicos de un usuario. Sin email ni hash: solo lo que otros módulos pueden ver. */
public record UserSummary(UUID id, String username, String displayName,
                          String personalMessage, String avatarUrl) {}