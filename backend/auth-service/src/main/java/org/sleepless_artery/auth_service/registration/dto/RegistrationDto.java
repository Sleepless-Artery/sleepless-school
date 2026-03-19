package org.sleepless_artery.auth_service.registration.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;


/**
 * Data Transfer Object for user registration requests.
 */
@Getter
@AllArgsConstructor
public class RegistrationDto {

    @NotBlank(message = "Email address cannot be blank")
    @Size(min = 1, max = 255, message = "Email address must be between 1 and 255 characters")
    @Email(message = "Enter a correct email address")
    private String emailAddress;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$",
            message = "Password must contain at least one lowercase letter, one uppercase letter and one digit")
    private String password;

    @NotBlank(message = "Confirm your password")
    private String confirmationPassword;
}