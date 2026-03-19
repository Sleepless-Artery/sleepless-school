package org.sleepless_artery.user_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


/**
 * DTO for creating or updating a user.
 */
@Getter @Setter
@AllArgsConstructor
public class UserRequestDto {

    @NotBlank(message = "Email address cannot be blank")
    @Size(min = 1, max = 255, message = "Email address must be between 1 and 255 characters")
    @Email(message = "Enter a correct email address")
    private String emailAddress;

    @NotBlank(message = "Username cannot be blank")
    @Size(min = 1, max = 50, message = "Username must contain from 1 to 50 characters")
    @Pattern(regexp = "^[a-zA-Zа-яА-Я0-9_]+$",
            message = "Username can contain only letters of the Russian and Latin alphabets, digits, and underscores")
    private String username;

    @Size(max = 1000, message = "Information must not exceed 1000 characters")
    private String information;
}
