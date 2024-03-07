package com.example.musify.service;

import com.example.musify.dto.response.*;

import java.util.List;
import java.util.UUID;

public interface IAlbumRatingService {
    AlbumRatingStatsDto getAlbumRating(UUID albumId);
    List<RecentAlbumRatingDto>getMostRecentAlbumRatings();

    List<AlbumRatingSummaryDto> getAlbumRatingsByUser(UUID userId);
    UserRatingDto getUserAlbumRating(UUID albumId);

    List<AlbumRatingCollectionDto> getAlbumRatingsByUserAndRating(String username, Double rating);
    List<GenreAlbumCountDto>getUserGenreOverview(String userId);
}
