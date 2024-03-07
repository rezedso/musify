package com.example.musify.dto.response;

import com.example.musify.entity.Genre;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecentAlbumDto {
    private UUID id;
    private String title;
    private Instant releaseDate;
    private String albumImage;
    private String slug;
    private Set<Genre> genres;
    private String artistName;
    private String artistSlug;
}
