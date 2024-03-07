package com.example.musify.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Year;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateArtistDto {
    @NotEmpty(message = "Name must not be empty.")
    @Size(min = 2, max = 80, message = "Name must be between 2 and 80 characters long.")
    private String name;
    @NotEmpty(message = "Origin country must not be empty.")
    @Size(min = 3, max = 80, message = "Origin country must be between 3 and 80 characters long.")
    private String originCountry;
    @PastOrPresent(message = "Formed year must be less than or equal to the current year.")
    private Year formedYear;
    private Set<String> artistGenres;
    @Size(min = 3, max = 80, message = "Slug must be between 3 and 80 characters long.")
    private String slug;
}
