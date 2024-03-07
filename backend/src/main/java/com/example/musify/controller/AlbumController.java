package com.example.musify.controller;

import com.example.musify.auth.dto.response.MessageDto;
import com.example.musify.dto.request.CreateAlbumDto;
import com.example.musify.dto.request.RateAlbumDto;
import com.example.musify.dto.request.UpdateAlbumDto;
import com.example.musify.dto.request.UserIdDto;
import com.example.musify.dto.response.AlbumDto;
import com.example.musify.dto.response.PageDto;
import com.example.musify.dto.response.RecentAlbumDto;
import com.example.musify.service.IAlbumService;
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
import java.util.List;
import java.util.UUID;

@Tag(name = "Album", description = "Endpoints related to albums.")
@RestController
@RequestMapping("/albums")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_USER')")
public class AlbumController {
    private final IAlbumService albumService;

    @Operation(summary = "Get a page containing all albums.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Retrieved a page containing all albums.",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PageDto.class,
                                    subTypes = {AlbumDto.class}))
                    })
    })
    @GetMapping("/page/{page}")
    public ResponseEntity<PageDto<AlbumDto>> getAlbums(
            @PathVariable("page") int page
    ) {
        return ResponseEntity.ok(albumService.getAlbums(page));
    }

    @Operation(summary = "Get most recent albums.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Retrieved a list containing the most recently created albums.",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "array", implementation = RecentAlbumDto.class))
                    })
    })
    @GetMapping("/recent")
    public ResponseEntity<List<RecentAlbumDto>> getMostRecentAlbums() {
        return ResponseEntity.ok(albumService.getMostRecentAlbums());
    }

    @Operation(summary = "Get an album.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Retrieved an album.",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AlbumDto.class))
                    }),
            @ApiResponse(
                    responseCode = "404",
                    description = "Album not found."
            )
    })
    @GetMapping("/{artistSlug}/{albumSlug}")
    public ResponseEntity<AlbumDto> getAlbum(
            @PathVariable("artistSlug") String artistSlug,
            @PathVariable("albumSlug") String albumSlug
    ) {
        return ResponseEntity.ok(albumService.getAlbum(artistSlug, albumSlug));
    }

    @Operation(summary = "Get albums by artist.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Retrieved a list of albums related to an artist.",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "array", implementation = AlbumDto.class))
                    }),
            @ApiResponse(
                    responseCode = "404",
                    description = "Artist not found."
            )
    })
    @GetMapping("/artists/{artistSlug}")
    public ResponseEntity<List<AlbumDto>> getAlbumsByArtist(
            @PathVariable("artistSlug") String artistSlug
    ) {
        return ResponseEntity.ok(albumService.getAlbumsByArtist(artistSlug));
    }

    @Operation(summary = "Get a page containing albums by genre.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Retrieved a page containing all albums by genre.",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PageDto.class,
                                    subTypes = {AlbumDto.class}))
                    }),
            @ApiResponse(responseCode = "404",
                    description = "Genre not found.")
    })
    @GetMapping("/genres/{genreSlug}/page/{page}")
    public ResponseEntity<PageDto<AlbumDto>> getAlbumsByGenre(
            @PathVariable("genreSlug") String genreSlug,
            @PathVariable("page") int page
    ) {
        return ResponseEntity.ok(albumService.getAlbumsByGenre(genreSlug, page));
    }

    @Operation(summary = "Create an album.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "Created an album.",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AlbumDto.class))
                    }),
            @ApiResponse(responseCode = "404",
                    description = "Artist not found.")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AlbumDto> createAlbum(
            @RequestPart("album") @Parameter(schema = @Schema(type = "string", format = "binary")) @Valid
            CreateAlbumDto request,
            @RequestPart(value = "image", required = false) MultipartFile file
    ) throws IOException {

        return ResponseEntity.status(HttpStatus.CREATED).body(albumService.createAlbum(request, file));
    }

    @Operation(summary = "Update an album.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Updated an album.",
                    content = {@Content(
                            mediaType = "application/json", schema = @Schema(implementation = AlbumDto.class))
                    }
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Album not found."
            )
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping(value = "/{albumId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AlbumDto> updateAlbum(
            @PathVariable("albumId") UUID albumId,
            @RequestPart(value = "album", required = false) @Parameter(schema = @Schema(type = "string", format = "binary")) @Valid
            UpdateAlbumDto updateAlbumDto,
            @RequestPart(value = "image", required = false) MultipartFile file
    ) throws IOException {
        return ResponseEntity.ok(albumService.updateAlbum(updateAlbumDto, file, albumId));
    }

    @Operation(summary = "Rate an album.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Rated an album.",
                    content = {@Content(
                            mediaType = "application/json", schema = @Schema(implementation = AlbumDto.class))
                    }
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Album not found."
            )
    })
    @PutMapping("/rate/{albumId}")
    public ResponseEntity<AlbumDto> rateAlbum(
            @RequestBody @Valid RateAlbumDto rateAlbumDto,
            @PathVariable("albumId") UUID albumId
    ) {
        return ResponseEntity.ok(albumService.rateAlbum(rateAlbumDto, albumId));
    }

    @Operation(summary = "Add an album to a list.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Added an album to a list.",
                    content = {@Content(
                            mediaType = "application/json", schema = @Schema(implementation = MessageDto.class))
                    }
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Album not found."
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "List not found."
            )
    })
    @PreAuthorize("hasRole('ROLE_ADMIN') or #request.id == authentication.principal.id")
    @PostMapping("/add/{listId}/{albumId}")
    public ResponseEntity<MessageDto> addAlbumToList(
            @PathVariable("listId") UUID listId,
            @PathVariable("albumId") UUID albumId,
            @RequestBody UserIdDto request
    ) {
        return ResponseEntity.ok(albumService.addAlbumToList(listId, albumId, request));
    }

    @Operation(summary = "Remove an album from a list.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Removed an album from a list.",
                    content = {@Content(
                            mediaType = "application/json", schema = @Schema(implementation = MessageDto.class))
                    }
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Album not found."
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "List not found."
            )
    })
    @PreAuthorize("hasRole('ROLE_ADMIN') or #request.id == authentication.principal.id")
    @DeleteMapping("/remove/{listId}/{albumId}")
    public ResponseEntity<MessageDto> removeAlbumFromList(
            @PathVariable("listId") UUID listId,
            @PathVariable("albumId") UUID albumId,
            @RequestBody UserIdDto request
    ) {
        return ResponseEntity.ok(albumService.removeAlbumFromList(listId, albumId, request));
    }

    @Operation(summary = "Delete an album.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Deleted an album.",
                    content = {@Content(
                            mediaType = "application/json", schema = @Schema(implementation = MessageDto.class))
                    }
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Album not found."
            )
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{albumId}")
    public ResponseEntity<MessageDto> deleteAlbum(
            @PathVariable("albumId") UUID albumId) {
        return ResponseEntity.ok(albumService.deleteAlbum(albumId));
    }
}
