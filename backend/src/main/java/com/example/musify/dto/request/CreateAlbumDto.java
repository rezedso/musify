package com.example.musify.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
public class CreateAlbumDto {
    @NotEmpty(message = "Artist name must not be empty.")
    @Size(min = 2, max = 80, message = "Artist name must be between 2 and 80 characters long.")
    private String artistName;
    @NotEmpty(message = "Title must not be empty.")
    @Size(min = 3, max = 80, message = "Title must be between 3 and 80 characters long.")
    private String title;

    @NotNull(message = "Release date must not be null.")
    @PastOrPresent(message = "Release date must be in the past or present.")
    private Instant releaseDate;
    @NotEmpty(message = "Genres must not be empty.")
    private Set<String> albumGenres;
    @NotEmpty(message = "Slug must not be empty.")
    @Size(min = 3, max = 80, message = "Slug must be between 3 and 80 characters long.")
    private String slug;
}
