package com.example.musify.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Year;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArtistDto {
    private UUID id;
    private String name;
    private String slug;
    private String originCountry;
    private String image;
    private Year formedYear;
    private Set<GenreDto> artistGenres;
}
