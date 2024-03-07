package com.example.musify.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ListAlbumDto {
    private String id;
    private String image;
    private String title;
    private String artistName;
    private String albumSlug;
    private String artistSlug;
    private Instant releaseDate;
}
