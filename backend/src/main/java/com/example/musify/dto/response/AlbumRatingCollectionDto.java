package com.example.musify.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlbumRatingCollectionDto {
    private UUID id;
    private String albumTitle;
    private String artistName;
    private String albumImage;
    private String albumSlug;
    private String artistSlug;
    private Instant releaseDate;
    private Double rating;
    private Instant ratedDate;
}
