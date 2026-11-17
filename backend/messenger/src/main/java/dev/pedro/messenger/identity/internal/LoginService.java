package dev.pedro.messenger.identity.internal;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Locale;
import java.util.Optional;

@Service
public class LoginService {

    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final String dummyHash;

    public LoginService(UserRepository users, PasswordEncoder passwordEncoder) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        // Hash falso para tardar lo mismo cuando el usuario no existe
        this.dummyHash = passwordEncoder.encode("dummy-password");
    }

    /**
     * Valida credenciales. El identificador puede ser username o email.
     * @throws InvalidCredentialsException si el usuario no existe o la contraseña no coincide
     */
    @Transactional(readOnly = true)
    public User login(String identifier, String rawPassword) {
        String id = identifier.trim().toLowerCase(Locale.ROOT);
        Optional<User> found = id.contains("@") ? users.findByEmail(id) : users.findByUsername(id);

        if (found.isEmpty()) {
            passwordEncoder.matches(rawPassword, dummyHash);
            throw new InvalidCredentialsException();
        }
        if (!passwordEncoder.matches(rawPassword, found.get().getPasswordHash())) {
            throw new InvalidCredentialsException();
        }
        return found.get();
    }
}