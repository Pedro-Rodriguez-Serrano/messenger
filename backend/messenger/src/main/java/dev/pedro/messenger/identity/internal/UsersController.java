package dev.pedro.messenger.identity.internal;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
public class UsersController {

    private final ProfileService profiles;

    public UsersController(ProfileService profiles) {
        this.profiles = profiles;
    }

    /** GET /api/v1/users/me. Requiere token. */
    @GetMapping("/me")
    public UserResponse me(@AuthenticationPrincipal Jwt jwt) {
        return UserResponse.from(profiles.getById(UUID.fromString(jwt.getSubject())));
    }
}