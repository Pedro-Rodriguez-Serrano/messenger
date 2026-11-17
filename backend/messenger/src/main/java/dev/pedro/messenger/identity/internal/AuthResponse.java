package dev.pedro.messenger.identity.internal;

/** Respuesta de register y login, según el contrato. */
public record AuthResponse(String accessToken, String tokenType, long expiresIn, UserResponse user) {

    static AuthResponse of(TokenService.IssuedToken token, User user) {
        return new AuthResponse(token.value(), "Bearer", token.expiresInSeconds(),
                UserResponse.from(user));
    }
}