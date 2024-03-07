package com.example.musify.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AlbumListUserIdDto {
    private UUID id;

    public AlbumListUserIdDto(String id) {
        this.id = UUID.fromString(id);
    }
}
