package org.sleepless_artery.auth_service.email.controller;

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
import org.sleepless_artery.auth_service.authentication.dto.LoginDto;
import org.sleepless_artery.auth_service.common.exception.dto.ApiError;
import org.sleepless_artery.auth_service.email.service.change.EmailChangeService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@Tag(
        name = "Email Management",
        description = "Email change operations"
)
@RestController
@RequiredArgsConstructor
@RequestMapping("${basic-request-path}")
@Validated
public class EmailChangeController {

    private final EmailChangeService emailChangeService;

    @Operation(summary = "Request email change")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Email change initiated"),
            @ApiResponse(responseCode = "401",
                    description = "Invalid credentials",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            ),
            @ApiResponse(responseCode = "409",
                    description = "Email already exists",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            )
    })
    @PostMapping("/change-email-address")
    public ResponseEntity<Void> changeEmailAddress(
            @Valid @RequestBody LoginDto loginDto,
            @Parameter(description = "New email address")
            @RequestParam @NotBlank String emailAddress
    ) {
        emailChangeService.changeEmailAddress(loginDto, emailAddress);
        return ResponseEntity.ok().build();
    }


    @Operation(summary = "Confirm email change")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Email successfully changed"),
            @ApiResponse(responseCode = "401",
                    description = "Invalid confirmation code",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            ),
            @ApiResponse(responseCode = "409",
                    description = "Email already exists",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            )
    })
    @PostMapping("/confirm-email-address")
    public ResponseEntity<Void> confirmEmailAddress(
            @Valid @RequestBody LoginDto loginDto,
            @RequestParam @NotBlank String emailAddress,
            @RequestParam @NotBlank String confirmationCode
    ) {
        emailChangeService.confirmEmailAddress(loginDto.getEmailAddress(), emailAddress, confirmationCode);
        return ResponseEntity.ok().build();
    }
}