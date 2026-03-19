package org.sleepless_artery.auth_service.password.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.sleepless_artery.auth_service.common.exception.dto.ApiError;
import org.sleepless_artery.auth_service.password.dto.PasswordResetDto;
import org.sleepless_artery.auth_service.password.service.PasswordResetService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@Tag(
        name = "Password Reset",
        description = "Password reset workflow"
)
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("${basic-request-path}")
public class PasswordResetController {

    private final PasswordResetService passwordResetService;


    @Operation(summary = "Initiate password reset")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reset email sent"),
            @ApiResponse(responseCode = "404",
                    description = "Credential not found",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            )
    })
    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(
            @Parameter(description = "User email")
            @RequestParam @NotBlank String emailAddress
    ) {
        passwordResetService.initiatePasswordReset(emailAddress);
        return ResponseEntity.ok().build();
    }


    @Operation(summary = "Validate password reset code")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Code valid"),
            @ApiResponse(responseCode = "400", description = "Invalid code")
    })
    @PostMapping("/validate-reset-code")
    public ResponseEntity<Void> validateResetCode(
            @RequestParam @NotBlank String emailAddress,
            @RequestParam @NotBlank String resetCode
    ) {
        boolean isValid = passwordResetService.validatedResetCode(emailAddress, resetCode);
        return isValid ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }


    @Operation(summary = "Reset password")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Password successfully reset"),
            @ApiResponse(responseCode = "400",
                    description = "Password validation error",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            )
    })
    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(
            @Valid @RequestBody PasswordResetDto passwordResetDto
    ) {
        passwordResetService.completePasswordReset(passwordResetDto);
        return ResponseEntity.ok().build();
    }
}