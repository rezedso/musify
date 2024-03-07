package com.example.musify.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePasswordDto {
    @NotEmpty(message = "Current password must not be empty.")
    @Size(min = 3, max = 80, message = "Current password must be between 3 and 80 characters long.")
    private String currentPassword;
    @NotEmpty(message = "New password must not be empty.")
    @Size(min = 3, max = 80, message = "New password must be between 3 and 80 characters long.")
    private String newPassword;
    @NotEmpty(message = "Confirmation password must not be empty.")
    @Size(min = 3, max = 80, message = "Confirmation password must be between 3 and 80 characters long.")
    private String confirmationPassword;
}
