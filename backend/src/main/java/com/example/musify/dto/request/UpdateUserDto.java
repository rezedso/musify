package com.example.musify.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserDto {
    private UUID id;
    @NotEmpty(message = "Username must not be empty.")
    @Size(min = 3, max = 40, message = "Username must be between 3 and 40 characters long.")
    private String username;
}
