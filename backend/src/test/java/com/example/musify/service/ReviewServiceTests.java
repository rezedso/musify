package com.example.musify.service;

import com.example.musify.dto.request.CreateReviewDto;
import com.example.musify.dto.request.UpdateReviewDto;
import com.example.musify.dto.response.PageDto;
import com.example.musify.dto.response.ReviewDto;
import com.example.musify.entity.*;
import com.example.musify.exception.ResourceAlreadyExistsException;
import com.example.musify.exception.ResourceNotFoundException;
import com.example.musify.repository.*;
import com.example.musify.service.impl.ReviewServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.time.Year;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceTests {
    @Mock
    private ArtistRepository artistRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private GenreRepository genreRepository;
    @Mock
    private AlbumRepository albumRepository;
    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private IUtilService utilService;
    @Spy
    private ModelMapper modelMapper;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    private Album album1;
    private User user1;
    private Review review1;

    @BeforeEach
    void setup() {
        Artist artist1 = Artist.builder()
                .name("artist")
                .formedYear(Year.of(2010))
                .originCountry("country")
                .createdAt(Instant.now())
                .slug("artist")
                .build();
        artistRepository.save(artist1);

        Genre genre1 = Genre.builder()
                .name("Test Genre")
                .slug("test-genre")
                .build();
        genreRepository.save(genre1);

        album1 = Album.builder()
                .title("Test 1")
                .slug("test-1")
                .artist(artist1)
                .originCountry("country")
                .createdAt(Instant.now())
                .rating(4.5)
                .build();

        Album album2 = Album.builder()
                .title("Test 2")
                .artist(artist1)
                .slug("test-2")
                .originCountry("country")
                .lists(new HashSet<>())
                .createdAt(Instant.now())
                .rating(4.0)
                .build();
        albumRepository.saveAll(List.of(album1, album2));

        user1 = User.builder()
                .username("testuser")
                .email("user@test.com")
                .password("password")
                .build();
        userRepository.save(user1);

        review1 = Review.builder()
                .user(user1)
                .title("Review 1")
                .rating(5.0)
                .album(album1)
                .content("Content")
                .createdAt(Instant.now())
                .build();
        reviewRepository.save(review1);
    }

    @Test
    void testGetUserReviews() {
        given(reviewRepository.findByUser(eq(user1.getId()), any(Pageable.class)))
                .willReturn(new PageImpl<>(List.of(review1)));
        given(utilService.getCurrentUser()).willReturn(user1);

        PageDto<ReviewDto> result = reviewService.getUserReviews(1);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalPages()).isEqualTo(1);
        assertThat(result.getCurrentPage()).isEqualTo(1);
        assertThat(result.getTotalElements()).isEqualTo(1L);

        verify(reviewRepository, times(1)).findByUser(eq(user1.getId()), any(Pageable.class));
    }

    @Test
    void testGetMostRecentReviews() {
        given(reviewRepository.findMostRecentReviews(any(Pageable.class))).willReturn(List.of(review1));

        List<ReviewDto> result = reviewService.getMostRecentReviews();

        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);

        verify(reviewRepository, times(1)).findMostRecentReviews(any(Pageable.class));
    }

    @Test
    void testGetAlbumReviews_Success() {
        given(albumRepository.findById(album1.getId())).willReturn(Optional.of(album1));
        given(reviewRepository.findByAlbum(eq(album1), any(Pageable.class))).willReturn(new PageImpl<>(List.of(review1)));

        PageDto<ReviewDto> result = reviewService.getAlbumReviews(album1.getId(), 1);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalPages()).isEqualTo(1);
        assertThat(result.getCurrentPage()).isEqualTo(1);
        assertThat(result.getTotalElements()).isEqualTo(1L);

        verify(albumRepository, times(1)).findById(album1.getId());
        verify(reviewRepository, times(1)).findByAlbum(eq(album1), any(Pageable.class));
    }

    @Test
    void testGetAlbumReviews_WhenAlbumNotFound_ThrowsResourceNotFoundException() {
        given(albumRepository.findById(album1.getId())).willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                reviewService.getAlbumReviews(album1.getId(), 1));

        verify(albumRepository, times(1)).findById(album1.getId());
        verify(reviewRepository, never()).findByAlbum(eq(album1), any(Pageable.class));
    }

    @Test
    void existsReview_ReturnsTrue() {
        given(albumRepository.findById(album1.getId())).willReturn(Optional.of(album1));
        given(utilService.getCurrentUser()).willReturn(user1);
        given(reviewRepository.existsReviewByAlbumAndUser(album1, user1)).willReturn(true);

        Boolean result = reviewService.existsReview(album1.getId());

        assertThat(result).isNotNull();
        assertThat(result).isTrue();

        verify(albumRepository, times(1)).findById(album1.getId());
        verify(reviewRepository, times(1)).existsReviewByAlbumAndUser(album1, user1);
    }

    @Test
    void existsReview_ReturnsFalse() {
        given(albumRepository.findById(album1.getId())).willReturn(Optional.of(album1));
        given(utilService.getCurrentUser()).willReturn(user1);
        given(reviewRepository.existsReviewByAlbumAndUser(album1, user1)).willReturn(false);

        Boolean result = reviewService.existsReview(album1.getId());

        assertThat(result).isNotNull();
        assertThat(result).isFalse();

        verify(albumRepository, times(1)).findById(album1.getId());
        verify(reviewRepository, times(1)).existsReviewByAlbumAndUser(album1, user1);
    }

    @Test
    void existsReview_WhenAlbumNotFound_ThrowsResourceNotFoundException() {
        given(albumRepository.findById(album1.getId())).willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> reviewService.existsReview(album1.getId()));

        verify(albumRepository, times(1)).findById(album1.getId());
        verify(reviewRepository, never()).existsReviewByAlbumAndUser(album1, user1);
    }

    @Test
    void testCreateReview_Success() {
        given(utilService.getCurrentUser()).willReturn(user1);
        given(albumRepository.findById(album1.getId())).willReturn(Optional.of(album1));
        given(reviewRepository.existsReviewByAlbumAndUser(album1, user1)).willReturn(false);

        CreateReviewDto createReviewDto = CreateReviewDto.builder()
                .title("Title")
                .content("Content")
                .rating(5.0)
                .build();

        ReviewDto result = reviewService.createReview(createReviewDto, album1.getId());

        assertThat(result).isNotNull();

        verify(albumRepository, times(1)).findById(album1.getId());
        verify(reviewRepository, times(1)).existsReviewByAlbumAndUser(album1, user1);
    }

    @Test
    void testCreateReview_WhenAlbumNotFound_ThrowsResourceNotFoundException() {
        given(utilService.getCurrentUser()).willReturn(user1);
        given(albumRepository.findById(album1.getId())).willReturn(Optional.empty());

        CreateReviewDto createReviewDto = CreateReviewDto.builder()
                .title("Title")
                .content("Content")
                .rating(5.0)
                .build();

        assertThrows(ResourceNotFoundException.class, () ->
                reviewService.createReview(createReviewDto, album1.getId()));

        verify(albumRepository, times(1)).findById(album1.getId());
        verify(reviewRepository, never()).existsReviewByAlbumAndUser(album1, user1);
    }

    @Test
    void testCreateReview_WhenReviewAlreadyExists_ThrowsResourceNotFoundException() {
        given(utilService.getCurrentUser()).willReturn(user1);
        given(albumRepository.findById(album1.getId())).willReturn(Optional.of(album1));
        given(reviewRepository.existsReviewByAlbumAndUser(album1, user1)).willReturn(true);

        CreateReviewDto createReviewDto = CreateReviewDto.builder()
                .title("Title")
                .content("Content")
                .rating(5.0)
                .build();

        assertThrows(ResourceAlreadyExistsException.class, () ->
                reviewService.createReview(createReviewDto, album1.getId()));

        verify(albumRepository, times(1)).findById(album1.getId());
        verify(reviewRepository, times(1)).existsReviewByAlbumAndUser(album1, user1);
    }

    @Test
    void testUpdateReview_Success() {
        given(reviewRepository.findById(review1.getId())).willReturn(Optional.of(review1));

        UpdateReviewDto updateReviewDto = UpdateReviewDto.builder()
                .title("New Title")
                .content("New Content")
                .rating(4.5)
                .build();

        ReviewDto result = reviewService.updateReview(updateReviewDto, review1.getId());

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo(updateReviewDto.getTitle());
        assertThat(result.getContent()).isEqualTo(updateReviewDto.getContent());
        assertThat(result.getRating()).isEqualTo(updateReviewDto.getRating());

        verify(reviewRepository, times(1)).findById(review1.getId());
    }

    @Test
    void testUpdateReview_WhenReviewNotFound_ThrowsResourceNotFoundException() {
        given(reviewRepository.findById(review1.getId())).willReturn(Optional.empty());

        UpdateReviewDto updateReviewDto = UpdateReviewDto.builder()
                .title("New Title")
                .content("New Content")
                .rating(4.5)
                .build();

        assertThrows(ResourceNotFoundException.class, () ->
                reviewService.updateReview(updateReviewDto, review1.getId()));

        verify(reviewRepository, times(1)).findById(review1.getId());
    }

//    @Test
//    void testDeleteReview_Success() {
//        given(reviewRepository.findById(review1.getId())).willReturn(Optional.of(review1));
//
//        MessageDto result = reviewService.deleteReview(review1.getId());
//
//        assertThat(result).isNotNull();
//        assertThat(result.getMessage()).isEqualTo("Review deleted.");
//
//        verify(reviewRepository, times(1)).findById(review1.getId());
//        verify(reviewRepository, times(1)).deleteById(review1.getId());
//    }
//
//    @Test
//    void testDeleteReview_WhenReviewNotFound_ThrowsResourceNotFoundException() {
//        given(reviewRepository.findById(review1.getId())).willReturn(Optional.empty());
//
//        assertThrows(ResourceNotFoundException.class, () ->
//                reviewService.deleteReview(review1.getId()));
//
//        verify(reviewRepository, times(1)).findById(review1.getId());
//        verify(reviewRepository, never()).deleteById(review1.getId());
//    }
}
