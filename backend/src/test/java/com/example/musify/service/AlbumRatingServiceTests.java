package com.example.musify.service;

import com.example.musify.dto.response.*;
import com.example.musify.entity.*;
import com.example.musify.exception.ResourceNotFoundException;
import com.example.musify.repository.*;
import com.example.musify.service.impl.AlbumRatingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.time.Year;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AlbumRatingServiceTests {
    @Mock
    private AlbumRepository albumRepository;
    @Mock
    private AlbumRatingRepository albumRatingRepository;
    @Mock
    private ArtistRepository artistRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private GenreRepository genreRepository;
    @Mock
    private IUtilService utilService;
    @Spy
    private ModelMapper modelMapper;

    @InjectMocks
    private AlbumRatingServiceImpl albumRatingService;

    private Album album1;
    private User user1;
    private AlbumRating albumRating1;

    @BeforeEach
    void setup() {
        Artist artist = Artist.builder()
                .name("artist")
                .formedYear(Year.of(2010))
                .originCountry("country")
                .createdAt(Instant.now())
                .slug("artist")
                .build();
        artistRepository.save(artist);

        Genre genre1 = Genre.builder()
                .name("Test Genre")
                .slug("test-genre")
                .build();
        genreRepository.save(genre1);

        album1 = Album.builder()
                .title("Test 1")
                .slug("test-1")
                .artist(artist)
                .originCountry("country")
                .createdAt(Instant.now())
                .rating(4.5)
                .build();
        albumRepository.save(album1);

        user1 = User.builder()
                .username("testuser")
                .email("user@test.com")
                .password("password")
                .build();

        userRepository.save(user1);

        albumRating1 = AlbumRating.builder()
                .album(album1)
                .user(user1)
                .createdAt(Instant.now())
                .rating(5.0)
                .build();
        albumRatingRepository.save(albumRating1);
    }

    @Test
    void testGetAlbumRating_Success() {
        given(albumRepository.findById(album1.getId())).willReturn(Optional.of(album1));
        given(albumRatingRepository.countAlbumRatingsByAlbum(album1)).willReturn(1L);
        given(albumRatingRepository.averageAlbumRatingByAlbum(album1)).willReturn(album1.getRating());

        AlbumRatingStatsDto result = albumRatingService.getAlbumRating(album1.getId());

        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(AlbumRatingStatsDto.class);
        assertThat(result.getRating()).isEqualTo(4.5);
        assertThat(result.getTotalRatings()).isEqualTo(1);

        verify(albumRepository, times(1)).findById(album1.getId());
        verify(albumRatingRepository, times(1)).countAlbumRatingsByAlbum(album1);
        verify(albumRatingRepository, times(1)).averageAlbumRatingByAlbum(album1);
    }

    @Test
    void testGetAlbumRating_WhenAlbumNotFoundThrowsResourceNotFoundException() {
        given(albumRepository.findById(album1.getId())).willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                albumRatingService.getAlbumRating(album1.getId()));

        verify(albumRepository, times(1)).findById(album1.getId());
        verify(albumRatingRepository, never()).countAlbumRatingsByAlbum(album1);
        verify(albumRatingRepository, never()).averageAlbumRatingByAlbum(album1);
    }

    @Test
    void testGetMostRecentAlbumRatings() {
        given(albumRatingRepository.findMostRecentRatings(any(Pageable.class))).willReturn(List.of(albumRating1));

        List<RecentAlbumRatingDto> result = albumRatingService.getMostRecentAlbumRatings();

        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0)).isInstanceOf(RecentAlbumRatingDto.class);

        verify(albumRatingRepository, times(1)).findMostRecentRatings(any(Pageable.class));
    }

    @Test
    void testGetAlbumRatingsByUser() {
        given(albumRatingRepository.findByUser(user1.getId())).willReturn(List.of(albumRating1));

        List<AlbumRatingSummaryDto> result = albumRatingService.getAlbumRatingsByUser(user1.getId());

        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0)).isInstanceOf(AlbumRatingSummaryDto.class);

        verify(albumRatingRepository, times(1)).findByUser(user1.getId());
    }

    @Test
    void testGetUserAlbumRating() {
        given(utilService.getCurrentUser()).willReturn(user1);
        given(albumRatingRepository.findRatingByUserAndAlbum(user1.getId(), album1.getId()))
                .willReturn(albumRating1.getRating());

        UserRatingDto result = albumRatingService.getUserAlbumRating(album1.getId());

        assertThat(result).isNotNull();
        assertThat(result.getRating()).isEqualTo(5.0);
        assertThat(result).isInstanceOf(UserRatingDto.class);

        verify(albumRatingRepository, times(1)).findRatingByUserAndAlbum(user1.getId(), album1.getId());
    }

    @Test
    void testGetAlbumRatingsByUserAndRating() {
        given(albumRatingRepository.findByUserAndRating(user1.getUsername(), 5.0)).willReturn(List.of(albumRating1));

        List<AlbumRatingCollectionDto> result = albumRatingService.getAlbumRatingsByUserAndRating(user1.getUsername(), 5.0);

        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0)).isInstanceOf(AlbumRatingCollectionDto.class);

        verify(albumRatingRepository, times(1)).findByUserAndRating(user1.getUsername(), 5.0);
    }

    @Test
    void testGetUserGenreOverview_Success() {
        given(userRepository.findByUsername(user1.getUsername())).willReturn(Optional.of(user1));

        given(albumRatingRepository.getUserGenreOverview(eq(user1.getUsername()), any(Pageable.class)))
                .willReturn(List.of(new GenreAlbumCountDto()));

        List<GenreAlbumCountDto> result = albumRatingService.getUserGenreOverview(user1.getUsername());

        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0)).isInstanceOf(GenreAlbumCountDto.class);

        verify(userRepository, times(1)).findByUsername(user1.getUsername());
        verify(albumRatingRepository, times(1)).getUserGenreOverview(eq(user1.getUsername()), any(Pageable.class));
    }

    @Test
    void testGetUserGenreOverview_WhenUserNotFound_ThrowsResourceNotFoundException() {
        given(userRepository.findByUsername(user1.getUsername())).willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                albumRatingService.getUserGenreOverview(user1.getUsername()));

        verify(userRepository, times(1)).findByUsername(user1.getUsername());
        verify(albumRatingRepository, never()).getUserGenreOverview(eq(user1.getUsername()), any(Pageable.class));
    }
}
