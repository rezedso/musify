package com.example.musify.dto.request;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateAlbumDto {
    @NotEmpty(message = "Title must not be empty.")
    @Size(min = 3, max = 80, message = "Title must be between 3 and 80 characters long.")
    private String title;
    @PastOrPresent(message = "Release date must be in the past or present.")
    private Instant releaseDate;
    @Nullable
    private Set<String> albumGenres;
    @NotEmpty(message = "Slug must not be empty.")
    @Size(min = 3, max = 80, message = "Slug must be between 3 and 80 characters long.")
    private String slug;
}
