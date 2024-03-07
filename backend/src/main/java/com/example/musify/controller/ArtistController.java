package com.example.musify.controller;

import com.example.musify.auth.dto.response.MessageDto;
import com.example.musify.dto.request.CreateArtistDto;
import com.example.musify.dto.request.UpdateArtistDto;
import com.example.musify.dto.response.*;
import com.example.musify.service.IArtistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Tag(name = "Artist", description = "Endpoints related to artists.")
@RestController
@RequestMapping("/artists")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_USER')")
public class ArtistController {
    private final IArtistService artistService;

    @Operation(summary = "Get a page containing all artists.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Retrieved a page containing all artists.",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PageDto.class,
                                    subTypes = {ArtistDto.class}))
                    })
    })
    @GetMapping("/page/{page}")
    public ResponseEntity<PageDto<ArtistDto>> getArtists(
            @PathVariable("page") int page
    ) {
        return ResponseEntity.ok(artistService.getArtists(page));
    }

    @Operation(summary = "Get most recent artists.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Retrieved a list containing the most recently created artists.",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "array", implementation = RecentArtistDto.class))
                    })
    })
    @GetMapping("/recent")
    public ResponseEntity<List<RecentArtistDto>> getMostRecentArtists() {
        return ResponseEntity.ok(artistService.getMostRecentArtists());
    }

    @Operation(summary = "Get a page containing artists by genre.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Retrieved a page containing all artists by genre.",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PageDto.class,
                                    subTypes = {ArtistDto.class}))
                    }),
            @ApiResponse(responseCode = "404",
                    description = "Genre not found.")
    })
    @GetMapping("/genres/{genreSlug}/page/{page}")
    public ResponseEntity<PageDto<ArtistDto>> getArtistsByGenre(
            @PathVariable("genreSlug") String genreSlug,
            @PathVariable("page") int page
    ) {
        return ResponseEntity.ok(artistService.getArtistsByGenre(genreSlug, page));
    }

    @Operation(summary = "Get an artist.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Retrieved an artist.",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ArtistDto.class))
                    }),
            @ApiResponse(
                    responseCode = "404",
                    description = "Artist not found."
            )
    })
    @GetMapping("/{artistSlug}")
    public ResponseEntity<ArtistDto> getArtist(
            @PathVariable("artistSlug") String artistSlug
    ) {
        return ResponseEntity.ok(artistService.getArtist(artistSlug));
    }

    @Operation(summary = "Create an artist.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "Created an artist.",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ArtistDto.class))
                    })
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ArtistDto> createArtist(
            @RequestPart("artist") @Valid CreateArtistDto request,
            @RequestPart(value = "image", required = false) MultipartFile file
    ) throws IOException {

        return ResponseEntity.status(HttpStatus.CREATED).body(artistService.createArtist(request, file));
    }

    @Operation(summary = "Update an artist.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Updated an artist.",
                    content = {@Content(
                            mediaType = "application/json", schema = @Schema(implementation = ArtistDto.class))
                    }
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Artist not found."
            )
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping(value = "/{artistId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ArtistDto> updateArtist(
            @PathVariable("artistId") UUID artistId,
            @RequestPart(value = "artist", required = false) @Parameter(schema = @Schema(type = "string", format = "binary")) @Valid UpdateArtistDto updateArtistDto,
            @RequestPart(value = "image", required = false) MultipartFile file
    ) throws IOException {
        return ResponseEntity.ok(artistService.updateArtist(updateArtistDto, file, artistId));
    }

    @Operation(summary = "Delete an artist.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Deleted an artist.",
                    content = {@Content(
                            mediaType = "application/json", schema = @Schema(implementation = MessageDto.class))
                    }
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Artist not found."
            )
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{artistId}")
    public ResponseEntity<MessageDto> deleteArtist(
            @PathVariable("artistId") UUID artistId) {
        return ResponseEntity.ok(artistService.deleteArtist(artistId));
    }
}
