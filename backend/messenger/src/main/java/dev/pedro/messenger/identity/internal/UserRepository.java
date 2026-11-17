package dev.pedro.messenger.identity.internal;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/** Acceso a la tabla "users". Spring genera la implementación solo. */
public interface UserRepository extends JpaRepository<User, UUID> {

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String id);

    Optional<User> findByUsername(String id);
}