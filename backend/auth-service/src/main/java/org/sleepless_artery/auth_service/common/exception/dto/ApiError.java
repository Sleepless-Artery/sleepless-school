package org.sleepless_artery.auth_service.common.exception.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;


@Schema(description = "Standard API error response")
public record ApiError(

        @Schema(description = "Error timestamp", example = "2026-03-16T10:25:14.521Z")
        Instant timestamp,

        @Schema(description = "HTTP status code", example = "401")
        int status,

        @Schema(description = "HTTP status description", example = "Unauthorized")
        String error,

        @Schema(description = "Error message", example = "Authentication failed")
        String message,

        @Schema(description = "Request path", example = "/api/auth/login")
        String path
) {}