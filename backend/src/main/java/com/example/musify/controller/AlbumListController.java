package com.example.musify.controller;

import com.example.musify.auth.dto.response.MessageDto;
import com.example.musify.dto.request.AlbumListUserIdDto;
import com.example.musify.dto.request.CreateAlbumListDto;
import com.example.musify.dto.request.UpdateAlbumListDto;
import com.example.musify.dto.response.AlbumListDto;
import com.example.musify.dto.response.AlbumListNameDto;
import com.example.musify.dto.response.AlbumListsCountDto;
import com.example.musify.service.IAlbumListService;
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

@Tag(name = "Album List", description = "Endpoints related to album lists.")
@RestController
@RequestMapping("/album-lists")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_USER')")
public class AlbumListController {
    private final IAlbumListService albumListService;

    @Operation(summary = "Get user lists count.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Retrieved the total count of lists created by a user.",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AlbumListsCountDto.class))
                    }),
            @ApiResponse(responseCode = "404",
                    description = "User not found."
            )
    })
    @GetMapping("/count/users/{username}")
    public ResponseEntity<AlbumListsCountDto> getUserListsCount(
            @PathVariable("username") String username
    ) {
        return ResponseEntity.ok(albumListService.getUserListsCount(username));
    }

    @Operation(summary = "Get a list of names of all the lists created by a user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Retrieved the names of all the lists created by a user.",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "array", implementation = AlbumListNameDto.class))
                    }),
            @ApiResponse(responseCode = "404",
                    description = "User not found."
            )
    })
    @GetMapping("/summary/users/{username}")
    public ResponseEntity<List<AlbumListNameDto>> getUserListNames(
            @PathVariable("username") String username
    ) {
        return ResponseEntity.ok(albumListService.getUserListNames(username));
    }

    @Operation(summary = "Get a list.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Retrieved a list.",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AlbumListDto.class))
                    }),
            @ApiResponse(responseCode = "404",
                    description = "Album List not found."
            )
    })
    @GetMapping("/{listName}/users/{username}")
    public ResponseEntity<AlbumListDto> getList(
            @PathVariable("listName") String listName,
            @PathVariable("username") String username
    ) {
        return ResponseEntity.ok(albumListService.getList(listName, username));
    }

    @Operation(summary = "Create an album list.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "Created an album list.",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AlbumListDto.class))
                    })
    })
    @PostMapping
    public ResponseEntity<AlbumListDto> createAlbumList(
            @RequestBody @Valid CreateAlbumListDto request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(albumListService.createAlbumList(request));
    }

    @Operation(summary = "Update an album list.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Retrieved an album list.",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AlbumListDto.class))
                    }),
            @ApiResponse(responseCode = "404",
                    description = "Album List not found."
            )
    })
    @PreAuthorize("hasRole('ROLE_ADMIN') or #request.userId == authentication.principal.id")
    @PutMapping("/{albumListId}")
    public ResponseEntity<AlbumListDto> updateAlbumList(
            @RequestBody @Valid UpdateAlbumListDto request,
            @PathVariable("albumListId") UUID albumListId
    ) {
        return ResponseEntity.ok(albumListService.updateAlbumList(request, albumListId));
    }

    @Operation(summary = "Delete an album list.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Deleted an album list.",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MessageDto.class))
                    }),
            @ApiResponse(responseCode = "404",
                    description = "Album List not found."
            )
    })
    @PreAuthorize("hasRole('ROLE_ADMIN') or #request.id == authentication.principal.id")
    @DeleteMapping("/{albumListId}")
    public ResponseEntity<MessageDto> deleteAlbumList(
            @PathVariable("albumListId") UUID albumListId,
            @RequestBody AlbumListUserIdDto request
    ) {
        return ResponseEntity.ok(albumListService.deleteAlbumList(albumListId,request));
    }
}
