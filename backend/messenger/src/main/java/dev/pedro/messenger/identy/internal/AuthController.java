package dev.pedro.messenger.identy.internal;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final RegistrationService registration;

    public AuthController(RegistrationService registration) {
        this.registration = registration;
    }

    /** POST /api/v1/auth/register. Devuelve 201, 400 (validación) o 409 (ya existe). */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(@Valid @RequestBody RegisterRequest request) {
        User user = registration.register(
                request.username(), request.email(), request.password(), request.displayName());
        return UserResponse.from(user);
    }
}