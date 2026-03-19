package org.sleepless_artery.auth_service.authentication.service;

import org.sleepless_artery.auth_service.authentication.dto.LoginDto;
import org.sleepless_artery.auth_service.authentication.dto.JwtResponse;


/**
 * Service interface for user authentication operations.
 */
public interface AuthService {
    JwtResponse createAuthenticationToken(LoginDto loginDto);
}
