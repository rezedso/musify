package com.example.musify.controller;

import com.example.musify.auth.service.JwtService;
import com.example.musify.auth.service.UserDetailsServiceImpl;
import com.example.musify.dto.response.*;
import com.example.musify.exception.ResourceNotFoundException;
import com.example.musify.repository.AlbumRatingRepository;
import com.example.musify.repository.AlbumRepository;
import com.example.musify.repository.TokenRepository;
import com.example.musify.repository.UserRepository;
import com.example.musify.service.IUtilService;
import com.example.musify.service.impl.AlbumRatingServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.UUID;


import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AlbumRatingController.class)
@WithMockUser(username = "user", password = "test", roles = {"USER", "ADMIN"})
@AutoConfigureMockMvc(addFilters = false)
public class AlbumRatingControllerTests {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private TokenRepository tokenRepository;
    @MockBean
    private JwtService jwtService;
    @MockBean
    private IUtilService utilService;
    @MockBean
    private UserDetailsServiceImpl userDetailsService;
    @MockBean
    private AlbumRatingRepository albumRatingRepository;
    @MockBean
    private AlbumRepository albumRepository;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private AlbumRatingServiceImpl albumRatingService;

    @Test
    public void testGetAlbumRating_Success() throws Exception {
        UUID albumId = UUID.randomUUID();

        given(albumRatingService.getAlbumRating(albumId)).willReturn(new AlbumRatingStatsDto());

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/album-ratings/{albumId}", albumId)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk());
    }

    @Test
    public void testGetAlbumRating_WhenAlbumNotFound_ThrowsResourceNotFoundException() throws Exception {
        given(albumRatingService.getAlbumRating(any(UUID.class)))
                .willThrow(new ResourceNotFoundException("Album not found."));

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/album-ratings/{albumId}", UUID.randomUUID())
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound());
    }


    @Test
    public void testGetUserAlbumRating() throws Exception {
        UUID albumId = UUID.randomUUID();

        given(albumRatingService.getUserAlbumRating(albumId)).willReturn(new UserRatingDto());

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/album-ratings/albums/{albumId}", albumId)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk());
    }

    @Test
    public void testGetMostRecentAlbumRating() throws Exception {
        given(albumRatingService.getMostRecentAlbumRatings()).willReturn(List.of(new RecentAlbumRatingDto()));

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/album-ratings/recent")
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk());
    }

    @Test
    public void testGetUserGenreOverview() throws Exception {
        given(albumRatingService.getUserGenreOverview(anyString())).willReturn(List.of(new GenreAlbumCountDto()));

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/album-ratings/genre-overview/users/{username}","username")
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk());
    }

    @Test
    public void testGetAlbumRatingsByUser() throws Exception {
        UUID userId = UUID.randomUUID();

        given(albumRatingService.getAlbumRatingsByUser(userId)).willReturn(List.of(new AlbumRatingSummaryDto()));

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/album-ratings/users/{userId}",userId)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk());
    }

    @Test
    public void testGetAlbumRatingsByUserAndRating_Success() throws Exception {
        UUID userId = UUID.randomUUID();

        given(albumRatingService.getAlbumRatingsByUserAndRating(anyString(),anyDouble()))
                .willReturn(List.of(new AlbumRatingCollectionDto()));

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/album-ratings/users/{username}/rating/{rating}","username",4.5)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk());
    }

    @Test
    public void testGetAlbumRatingsByUserAndRating_WhenUserNotFound_ThrowsResourceNotFoundException() throws Exception {
          given(albumRatingService.getAlbumRatingsByUserAndRating(anyString(),anyDouble()))
                .willThrow(new ResourceNotFoundException("User not found."));

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/album-ratings/users/{username}/rating/{rating}","username",4.5)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound());
    }
}
