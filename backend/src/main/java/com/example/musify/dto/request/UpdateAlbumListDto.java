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
public class UpdateAlbumListDto {
    @NotEmpty(message = "Name must not be empty.")
    @Size(min = 3,max = 80,message = "Name must be between 3 and 80 characters long.")
    private String name;
    private UUID userId;
}
