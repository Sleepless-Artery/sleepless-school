package org.sleepless_artery.gateway_service.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.sleepless_artery.gateway_service.exception.TokenValidationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;


/**
 * Utility component responsible for validating JWT tokens.
 */
@Slf4j
@Component
public class JwtValidator {

    private final SecretKey secretKey;

    public JwtValidator(@Value("${jwt.secret}") String secret) {
        var keyBytes = Base64.getDecoder().decode(secret);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Validates the provided JWT token.
     *
     * @param token JWT token string
     * @return parsed token claims
     * @throws TokenValidationException if token validation fails
     */
    public Claims validateToken(String token) throws TokenValidationException {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token");
            throw new TokenValidationException("Token expired");
        } catch (SignatureException ex) {
            log.error("Invalid JWT token");
            throw new TokenValidationException("Invalid token signature");
        } catch (Exception ex) {
            log.error(ex.getMessage());
            throw new TokenValidationException("Token validation failed");
        }
    }
}