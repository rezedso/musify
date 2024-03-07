package com.example.musify.dto.response;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewDto {
    private UUID id;
    private String title;
    private String content;
    private Double rating;
    private UUID userId;
    private String username;
    private String userImage;
    private AlbumDto album;
    private Instant createdAt;
}
