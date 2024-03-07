package com.example.musify.repository;

import com.example.musify.entity.Album;
import com.example.musify.entity.Artist;
import com.example.musify.entity.Genre;
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
import java.time.Year;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class AlbumRepositoryTests {
    @Autowired
    private AlbumRepository albumRepository;
    @Autowired
    private ArtistRepository artistRepository;
    @Autowired
    private GenreRepository genreRepository;

    private Genre genre1;
    private Album album1;
    private Album album2;
    private Artist artist;

    @BeforeEach
    void setup() {
        genre1 = Genre.builder()
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

        album2 = Album.builder()
                .title("Test 2")
                .albumGenres(Set.of(genre1))
                .slug("test-2")
                .originCountry("country")
                .createdAt(Instant.now())
                .rating(4.0)
                .build();
        albumRepository.saveAll(List.of(album1, album2));

        artist = Artist.builder()
                .createdAt(Instant.now())
                .formedYear(Year.of(1999))
                .artistGenres(Set.of(genre1))
                .name("Artist")
                .slug("artist")
                .build();
        artistRepository.save(artist);
    }

    @Test
    void testSaveAlbum() {
        Instant beforeSave = Instant.now();

        Album newAlbum = Album.builder()
                .title("Test New")
                .albumGenres(Set.of(genre1))
                .slug("test-new")
                .originCountry("country")
                .createdAt(beforeSave)
                .rating(5.0)
                .build();

        albumRepository.save(newAlbum);

        Album retrievedAlbum = albumRepository.findById(newAlbum.getId()).orElseThrow();

        assertThat(retrievedAlbum).isNotNull();
        assertThat(retrievedAlbum.getId()).isInstanceOf(UUID.class);
        assertThat(retrievedAlbum.getTitle()).isEqualTo("Test New");
        assertThat(retrievedAlbum.getSlug()).isEqualTo("test-new");
        assertThat(retrievedAlbum.getOriginCountry()).isEqualTo("country");
        assertThat(retrievedAlbum.getRating()).isEqualTo(5.0);
        assertThat(retrievedAlbum.getAlbumGenres()).contains(genre1);
        assertThat(retrievedAlbum.getCreatedAt()).isBetween(beforeSave, Instant.now());
        assertThat(albumRepository.count()).isGreaterThan(0);
    }

    @Test
    void testFindById_Success() {
        Album retrievedAlbum = albumRepository.findById(album1.getId()).orElseThrow();

        assertThat(retrievedAlbum).isNotNull();
        assertThat(retrievedAlbum.getTitle()).isEqualTo("Test 1");
        assertThat(retrievedAlbum.getSlug()).isEqualTo("test-1");
    }

    @Test
    void testFindById_WhenAlbumNotFound_ThrowsResourceNotFoundException() {
        UUID nonExistentId = UUID.randomUUID();

        assertThrows(ResourceNotFoundException.class, () -> albumRepository.findById(nonExistentId)
                .orElseThrow(() -> new ResourceNotFoundException("Album not found.")));
    }

    @Test
    void testFindAll() {
        PageRequest pageRequest = PageRequest.of(0, 20, Sort.by("title").descending());
        Page<Album> albumsPage = albumRepository.findAll(pageRequest);

        assertThat(albumsPage).isNotNull();
        assertThat(albumsPage.getContent().size()).isGreaterThan(0);
    }

    @Test
    void testFindByArtistSlugAndAlbumSlug() {
        album2.setArtist(artist);
        Optional<Album> optionalAlbum = albumRepository.findByArtistSlugAndAlbumSlug(artist.getSlug(), album2.getSlug());

        assertThat(optionalAlbum).isPresent();
        Album album = optionalAlbum.get();

        assertThat(album).isNotNull();
        assertThat(album.getTitle()).isEqualTo("Test 2");
        assertThat(album.getSlug()).isEqualTo("test-2");
    }

    @Test
    void testFindMostRecentAlbums() {
        Pageable pageable = PageRequest.of(0, 4);

        List<Album> recentAlbums = albumRepository.findMostRecentAlbums(pageable);

        assertThat(recentAlbums).isNotNull();
        assertThat(recentAlbums.size()).isGreaterThan(0);
    }

    @Test
    void testFindByArtist() {
        album1.setArtist(artist);
        List<Album> retrievedAlbums = albumRepository.findByArtist(artist);

        assertThat(retrievedAlbums).isNotNull();
        assertThat(retrievedAlbums).hasSizeGreaterThan(0);
    }

    @Test
    void testFindByGenreSlug() {
        Pageable pageable = PageRequest.of(0, 2);

        Page<Album> albumsPage = albumRepository.findByGenreSlug(genre1.getSlug(), pageable);

        assertThat(albumsPage).isNotNull();
        assertThat(albumsPage.getContent().size()).isGreaterThan(0);
    }

    @Test
    void testDeleteAlbum() {
        long albumsCount = albumRepository.count();

        albumRepository.delete(album1);

        Optional<Album> retrievedAlbum = albumRepository.findById(album1.getId());

        assertThat(albumRepository.count()).isEqualTo(albumsCount - 1);
        assertThat(retrievedAlbum).isEmpty();
    }
}
