package dev.pedro.messenger.identity.internal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
public class ProfileService {

    private final UserRepository users;

    public ProfileService(UserRepository users) {
        this.users = users;
    }

    /** Devuelve el usuario con ese id. Si ya no existe (cuenta borrada), se trata como 401. */
    @Transactional(readOnly = true)
    public User getById(UUID id) {
        return users.findById(id).orElseThrow(InvalidCredentialsException::new);
    }
}