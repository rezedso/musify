package com.example.musify.service;

import com.example.musify.auth.dto.response.MessageDto;
import com.example.musify.dto.request.CreateReviewDto;
import com.example.musify.dto.request.UpdateReviewDto;
import com.example.musify.dto.request.UserIdDto;
import com.example.musify.dto.response.PageDto;
import com.example.musify.dto.response.ReviewDto;

import java.util.List;
import java.util.UUID;

public interface IReviewService {
    PageDto<ReviewDto> getUserReviews(int page);
    List<ReviewDto> getMostRecentReviews();
    PageDto<ReviewDto> getAlbumReviews(UUID albumId, int page);
    boolean existsReview(UUID albumId);
    ReviewDto createReview(CreateReviewDto createReviewDto, UUID albumId);
    ReviewDto updateReview(UpdateReviewDto updateReviewDto, UUID reviewId);
    MessageDto deleteReview(UUID reviewId, UserIdDto request);
}
