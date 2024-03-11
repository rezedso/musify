package com.example.musify.controller;


import com.example.musify.auth.dto.response.MessageDto;
import com.example.musify.dto.request.CreateReviewDto;
import com.example.musify.dto.request.UpdateReviewDto;
import com.example.musify.dto.request.UserIdDto;
import com.example.musify.dto.response.PageDto;
import com.example.musify.dto.response.ReviewDto;
import com.example.musify.service.IReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Review", description = "Endpoints related to reviews.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
@PreAuthorize("hasRole('ROLE_USER')")
public class ReviewController {
    private final IReviewService reviewService;

    @Operation(summary = "Get a page containing all the reviews created by a user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Retrieved a page containing all the reviews created by a user.",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PageDto.class,
                                    subTypes = {ReviewDto.class}))
                    })
    })
    @GetMapping("users/{username}/page/{page}")
    ResponseEntity<PageDto<ReviewDto>> getUserReviews(
            @PathVariable("username") String username,
            @PathVariable("page") int page
    ) {
        return ResponseEntity.ok(reviewService.getUserReviews(username, page));
    }

    @Operation(summary = "Get a page with all the reviews.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Retrieved a page containing all the reviews.",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "array", implementation = ReviewDto.class))
                    })
    })
    @GetMapping("/page/{page}")
    ResponseEntity<PageDto<ReviewDto>> getReviews(
            @PathVariable("page") int page
    ) {
        return ResponseEntity.ok(reviewService.getReviews(page));
    }

    @Operation(summary = "Get a page containing all the reviews from an album.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Retrieved a page containing all the reviews from an album.",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PageDto.class,
                                    subTypes = {ReviewDto.class}))
                    }),
            @ApiResponse(
                    responseCode = "404",
                    description = "Album not found."
            )
    })
    @GetMapping("/albums/{albumId}/page/{page}")
    ResponseEntity<PageDto<ReviewDto>> getAlbumReviews(
            @PathVariable("albumId") UUID albumId,
            @PathVariable("page") int page
    ) {
        return ResponseEntity.ok(reviewService.getAlbumReviews(albumId, page));
    }

    @Operation(summary = "Check if a user is already reviewed an album.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Checked if a user already reviewed an album.",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Boolean.class))
                    })
    })
    @GetMapping("/exists-review/{albumId}")
    ResponseEntity<Boolean> existsReview(
            @PathVariable("albumId") UUID albumId
    ) {
        return ResponseEntity.ok(reviewService.existsReview(albumId));
    }

    @Operation(summary = "Create a review.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "Created a review.",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ReviewDto.class))
                    })
    })
    @PostMapping("/albums/{albumId}")
    ResponseEntity<ReviewDto> createReview(
            @RequestBody @Valid CreateReviewDto createReviewDto,
            @PathVariable("albumId") UUID albumId
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewService.createReview(createReviewDto, albumId));
    }

    @Operation(summary = "Update a review.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Updated a review.",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ReviewDto.class))
                    }),
            @ApiResponse(
                    responseCode = "404",
                    description = "Review not found."
            )
    })
    @PreAuthorize("hasRole('ROLE_ADMIN') or #request.userId == authentication.principal.id")
    @PutMapping("/{reviewId}")
    ResponseEntity<ReviewDto> updateReview(
            @RequestBody UpdateReviewDto request,
            @PathVariable("reviewId") UUID reviewId
    ) {
        return ResponseEntity.ok(reviewService.updateReview(request, reviewId));
    }

    @Operation(summary = "Delete a review.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Deleted a review.",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MessageDto.class))
                    }),
            @ApiResponse(
                    responseCode = "404",
                    description = "Review not found."
            )
    })
    @PreAuthorize("hasRole('ROLE_ADMIN') or #request.id == authentication.principal.id")
    @DeleteMapping("/{reviewId}")
    ResponseEntity<MessageDto> deleteReview(
            @PathVariable("reviewId") UUID reviewId,
            @RequestBody UserIdDto request
    ) {
        return ResponseEntity.ok(reviewService.deleteReview(reviewId, request));
    }
}
