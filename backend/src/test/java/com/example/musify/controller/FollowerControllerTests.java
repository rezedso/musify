package com.example.musify.controller;

import com.example.musify.auth.dto.response.MessageDto;
import com.example.musify.auth.service.JwtService;
import com.example.musify.auth.service.UserDetailsServiceImpl;
import com.example.musify.dto.response.FollowerDto;
import com.example.musify.dto.response.FollowingArtistDto;
import com.example.musify.exception.ResourceNotFoundException;
import com.example.musify.repository.ArtistRepository;
import com.example.musify.repository.FollowerRepository;
import com.example.musify.repository.TokenRepository;
import com.example.musify.repository.UserRepository;
import com.example.musify.service.IUtilService;
import com.example.musify.service.impl.FollowerServiceImpl;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FollowerController.class)
@WithMockUser(username = "user", password = "test", roles = {"USER", "ADMIN"})
@AutoConfigureMockMvc(addFilters = false)
public class FollowerControllerTests {
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
    private FollowerRepository followerRepository;
    @MockBean
    private ArtistRepository artistRepository;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private FollowerServiceImpl followerService;

    @Test
    public void testGetArtistFollowers_Success() throws Exception {
        given(followerService.getArtistFollowers(any(UUID.class))).willReturn(List.of(new FollowerDto()));

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/followers/artists/{artistId}", UUID.randomUUID())
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk());
    }

    @Test
    public void testGetArtistFollowers_WhenArtistNotFound_ThrowsResourceNotFoundException() throws Exception {
        given(followerService.getArtistFollowers(any(UUID.class)))
                .willThrow(new ResourceNotFoundException("Artist not found."));

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/followers/artists/{artistId}", UUID.randomUUID())
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetUserFollowingArtists_Success() throws Exception {
        given(followerService.getUserFollowingArtists(anyString())).willReturn(List.of(new FollowingArtistDto()));

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/followers/{username}", UUID.randomUUID())
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk());
    }

    @Test
    public void testGetUserFollowingArtists_WhenUserNotFound_ThrowsResourceNotFoundException() throws Exception {
        given(followerService.getUserFollowingArtists(anyString()))
                .willThrow(new ResourceNotFoundException("User not found."));

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/followers/{username}", "username")
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetArtistFollowersCount_Success() throws Exception {
        given(followerService.getArtistFollowersCount(any(UUID.class))).willReturn(anyLong());

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/followers/count/{artistId}", UUID.randomUUID())
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk());
    }

    @Test
    public void testGetUserFollowingArtists_WhenArtistNotFound_ThrowsResourceNotFoundException() throws Exception {
        given(followerService.getArtistFollowersCount(any(UUID.class)))
                .willThrow(new ResourceNotFoundException("Artist not found."));

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/followers/count/{artistId}", UUID.randomUUID())
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound());
    }

    @Test
    public void testIsFollowing_Success() throws Exception {
        given(followerService.isFollowing(any(UUID.class))).willReturn(anyBoolean());

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/followers/is-following/{artistId}", UUID.randomUUID())
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk());
    }

    @Test
    public void testIsFollowing_WhenArtistNotFound_ThrowsResourceNotFoundException() throws Exception {
        given(followerService.isFollowing(any(UUID.class)))
                .willThrow(new ResourceNotFoundException("Artist not found."));

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/followers/is-following/{artistId}", UUID.randomUUID())
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound());
    }

    @Test
    public void testFollowArtist_Success() throws Exception {
        given(followerService.followArtist(any(UUID.class))).willReturn(new FollowerDto());

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/followers/follow/{artistId}", UUID.randomUUID())
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isCreated());
    }

    @Test
    public void testFollowArtist_WhenArtistNotFound_ThrowsResourceNotFoundException() throws Exception {
        given(followerService.followArtist(any(UUID.class)))
                .willThrow(new ResourceNotFoundException("Artist not found."));

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/followers/follow/{artistId}", UUID.randomUUID())
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUnFollowArtist_Success() throws Exception {
        given(followerService.unFollowArtist(any(UUID.class))).willReturn(new MessageDto());

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/followers/unfollow/{artistId}", UUID.randomUUID())
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk());
    }

    @Test
    public void testUnFollowArtist_WhenArtistNotFound_ThrowsResourceNotFoundException() throws Exception {
        given(followerService.unFollowArtist(any(UUID.class)))
                .willThrow(new ResourceNotFoundException("Artist not found."));

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/followers/unfollow/{artistId}", UUID.randomUUID())
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound());
    }
}
