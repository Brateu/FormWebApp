package org.microservices.userservice.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO for change password request via JSON body.
 */
@Data
public class ChangePasswordRequestDto {
    @NotBlank(message = "Current password cannot be blank")
    private String currentPassword;

    @NotBlank(message = "New password cannot be blank")
    @Size(min = 8, message = "New password must be at least 8 characters long")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()_+\\-\\[\\]{}:;',.?/]).{8,}$",
            message = "New password must contain at least one digit, one uppercase letter, one lowercase letter, and one special character"
    )
    private String newPassword;

    @NotBlank(message = "Confirm new password cannot be blank")
    private String confirmNewPassword;
}
