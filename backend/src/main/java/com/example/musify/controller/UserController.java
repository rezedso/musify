package com.example.musify.controller;

import com.example.musify.auth.dto.response.MessageDto;
import com.example.musify.dto.request.UpdatePasswordDto;
import com.example.musify.dto.request.UpdateUserDto;
import com.example.musify.dto.request.UserRoleDto;
import com.example.musify.dto.response.UpdatedUserDto;
import com.example.musify.dto.response.UserDto;
import com.example.musify.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Tag(name = "User", description = "Endpoints related to users.")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_USER')")
public class UserController {
    private final IUserService userService;

    @Operation(summary = "Get a user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Retrieved a user.",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserDto.class))
                    }),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found."
            )
    })
    @GetMapping("/{username}")
    public ResponseEntity<UserDto> getUser(
            @PathVariable("username") String username
    ) {
        return ResponseEntity.ok(userService.getUser(username));
    }

    @Operation(summary = "Get a list containing all users.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Retrieved a list containing all users.",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "array", implementation = UserDto.class))
                    })
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<List<UserDto>> getUsers() {
        return ResponseEntity.ok(userService.getUsers());
    }

    @Operation(summary = "Update a user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Updated a user.",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UpdatedUserDto.class))
                    })
    })
    @PutMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UpdatedUserDto> updateUser(
            @RequestPart(value = "user", required = false) @Parameter(schema = @Schema(type = "string", format = "binary"))
            @Valid UpdateUserDto request,
            @RequestPart(value = "image", required = false) MultipartFile file
    ) throws IOException {
        return ResponseEntity.ok(userService.updateUser(request, file));
    }

    @Operation(summary = "Update a user's password.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Updated a user's password.",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MessageDto.class))
                    }),
            @ApiResponse(
                    responseCode = "400",
                    description = "Wrong password."
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Passwords don't match."
            )
    })
    @PutMapping("/update-password")
    public ResponseEntity<MessageDto> updatePassword(
            @RequestBody UpdatePasswordDto request
    ) {
        return ResponseEntity.ok(userService.updatePassword(request));
    }

    @Operation(summary = "Update a user's role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Updated a user's role.",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserDto.class))
                    }),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found."
            )
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{userId}/update-role")
    public ResponseEntity<UserDto> updateUserRole(
            @RequestBody @Valid UserRoleDto userRoleDto,
            @PathVariable("userId") UUID userId,
            @RequestParam("addRole") boolean addRole
    ) {
        return ResponseEntity.ok(userService.updateUserRole(userRoleDto, userId, addRole));
    }

    @Operation(summary = "Delete a user.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Deleted a user.",
                    content = {@Content(
                            mediaType = "application/json", schema = @Schema(implementation = MessageDto.class))
                    }
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found."
            )
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{userId}")
    public ResponseEntity<MessageDto> deleteUser(
            @PathVariable("userId") UUID userId
    ) {
        return ResponseEntity.ok(userService.deleteUser(userId));
    }
}
