package com.example.musify.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAlbumRatingDto {
    private Double rating;
    private Long totalRatings;
}
