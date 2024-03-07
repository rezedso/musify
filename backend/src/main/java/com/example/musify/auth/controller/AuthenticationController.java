package com.example.musify.auth.controller;

import com.example.musify.auth.dto.request.UserLoginDto;
import com.example.musify.auth.dto.request.UserRegisterDto;
import com.example.musify.auth.dto.response.LoginDto;
import com.example.musify.auth.dto.response.MessageDto;
import com.example.musify.auth.dto.response.TokenRefreshDto;
import com.example.musify.auth.service.AuthenticationService;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Tag(name = "Authentication", description = "Endpoints related to user authentication.")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final Bucket bucket;

    @Operation(summary = "Register a user")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "User successfully registered.",
                    content = {@Content(
                            mediaType = "application/json", schema = @Schema(implementation = MessageDto.class))
                    }
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Username or email already exists.")
    })
    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MessageDto> register(
            @RequestPart("user") @Parameter(schema = @Schema(type = "string", format = "binary")) @Valid UserRegisterDto userRegisterDto,
            @RequestPart(value = "image", required = false) MultipartFile file
    ) throws IOException {
        return ResponseEntity.status(HttpStatus.CREATED).body(authenticationService.register(userRegisterDto, file));
    }

    @Operation(summary = "Log a user in.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User successfully logged in.",
                    content = {@Content(
                            mediaType = "application/json", schema = @Schema(implementation = LoginDto.class))
                    }
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request."),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized: Bad credentials.")
    })
    @PostMapping("/login")
    public ResponseEntity<LoginDto> login(
            @RequestBody @Valid UserLoginDto userLoginDto
    ) {
        return ResponseEntity.ok(authenticationService.login(userLoginDto));
    }

    @Operation(summary = "Refresh Jwt.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Token successfully refreshed.",
                    content = {@Content(
                            mediaType = "application/json", schema = @Schema(implementation = TokenRefreshDto.class))
                    }
            )
    })
    @PostMapping("/refresh-token")
    public ResponseEntity<TokenRefreshDto> refreshToken(
            HttpServletRequest request, HttpServletResponse response
    ) throws IOException {
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
        if (probe.isConsumed()) {
            return ResponseEntity.ok()
                    .header("X-Rate-Limit-Remaining", Long.toString(probe.getRemainingTokens()))
                    .body(authenticationService.refreshToken(request, response));
        }
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .header("X-Rate-Limit-Retry-After-Milliseconds", Long.toString(TimeUnit.NANOSECONDS.toMillis(probe.getNanosToWaitForRefill())))
                .build();
    }
}
