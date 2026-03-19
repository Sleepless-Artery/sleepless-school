package org.sleepless_artery.auth_service.authentication.service;

import lombok.RequiredArgsConstructor;
import org.sleepless_artery.auth_service.authentication.dto.LoginDto;
import org.sleepless_artery.auth_service.authentication.dto.JwtResponse;
import org.sleepless_artery.auth_service.common.exception.BadCredentialException;
import org.sleepless_artery.auth_service.common.logging.annotation.BusinessEvent;
import org.sleepless_artery.auth_service.common.logging.event.LogEvent;
import org.sleepless_artery.auth_service.security.jwt.JwtTokenUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Objects;


/**
 * Implementation of {@link AuthService} responsible for authentication logic.
 * Handles token generation upon successful credential validation.
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final JwtTokenUtils jwtTokenUtils;
    private final AuthenticationManager authenticationManager;


    /**
     * Authenticates a user and generates a JWT token.
     *
     * @param loginDto the login credentials containing email and password
     * @return JwtResponse containing the generated access token
     * @throws BadCredentialException if the provided credentials are invalid
     */
    @Override
    @BusinessEvent(LogEvent.CREATION_AUTHENTICATION_TOKEN)
    public JwtResponse createAuthenticationToken(LoginDto loginDto) {
        try {
            var authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDto.getEmailAddress(),
                            loginDto.getPassword()
                    )
            );

            var userDetails = (UserDetails) authentication.getPrincipal();
            var token = jwtTokenUtils.generateToken(Objects.requireNonNull(userDetails));
            return new JwtResponse(token);

        } catch (BadCredentialsException exception) {
            throw new BadCredentialException("Incorrect login or password");
        }
    }
}