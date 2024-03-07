package com.example.musify.service.impl;

import com.example.musify.auth.dto.response.MessageDto;
import com.example.musify.dto.request.CreateReviewDto;
import com.example.musify.dto.request.UpdateReviewDto;
import com.example.musify.dto.request.UserIdDto;
import com.example.musify.dto.response.PageDto;
import com.example.musify.dto.response.ReviewDto;
import com.example.musify.entity.Album;
import com.example.musify.entity.Review;
import com.example.musify.entity.User;
import com.example.musify.exception.ResourceAlreadyExistsException;
import com.example.musify.exception.ResourceNotFoundException;
import com.example.musify.repository.AlbumRepository;
import com.example.musify.repository.ReviewRepository;
import com.example.musify.service.IReviewService;
import com.example.musify.service.IUtilService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements IReviewService {
    private final ReviewRepository reviewRepository;
    private final AlbumRepository albumRepository;
    private final IUtilService utilService;
    private final ModelMapper modelMapper;

    @Override
    public PageDto<ReviewDto> getUserReviews(int page) {
        User user = utilService.getCurrentUser();

        Page<Review> pageRequest = reviewRepository.findByUser(user.getId(),
                PageRequest.of(page - 1, 20));
        int totalPages = pageRequest.getTotalPages();
        int currentPage = pageRequest.getNumber() + 1;
        Long totalElements = pageRequest.getTotalElements();

        List<ReviewDto> content = pageRequest.getContent().stream().map(review ->
                modelMapper.map(review, ReviewDto.class)).collect(Collectors.toList());
        return new PageDto<>(content, totalPages, currentPage, totalElements);
    }

    @Override
    public List<ReviewDto> getMostRecentReviews() {
        Pageable pageable = PageRequest.of(0, 4);

        return reviewRepository.findMostRecentReviews(pageable).stream().map(
                review -> modelMapper.map(review, ReviewDto.class)).toList();
    }

    @Override
    public PageDto<ReviewDto> getAlbumReviews(UUID albumId, int page) {
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new ResourceNotFoundException("Album not found."));

        Page<Review> pageRequest = reviewRepository.findByAlbum(album,
                PageRequest.of(page - 1, 20));
        int totalPages = pageRequest.getTotalPages();
        int currentPage = pageRequest.getNumber() + 1;
        Long totalElements = pageRequest.getTotalElements();

        List<ReviewDto> content = pageRequest.getContent().stream().map(review ->
                modelMapper.map(review, ReviewDto.class)).collect(Collectors.toList());

        return new PageDto<>(content, totalPages, currentPage, totalElements);
    }

    @Override
    public boolean existsReview(UUID albumId) {
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new ResourceNotFoundException("Album not found."));

        User user = utilService.getCurrentUser();

        return reviewRepository.existsReviewByAlbumAndUser(album, user);
    }

    @Override
    public ReviewDto createReview(CreateReviewDto request, UUID albumId) {
        User user = utilService.getCurrentUser();
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new ResourceNotFoundException("Album not found."));

        boolean existsReview = reviewRepository.existsReviewByAlbumAndUser(album, user);

        if (existsReview) {
            throw new ResourceAlreadyExistsException("User already reviewed this album.");
        } else {
            Review newReview = Review.builder()
                    .album(album)
                    .user(user)
                    .title(request.getTitle())
                    .content(request.getContent())
                    .rating(request.getRating())
                    .build();

            reviewRepository.save(newReview);

            return modelMapper.map(newReview, ReviewDto.class);
        }
    }

    @Override
    @Transactional
    public ReviewDto updateReview(UpdateReviewDto request, UUID reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found."));

        if (!review.getTitle().equals(request.getTitle())) {
            review.setTitle(request.getTitle());
        }

        if (!review.getContent().equals(request.getContent())) {
            review.setContent(request.getContent());
        }

        if (!review.getRating().equals(request.getRating())) {
            review.setRating(request.getRating());
        }

        return modelMapper.map(review, ReviewDto.class);
    }

    @Override
    public MessageDto deleteReview(UUID reviewId, UserIdDto request) {
        reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found."));

        reviewRepository.deleteById(reviewId);

        return new MessageDto("Review deleted.");
    }
}
