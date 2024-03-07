package com.example.musify.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginDto {
    private String accessToken;
    private String refreshToken;
    private UUID id;
    private String username;
    private String email;
    private String imageUrl;
    private List<String> roles;
}
