package org.microservices.userservice.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Data Transfer Object used for registering a new user.
 * Encapsulates the necessary information required during the registration process.
 *
 * Fields include:
 * - email: The email address of the user. It must be a valid email format and not blank.
 * - password: The user's desired password. It must meet certain complexity criteria, including containing at least
 *             one digit, one uppercase letter, one lowercase letter, and one special character. It must also be
 *             a minimum of 8 characters long.
 * - fullName: The full name of the user. It must not be blank.
 *
 * This class is validated using constraints such as:
 * - @NotBlank: Ensures the field is not null or empty.
 * - @Email: Validates that the email field is in a proper email format.
 * - @Size: Validates the minimum length of the password field.
 * - @Pattern: Validates that the password meets the required complexity.
 *
 * This class is commonly used in the registration endpoint of the authentication service.
 */
@Data
public class RegisterRequestDto {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>\\.]).+$",
            message = "Password must contain at least one digit, one uppercase letter, one lowercase letter, and one special character"
    )
    private String password;

    @NotBlank
    private String fullName;
}