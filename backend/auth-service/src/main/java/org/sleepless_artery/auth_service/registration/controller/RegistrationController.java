package org.sleepless_artery.auth_service.registration.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.sleepless_artery.auth_service.authentication.dto.JwtResponse;
import org.sleepless_artery.auth_service.common.exception.dto.ApiError;
import org.sleepless_artery.auth_service.registration.dto.RegistrationDto;
import org.sleepless_artery.auth_service.registration.service.RegistrationService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@Tag(
        name = "Registration",
        description = "User registration workflow"
)
@RestController
@RequiredArgsConstructor
@RequestMapping("${basic-request-path}")
@Validated
public class RegistrationController {

    private final RegistrationService registrationService;

    @Operation(summary = "Start registration")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Registration started"),
            @ApiResponse(responseCode = "409",
                    description = "Email already exists",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            )
    })
    @PostMapping("/register")
    public ResponseEntity<Void> registerUser(
            @Valid @RequestBody RegistrationDto credentialDto
    ) {
        registrationService.startRegistration(credentialDto);
        return ResponseEntity.ok().build();
    }


    @Operation(summary = "Confirm registration")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Registration successful",
                    content = @Content(schema = @Schema(implementation = JwtResponse.class))
            ),
            @ApiResponse(responseCode = "401",
                    description = "Invalid confirmation code",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            ),
            @ApiResponse(responseCode = "409",
                    description = "Email already registered",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            )
    })
    @PostMapping("/confirm-registration")
    public ResponseEntity<JwtResponse> confirmRegistration(
            @Valid @RequestBody RegistrationDto credentialDto,
            @RequestParam @NotBlank String confirmationCode
    ) {
        return ResponseEntity.ok(
                registrationService.confirmRegistration(credentialDto, confirmationCode)
        );
    }
}