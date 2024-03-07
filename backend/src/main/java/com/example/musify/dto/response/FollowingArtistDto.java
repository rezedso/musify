package com.example.musify.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FollowingArtistDto {
    private UUID id;
    private String name;
    private String slug;
    private String image;
}
