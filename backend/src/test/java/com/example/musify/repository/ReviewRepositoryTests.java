package com.example.musify.repository;

import com.example.musify.entity.Album;
import com.example.musify.entity.Genre;
import com.example.musify.entity.Review;
import com.example.musify.entity.User;
import com.example.musify.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ReviewRepositoryTests {
    @Autowired
    private AlbumRepository albumRepository;
    @Autowired
    private GenreRepository genreRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ReviewRepository reviewRepository;

    private Album album1;
    private User user1;
    private User user2;
    private Review review1;
    private Review review2;

    @BeforeEach
    void setup() {
        Genre genre1 = Genre.builder()
                .name("Test Genre")
                .slug("test-genre")
                .build();
        genreRepository.save(genre1);

        album1 = Album.builder()
                .title("Album 1")
                .albumGenres(Set.of(genre1))
                .slug("album-1")
                .originCountry("country")
                .createdAt(Instant.now())
                .rating(4.5)
                .build();

        albumRepository.save(album1);

        user1 = User.builder()
                .username("reviewUser")
                .email("reviewuser@test.com")
                .password("password")
                .build();
        userRepository.save(user1);

        user2 = User.builder()
                .username("reviewUser2")
                .email("reviewuser2@test.com")
                .password("password")
                .build();
        userRepository.save(user2);

        review1 = Review.builder()
                .title("Review 1")
                .content("Content 1")
                .album(album1)
                .createdAt(Instant.now())
                .user(user1)
                .rating(5.0)
                .build();

        review2 = Review.builder()
                .title("Review 2")
                .content("Content 2")
                .album(album1)
                .createdAt(Instant.now())
                .user(user2)
                .rating(5.0)
                .build();

        reviewRepository.saveAll(List.of(review1, review2));
    }

    @Test
    void testSaveReview() {
        Instant beforeSave = Instant.now();
        Review newReview = Review.builder()
                .title("Review")
                .content("Content")
                .album(album1)
                .createdAt(beforeSave)
                .user(user1)
                .rating(5.0)
                .build();

        reviewRepository.save(newReview);

        Review retrievedReview = reviewRepository.findById(newReview.getId()).orElseThrow();

        assertThat(retrievedReview).isNotNull();
        assertThat(retrievedReview.getTitle()).isEqualTo("Review");
        assertThat(retrievedReview.getContent()).isEqualTo("Content");
        assertThat(retrievedReview.getUser()).isEqualTo(user1);
        assertThat(retrievedReview.getAlbum()).isEqualTo(album1);
        assertThat(retrievedReview.getRating()).isEqualTo(5.0);
        assertThat(retrievedReview.getCreatedAt()).isBetween(beforeSave, Instant.now());
    }

    @Test
    void testFindById_Success() {
        Review retrievedReview = reviewRepository.findById(review1.getId()).orElseThrow();

        assertThat(retrievedReview).isNotNull();
        assertThat(retrievedReview.getTitle()).isEqualTo("Review 1");
        assertThat(retrievedReview.getContent()).isEqualTo("Content 1");
    }

    @Test
    void testFindById_WhenReviewNotFound_ThrowsResourceNotFoundException() {
        UUID nonExistentId = UUID.randomUUID();

        assertThrows(ResourceNotFoundException.class, () -> reviewRepository.findById(nonExistentId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found.")));
    }

    @Test
    void testFindByUser() {
        PageRequest pageRequest = PageRequest.of(0, 20, Sort.by("createdAt").descending());
        Page<Review> reviewsPage = reviewRepository.findByUser(user2.getId(), pageRequest);

        assertThat(reviewsPage).isNotNull();
        assertThat(reviewsPage.getContent().size()).isGreaterThan(0);
    }

    @Test
    void testFindByAlbum() {
        PageRequest pageRequest = PageRequest.of(0, 20, Sort.by("createdAt").descending());
        Page<Review> reviewsPage = reviewRepository.findByAlbum(album1, pageRequest);

        assertThat(reviewsPage).isNotNull();
        assertThat(reviewsPage.getContent().size()).isGreaterThan(0);
    }

    @Test
    void testFindMostRecentReviews() {
        Pageable pageable = PageRequest.of(0, 4);

        List<Review> recentReviews = reviewRepository.findMostRecentReviews(pageable);

        assertThat(recentReviews).isNotNull();
        assertThat(recentReviews.size()).isGreaterThan(0);
    }

    @Test
    void testExistsReviewByAlbumAndUser() {
        Boolean existsReview = reviewRepository.existsReviewByAlbumAndUser(album1, user1);

        assertThat(existsReview).isNotNull();
        assertThat(existsReview).isTrue();
    }

    @Test
    void testDeleteReviewById_Success() {
        long reviewsCount = reviewRepository.count();

        reviewRepository.deleteById(review2.getId());

        Optional<Review> retrievedReview = reviewRepository.findById(review2.getId());

        assertThat(reviewRepository.count()).isEqualTo(reviewsCount - 1);
        assertThat(retrievedReview).isEmpty();
    }

    @Test
    void testDeleteReviewById_WhenReviewNotFound_ThrowsResourceNotFoundException() {
        UUID nonExistentId = UUID.randomUUID();

        assertThrows(ResourceNotFoundException.class, () -> reviewRepository.findById(nonExistentId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found.")));
    }
}
