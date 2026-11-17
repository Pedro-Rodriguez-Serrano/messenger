package dev.pedro.messenger.identity.internal;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;
import java.time.Instant;

@Service
public class TokenService {

    public record IssuedToken(String value, long expiresInSeconds) {}

    private final JwtEncoder encoder;
    private final long ttlSeconds;

    public TokenService(JwtEncoder encoder,
                        @Value("${app.security.jwt.ttl-seconds:900}") long ttlSeconds) {
        this.encoder = encoder;
        this.ttlSeconds = ttlSeconds;
    }

    /** Crea un token firmado cuyo "subject" es el id del usuario. */
    public IssuedToken issue(User user) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("messenger")
                .subject(user.getId().toString())
                .issuedAt(now)
                .expiresAt(now.plusSeconds(ttlSeconds))
                .claim("username", user.getUsername())
                .build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        String token = encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
        return new IssuedToken(token, ttlSeconds);
    }
}