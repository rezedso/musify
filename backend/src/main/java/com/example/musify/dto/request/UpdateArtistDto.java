package com.example.musify.dto.request;

import jakarta.annotation.Nullable;
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
public class UpdateArtistDto {
    @Size(min = 3, max = 80, message = "Name must be between 3 and 80 characters long.")
    private String name;
    @Size(min = 3, max = 80, message = "Origin country must be between 3 and 80 characters long.")
    private String originCountry;
    @PastOrPresent(message = "Formed year must be less than or equal to the current year.")
    private Year formedYear;
    @Nullable
    private Set<String> artistGenres;
}
