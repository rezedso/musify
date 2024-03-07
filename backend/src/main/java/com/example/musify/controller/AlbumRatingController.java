package com.example.musify.controller;


import com.example.musify.dto.response.*;
import com.example.musify.service.IAlbumRatingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;


@Tag(name = "Album Rating", description = "Endpoints related to album ratings.")
@RestController
@RequestMapping("/album-ratings")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_USER')")
public class AlbumRatingController {
    private final IAlbumRatingService albumRatingService;

    @Operation(summary = "Get an album rating.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Retrieved an album rating.",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AlbumRatingStatsDto.class))
                    }),
            @ApiResponse(responseCode = "404",
                    description = "Album not found."
            )
    })
    @GetMapping("/{albumId}")
    public ResponseEntity<AlbumRatingStatsDto> getAlbumRating(
            @PathVariable("albumId") UUID albumId
    ) {
        return ResponseEntity.ok(albumRatingService.getAlbumRating(albumId));
    }

    @Operation(summary = "Get a user's album rating.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Retrieved a user's album rating.",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserRatingDto.class))
                    })
    })
    @GetMapping("/albums/{albumId}")
    public ResponseEntity<UserRatingDto> getUserAlbumRating(
            @PathVariable("albumId") UUID albumId
    ) {
        return ResponseEntity.ok(albumRatingService.getUserAlbumRating(albumId));
    }

    @Operation(summary = "Get the most recent album ratings.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Retrieved the most recently created album ratings.",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "array", implementation = RecentAlbumRatingDto.class))
                    }),
            @ApiResponse(responseCode = "404",
                    description = "Album not found."
            )
    })
    @GetMapping("/recent")
    public ResponseEntity<List<RecentAlbumRatingDto>> getMostRecentAlbumRatings() {
        return ResponseEntity.ok(albumRatingService.getMostRecentAlbumRatings());
    }

    @Operation(summary = "Get a user's genre overview.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Retrieved a list of a user's most rated album genres and the total number of albums rated for each genre.",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "array", implementation = GenreAlbumCountDto.class))
                    }),
            @ApiResponse(responseCode = "404",
                    description = "User not found."
            )
    })
    @GetMapping("/genre-overview/users/{username}")
    public ResponseEntity<List<GenreAlbumCountDto>> getUserGenreOverview(
            @PathVariable("username") String username
    ) {
        return ResponseEntity.ok(albumRatingService.getUserGenreOverview(username));
    }

    @Operation(summary = "Get a list of album ratings by user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Retrieved a list of a user's rated albums.",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "array", implementation = AlbumRatingSummaryDto.class))
                    })
    })
    @GetMapping("/users/{userId}")
    public ResponseEntity<List<AlbumRatingSummaryDto>> getAlbumRatingsByUser(
            @PathVariable("userId") UUID userId
    ) {
        return ResponseEntity.ok(albumRatingService.getAlbumRatingsByUser(userId));
    }

    @Operation(summary = "Get a list of album ratings by user and rating.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Retrieved a list of a user's rated albums with a specific rating.",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "array", implementation = AlbumRatingCollectionDto.class))
                    })
    })
    @GetMapping("/users/{username}/rating/{rating}")
    public ResponseEntity<List<AlbumRatingCollectionDto>> getAlbumRatingsByUserAndRating(
            @PathVariable("username") String username,
            @PathVariable("rating") Double rating
    ) {
        return ResponseEntity.ok(albumRatingService.getAlbumRatingsByUserAndRating(username, rating));
    }
}
