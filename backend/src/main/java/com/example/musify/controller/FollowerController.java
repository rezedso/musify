package com.example.musify.controller;

import com.example.musify.auth.dto.response.MessageDto;
import com.example.musify.dto.response.FollowerDto;
import com.example.musify.dto.response.FollowingArtistDto;
import com.example.musify.service.IFollowerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Follower", description = "Endpoints related to followers.")
@RestController
@RequestMapping("/followers")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_USER')")
public class FollowerController {
    private final IFollowerService followerService;

    @Operation(summary = "Get a list containing all artist's followers.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Retrieved a list containing all artist's followers.",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "array", implementation = FollowerDto.class))
                    })
    })
    @GetMapping("/artists/{artistId}")
    ResponseEntity<List<FollowerDto>> getArtistFollowers(
            @PathVariable("artistId") UUID artistId
    ) {
        return ResponseEntity.ok(followerService.getArtistFollowers(artistId));
    }

    @Operation(summary = "Get a list containing all the artists followed by a user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Retrieved a list containing all the artists followed by a user.",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "array", implementation = FollowingArtistDto.class))
                    })
    })
    @GetMapping("/{username}")
    ResponseEntity<List<FollowingArtistDto>> getUserFollowingArtists(
            @PathVariable("username") String username
    ) {
        return ResponseEntity.ok(followerService.getUserFollowingArtists(username));
    }

    @Operation(summary = "Get an artist's followers count.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Retrieved the count of an artist's followers.",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Long.class))
                    })
    })
    @GetMapping("/count/{artistId}")
    ResponseEntity<Long> getArtistFollowersCount(
            @PathVariable("artistId") UUID artistId
    ) {
        return ResponseEntity.ok(followerService.getArtistFollowersCount(artistId));
    }

    @Operation(summary = "Check if a user is following an artist.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Checked if the user is following the artist.",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Boolean.class))
                    })
    })
    @GetMapping("/is-following/{artistId}")
    ResponseEntity<Boolean> isUserFollowing(
            @PathVariable("artistId") UUID artistId
    ) {
        return ResponseEntity.ok(followerService.isFollowing(artistId));
    }

    @Operation(summary = "Follow an artist.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "Followed an artist.",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = FollowerDto.class))
                    })
    })
    @PostMapping("/follow/{artistId}")
    ResponseEntity<FollowerDto> followArtist(
            @PathVariable("artistId") UUID artistId
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(followerService.followArtist(artistId));
    }

    @Operation(summary = "Unfollow an artist.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Unfollowed an artist.",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = FollowerDto.class))
                    })
    })
    @DeleteMapping("/unfollow/{artistId}")
    ResponseEntity<MessageDto> unFollowArtist(
            @PathVariable("artistId") UUID artistId
    ) {
        return ResponseEntity.ok(followerService.unFollowArtist(artistId));
    }
}
