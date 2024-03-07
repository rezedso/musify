package com.example.musify.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecentAlbumRatingDto {
    private UUID id;
    private String artistName;
    private String artistSlug;
    private String albumTitle;
    private String albumImage;
    private String albumSlug;
    private Double rating;
    private String username;
    private Instant releaseDate;
    private Instant createdAt;
}
