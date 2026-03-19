package org.sleepless_artery.auth_service.authentication.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sleepless_artery.auth_service.authentication.dto.JwtResponse;
import org.sleepless_artery.auth_service.authentication.dto.LoginDto;
import org.sleepless_artery.auth_service.authentication.service.AuthService;
import org.sleepless_artery.auth_service.common.exception.dto.ApiError;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@Tag(
        name = "Authentication",
        description = "Authentication operations"
)
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("${basic-request-path}")
public class AuthController {

    private final AuthService authService;

    @Operation(
            summary = "Authenticate user",
            description = "Authenticates user and returns JWT token"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Authentication successful",
                    content = @Content(schema = @Schema(implementation = JwtResponse.class))
            ),
            @ApiResponse(responseCode = "401",
                    description = "Invalid credentials",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            ),
            @ApiResponse(responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            )
    })
    @PostMapping("/login")
    public ResponseEntity<JwtResponse> createAuthenticationToken(
            @Valid @RequestBody LoginDto loginDto
    ) {
        return ResponseEntity.ok(authService.createAuthenticationToken(loginDto));
    }


    @Operation(
            summary = "Logout user",
            description = "Logout endpoint (stateless, token should be discarded client-side)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Logout successful")
    })
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        return ResponseEntity.ok().build();
    }
}