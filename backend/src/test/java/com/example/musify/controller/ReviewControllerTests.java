package com.example.musify.controller;

import com.example.musify.auth.dto.response.MessageDto;
import com.example.musify.auth.service.JwtService;
import com.example.musify.auth.service.UserDetailsServiceImpl;
import com.example.musify.dto.request.CreateReviewDto;
import com.example.musify.dto.request.UpdateReviewDto;
import com.example.musify.dto.request.UserIdDto;
import com.example.musify.dto.response.*;
import com.example.musify.exception.ResourceNotFoundException;
import com.example.musify.repository.AlbumRepository;
import com.example.musify.repository.ReviewRepository;
import com.example.musify.repository.TokenRepository;
import com.example.musify.service.IUtilService;
import com.example.musify.service.impl.ReviewServiceImpl;
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
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReviewController.class)
@WithMockUser(username = "user", password = "test", roles = {"USER", "ADMIN"})
@AutoConfigureMockMvc(addFilters = false)
public class ReviewControllerTests {
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
    private ReviewServiceImpl reviewService;
    @MockBean
    private ReviewRepository reviewRepository;
    @MockBean
    private AlbumRepository albumRepository;

    @Test
    public void testGetUserReviews_Success() throws Exception {
        given(reviewService.getUserReviews(anyInt())).willReturn(
                new PageDto<>(List.of(new ReviewDto()), 1, 1, 1L));

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/reviews/page/{page}", 1)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isNotEmpty())
                .andExpect(jsonPath("$.currentPage").isNotEmpty())
                .andExpect(jsonPath("$.totalElements").isNotEmpty())
                .andExpect(jsonPath("$.totalPages").isNotEmpty())
        ;
    }

    @Test
    public void testGetMostRecentReviews() throws Exception {
        given(reviewService.getMostRecentReviews()).willReturn(List.of(new ReviewDto()));

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/reviews/recent")
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk());
    }

    @Test
    public void testGetAlbumReviews_Success() throws Exception {
        given(reviewService.getAlbumReviews(any(UUID.class), anyInt())).willReturn(
                new PageDto<>(List.of(new ReviewDto()), 1, 1, 1L));

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/reviews/albums/{albumId}/page/{page}",UUID.randomUUID(),1)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk());
    }

    @Test
    public void testGetAlbumReviews_WhenAlbumNotFound_ThrowsResourceNotFoundException() throws Exception {
        given(reviewService.getAlbumReviews(any(UUID.class), anyInt()))
                .willThrow(new ResourceNotFoundException("Album not found."));

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/reviews/albums/{albumId}/page/{page}",UUID.randomUUID(),1)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound());
    }

    @Test
    public void testExistsReview_Success() throws Exception {
        given(reviewService.existsReview(any(UUID.class))).willReturn(anyBoolean());

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/reviews/exists-review/{albumId}", UUID.randomUUID())
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk());
    }

    @Test
    public void testExistsReview_WhenAlbumNotFound_ThrowsResourceNotFoundException() throws Exception {
        given(reviewService.existsReview(any(UUID.class)))
                .willThrow(new ResourceNotFoundException("Album not found."));

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/reviews/exists-review/{albumId}", UUID.randomUUID())
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCreateReview_Success() throws Exception {
        CreateReviewDto createReviewDto = CreateReviewDto.builder()
                .title("title")
                .content("content")
                .rating(4.5)
                .build();

        given(reviewService.createReview(any(), any(UUID.class)))
                .willReturn(new ReviewDto());

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/reviews/albums/{albumId}", UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createReviewDto));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isCreated());
    }

    @Test
    public void testCreateReview_WhenAlbumNotFound_ThrowsResourceNotFoundException() throws Exception {
        CreateReviewDto createReviewDto = CreateReviewDto.builder()
                .title("title")
                .content("content")
                .rating(4.5)
                .build();

        given(reviewService.createReview(any(), any(UUID.class)))
                .willThrow(new ResourceNotFoundException("Album not found."));

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/reviews/albums/{albumId}", UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createReviewDto));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUpdateReview_Success() throws Exception {
        UpdateReviewDto updateReviewDto = UpdateReviewDto.builder()
                .title("title")
                .content("content")
                .rating(4.5)
                .build();

        given(reviewService.updateReview(any(), any(UUID.class)))
                .willReturn(new ReviewDto());

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/reviews/{reviewId}", UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateReviewDto));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk());
    }

    @Test
    public void testUpdateReview_WhenAlbumNotFound_ThrowsResourceNotFoundException() throws Exception {
        UpdateReviewDto updateReviewDto = UpdateReviewDto.builder()
                .title("title")
                .content("content")
                .rating(4.5)
                .build();

        given(reviewService.updateReview(any(), any(UUID.class)))
                .willThrow(new ResourceNotFoundException("Album not found."));

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/reviews/{reviewId}", UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateReviewDto));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteReview_Success() throws Exception {
        UUID reviewId = UUID.randomUUID();

        UserIdDto userIdDto = UserIdDto.builder()
                .id(UUID.randomUUID())
                .build();

        given(reviewService.deleteReview(reviewId,userIdDto)).willReturn(new MessageDto("Review deleted."));

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/reviews/{reviewId}", reviewId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userIdDto));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Review deleted."));
    }

    @Test
    public void testDeleteReview_WhenReviewNotFound_ThrowsResourceNotFoundException() throws Exception {
        UUID reviewId = UUID.randomUUID();

        UserIdDto userIdDto = UserIdDto.builder()
                .id(UUID.randomUUID())
                .build();

        given(reviewService.deleteReview(reviewId,userIdDto))
                .willThrow(new ResourceNotFoundException("Review not found"));

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/reviews/{reviewId}", reviewId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userIdDto));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound());
    }
}
