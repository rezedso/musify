package com.example.musify.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AlbumListDto {
    private UUID id;
    private String name;
    private UUID userId;
    private String username;
    private Set<ListAlbumDto> albums;
}
