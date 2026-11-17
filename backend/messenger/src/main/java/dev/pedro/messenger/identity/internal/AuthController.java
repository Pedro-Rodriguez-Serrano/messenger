package dev.pedro.messenger.identity.internal;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final RegistrationService registration;
    private final LoginService login;
    private final TokenService tokens;

    public AuthController(RegistrationService registration, LoginService login, TokenService tokens) {
        this.registration = registration;
        this.login = login;
        this.tokens = tokens;
    }

    /** POST /api/v1/auth/register. 201, 400 (validación) o 409 (ya existe). */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        User user = registration.register(
                request.username(), request.email(), request.password(), request.displayName());
        return AuthResponse.of(tokens.issue(user), user);
    }

    /** POST /api/v1/auth/login. 200, 400 (validación) o 401 (credenciales incorrectas). */
    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        User user = login.login(request.identifier(), request.password());
        return AuthResponse.of(tokens.issue(user), user);
    }
}