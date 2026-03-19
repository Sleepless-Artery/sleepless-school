package org.sleepless_artery.auth_service.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.sleepless_artery.auth_service.config.jwt.JwtProperties;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * Utility class for generating and managing JWT tokens.
 */
@Component
@RequiredArgsConstructor
public class JwtTokenUtils {

    private final JwtProperties jwtProperties;
    private SecretKey signingKey;


    /**
     * Initializes the signing key from the configured secret.
     * Validates key length for HS512 algorithm.
     */
    @PostConstruct
    public void init() {
        var keyBytes = Decoders.BASE64.decode(jwtProperties.getSecret());

        if (keyBytes.length < 64) {
            throw new IllegalArgumentException(
                    "JWT secret must be at least 512 bits for HS512"
            );
        }

        signingKey = Keys.hmacShaKeyFor(keyBytes);
    }


    /**
     * Generates a signed JWT token for the given user details.
     *
     * @param userDetails the authenticated user details containing roles
     * @return the compact JWT string
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();

        Set<String> roles = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        claims.put("roles", roles);

        var issuedDate = new Date();
        var expirationDate = new Date(issuedDate.getTime() + jwtProperties.getLifetime().toMillis());

        return Jwts.builder()
                .claims(claims)
                .subject(userDetails.getUsername())
                .issuedAt(issuedDate)
                .expiration(expirationDate)
                .signWith(signingKey)
                .compact();
    }
}