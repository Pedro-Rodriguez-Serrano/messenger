package dev.pedro.messenger.identity.internal;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Locale;

@Service
public class RegistrationService {

    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;

    public RegistrationService(UserRepository users, PasswordEncoder passwordEncoder) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Crea una cuenta nueva.
     * Precondiciones: los datos ya vienen validados (formato y longitudes).
     * Efecto: guarda el usuario con la contraseña hasheada; el email se guarda en minúsculas.
     *
     * @throws AccountConflictException si el username o el email ya existen
     */
    @Transactional
    public User register(String username, String email, String rawPassword, String displayName) {
        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);

        if (users.existsByUsername(username)) {
            throw new AccountConflictException("El nombre de usuario ya está en uso");
        }
        if (users.existsByEmail(normalizedEmail)) {
            throw new AccountConflictException("El email ya está en uso");
        }

        try {
            User user = new User(username, normalizedEmail,
                    passwordEncoder.encode(rawPassword), displayName.trim());
            return users.saveAndFlush(user);
        } catch (DataIntegrityViolationException e) {
            // Dos registros simultáneos: la restricción única de la BDD actúa de árbitro
            throw new AccountConflictException("El usuario o el email ya están en uso");
        }
    }
}