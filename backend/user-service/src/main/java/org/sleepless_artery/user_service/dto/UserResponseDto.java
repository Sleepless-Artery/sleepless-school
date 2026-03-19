package org.sleepless_artery.user_service.dto;


/**
 * DTO for displaying user's data.
 */
public record UserResponseDto(
        Long id,
        String emailAddress,
        String username,
        String information
) {}
