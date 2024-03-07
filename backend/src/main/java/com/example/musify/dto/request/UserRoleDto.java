package com.example.musify.dto.request;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRoleDto {
    @Pattern(regexp = "^(ROLE_USER|ROLE_ADMIN)$", message = "Role can only be 'ROLE_USER' or 'ROLE_ADMIN'")
    private String role;
}
