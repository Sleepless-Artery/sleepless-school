package org.sleepless_artery.auth_service.authentication.dto;

/**
 * Response object containing the generated JWT.
 *
 * @param token the generated JSON Web Token
 */
public record JwtResponse(String token) {}
