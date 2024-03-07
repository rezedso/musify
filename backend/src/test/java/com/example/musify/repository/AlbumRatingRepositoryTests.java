package com.example.musify.repository;

import com.example.musify.dto.response.GenreAlbumCountDto;
import com.example.musify.entity.*;
import com.example.musify.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.time.Year;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class AlbumRatingRepositoryTests {
    @Autowired
    private AlbumRepository albumRepository;
    @Autowired
    private AlbumRatingRepository albumRatingRepository;
    @Autowired
    private ArtistRepository artistRepository;
    @Autowired
    private GenreRepository genreRepository;
    @Autowired
    private UserRepository userRepository;

    private Album album1;
    private User user1;
    private User user2;
    private AlbumRating albumRating1;

    @BeforeEach
    void setup() {
        Genre genre1 = Genre.builder()
                .name("Test Genre")
                .slug("test-genre")
                .build();
        genreRepository.save(genre1);

        album1 = Album.builder()
                .title("Test 1")
                .albumGenres(Set.of(genre1))
                .slug("test-1")
                .originCountry("country")
                .createdAt(Instant.now())
                .rating(4.5)
                .build();

        Album album2 = Album.builder()
                .title("Test 2")
                .albumGenres(Set.of(genre1))
                .slug("test-2")
                .originCountry("country")
                .createdAt(Instant.now())
                .rating(4.0)
                .build();
        albumRepository.saveAll(List.of(album1, album2));

        Artist artist = Artist.builder()
                .createdAt(Instant.now())
                .formedYear(Year.of(1999))
                .artistGenres(Set.of(genre1))
                .name("Artist")
                .slug("artist")
                .build();
        artistRepository.save(artist);

        user1 = User.builder()
                .username("testuser")
                .email("user@test.com")
                .password("password")
                .build();

        user2 = User.builder()
                .username("testuser2")
                .email("user2@test.com")
                .password("password")
                .build();
        userRepository.saveAll(List.of(user1, user2));

        albumRating1 = AlbumRating.builder()
                .user(user1)
                .createdAt(Instant.now())
                .album(album1)
                .rating(5.0)
                .build();
        albumRatingRepository.save(albumRating1);

        AlbumRating albumRating2 = AlbumRating.builder()
                .user(user1)
                .createdAt(Instant.now().minusSeconds(1))
                .album(album2)
                .rating(5.0)
                .build();
        albumRatingRepository.save(albumRating2);
    }

    @Test
    void testSaveAlbumRating() {
        Instant beforeSave = Instant.now();

        AlbumRating newAlbumRating = AlbumRating.builder()
                .user(user1)
                .createdAt(beforeSave)
                .album(album1)
                .rating(5.0)
                .build();

        albumRatingRepository.save(newAlbumRating);

        AlbumRating retrievedAlbumRating = albumRatingRepository.findById(newAlbumRating.getId()).orElseThrow();

        assertThat(retrievedAlbumRating).isNotNull();
        assertThat(retrievedAlbumRating.getId()).isInstanceOf(UUID.class);
        assertThat(retrievedAlbumRating.getAlbum()).isEqualTo(album1);
        assertThat(retrievedAlbumRating.getRating()).isEqualTo(5.0);
        assertThat(retrievedAlbumRating.getCreatedAt()).isBetween(beforeSave, Instant.now());
        assertThat(albumRatingRepository.count()).isGreaterThan(0);
    }

    @Test
    void testFindById_Success() {
        AlbumRating retrievedAlbum = albumRatingRepository.findById(albumRating1.getId()).orElseThrow();

        assertThat(retrievedAlbum).isNotNull();
        assertThat(retrievedAlbum.getAlbum()).isEqualTo(album1);
        assertThat(retrievedAlbum.getRating()).isEqualTo(5.0);
        assertThat(retrievedAlbum.getUser()).isEqualTo(user1);
    }

    @Test
    void testFindById_WhenAlbumRatingNotFound_ThrowsResourceNotFoundException() {
        UUID nonExistentId = UUID.randomUUID();

        assertThrows(ResourceNotFoundException.class, () -> albumRatingRepository.findById(nonExistentId)
                .orElseThrow(() -> new ResourceNotFoundException("Album Rating not found.")));
    }

    @Test
    void testFindByUserAndAlbum() {
        AlbumRating albumRating = albumRatingRepository.findByAlbumAndUser(album1, user1);

        assertThat(albumRating).isNotNull();
        assertThat(albumRating.getUser()).isEqualTo(user1);
        assertThat(albumRating.getAlbum()).isEqualTo(album1);
        assertThat(albumRating.getRating()).isEqualTo(5.0);
    }

    @Test
    void testCountAlbumRatingsByAlbum() {
        AlbumRating albumRating = AlbumRating.builder()
                .user(user2)
                .createdAt(Instant.now())
                .album(album1)
                .rating(5.0)
                .build();
        albumRatingRepository.save(albumRating);

        Long countByAlbum = albumRatingRepository.countAlbumRatingsByAlbum(album1);

        assertThat(countByAlbum).isNotNull();
        assertThat(countByAlbum).isEqualTo(2);
    }

    @Test
    void testGetAverageAlbumRatingByAlbum() {
        Double averageRating = albumRatingRepository.averageAlbumRatingByAlbum(album1);

        assertThat(averageRating).isNotNull();
        assertThat(averageRating).isEqualTo(5.0);
    }

    @Test
    void testFindByUser() {
        List<AlbumRating> retrievedAlbumRatings = albumRatingRepository.findByUser(user1.getId());

        assertThat(retrievedAlbumRatings).isNotNull();
        assertThat(retrievedAlbumRatings.size()).isEqualTo(2);
    }

    @Test
    void testFindRatingByUserAndAlbum() {
        Double rating = albumRatingRepository.findRatingByUserAndAlbum(user1.getId(), album1.getId());

        assertThat(rating).isNotNull();
        assertThat(rating).isEqualTo(5.0);
    }

    @Test
    void testFindByUserAndRating() {
        List<AlbumRating> retrievedAlbumRatings = albumRatingRepository.findByUserAndRating("testuser", 5.0);

        assertThat(retrievedAlbumRatings).isNotNull();
        assertThat(retrievedAlbumRatings.size()).isEqualTo(2);
    }

    @Test
    void testGetUserGenreOverview() {
        Pageable pageable = PageRequest.of(0, 2);
        List<GenreAlbumCountDto> genreOverview = albumRatingRepository
                .getUserGenreOverview("testuser", pageable);

        assertThat(genreOverview).isNotNull();
    }
}
