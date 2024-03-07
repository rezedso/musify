package com.example.musify.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FollowerDto {
    private UUID id;
    private UserDto user;
    private ArtistDto artist;
}
