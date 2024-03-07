package com.example.musify.repository;

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
public class ArtistRepositoryTests {
    @Autowired
    private ArtistRepository artistRepository;
    @Autowired
    private GenreRepository genreRepository;

    private Genre genre1;
    private Artist artist1;

    @BeforeEach
    void setup() {
        genre1 = Genre.builder()
                .name("Test Genre")
                .slug("test-genre")
                .build();
        genreRepository.save(genre1);

        artist1 = Artist.builder()
                .createdAt(Instant.now())
                .formedYear(Year.of(1999))
                .artistGenres(Set.of(genre1))
                .originCountry("country")
                .name("Artist")
                .slug("artist")
                .build();

        artistRepository.save(artist1);
    }

    @Test
    void testSave() {
        Instant beforeSave = Instant.now();

        Artist newArtist = Artist.builder()
                .createdAt(Instant.now())
                .formedYear(Year.of(1999))
                .artistGenres(Set.of(genre1))
                .name("New Artist")
                .slug("new-artist")
                .originCountry("country")
                .createdAt(Instant.now())
                .build();
        artistRepository.save(newArtist);

        Artist retrievedArtist = artistRepository.findById(newArtist.getId()).orElseThrow();

        assertThat(retrievedArtist).isNotNull();
        assertThat(retrievedArtist.getId()).isInstanceOf(UUID.class);
        assertThat(retrievedArtist.getName()).isEqualTo("New Artist");
        assertThat(retrievedArtist.getSlug()).isEqualTo("new-artist");
        assertThat(retrievedArtist.getOriginCountry()).isEqualTo("country");
        assertThat(retrievedArtist.getFormedYear()).isEqualTo(Year.of(1999));
        assertThat(retrievedArtist.getCreatedAt()).isBetween(beforeSave, Instant.now());
        assertThat(artistRepository.count()).isGreaterThan(0);
    }

    @Test
    void testFindById_Success() {
        Artist retrievedArtist = artistRepository.findById(artist1.getId()).orElseThrow();

        assertThat(retrievedArtist).isNotNull();
        assertThat(retrievedArtist.getName()).isEqualTo("Artist");
        assertThat(retrievedArtist.getSlug()).isEqualTo("artist");
        assertThat(retrievedArtist.getOriginCountry()).isEqualTo("country");
        assertThat(retrievedArtist.getFormedYear()).isEqualTo(Year.of(1999));
    }

    @Test
    void testFindById_WhenArtistNotFound_ThrowsResourceNotFoundException() {
        UUID nonExistentId = UUID.randomUUID();

        assertThrows(ResourceNotFoundException.class, () -> artistRepository.findById(nonExistentId)
                .orElseThrow(() -> new ResourceNotFoundException("Artist not found.")));
    }

    @Test
    void testFindByName_Success() {
        Artist retrievedArtist = artistRepository.findByName(artist1.getName()).orElseThrow();

        assertThat(retrievedArtist).isNotNull();
        assertThat(retrievedArtist.getName()).isEqualTo("Artist");
        assertThat(retrievedArtist.getSlug()).isEqualTo("artist");
        assertThat(retrievedArtist.getOriginCountry()).isEqualTo("country");
        assertThat(retrievedArtist.getFormedYear()).isEqualTo(Year.of(1999));
    }

    @Test
    void testFindByName_WhenArtistNotFound_ThrowsResourceNotFoundException() {
        assertThrows(ResourceNotFoundException.class, () -> artistRepository.findByName("nonExistentName")
                .orElseThrow(() -> new ResourceNotFoundException("Artist not found.")));
    }

    @Test
    void testFindBySlug_Success() {
        Artist retrievedArtist = artistRepository.findBySlug(artist1.getSlug()).orElseThrow();

        assertThat(retrievedArtist).isNotNull();
        assertThat(retrievedArtist.getName()).isEqualTo("Artist");
        assertThat(retrievedArtist.getSlug()).isEqualTo("artist");
        assertThat(retrievedArtist.getOriginCountry()).isEqualTo("country");
        assertThat(retrievedArtist.getFormedYear()).isEqualTo(Year.of(1999));
    }

    @Test
    void testFindBySlug_WhenArtistNotFound_ThrowsResourceNotFoundException() {
        assertThrows(ResourceNotFoundException.class, () -> artistRepository.findBySlug("non-existent-slug")
                .orElseThrow(() -> new ResourceNotFoundException("Artist not found.")));
    }

    @Test
    void testFindAll() {
        PageRequest pageRequest = PageRequest.of(0, 20, Sort.by("formedYear").ascending());
        Page<Artist> artistsPage = artistRepository.findAll(pageRequest);

        assertThat(artistsPage).isNotNull();
        assertThat(artistsPage.getContent().size()).isGreaterThan(0);
    }

    @Test
    void testFindAllByGenreSlug() {
        PageRequest pageRequest = PageRequest.of(0, 20, Sort.by("formedYear").ascending());
        Page<Artist> artistsPage = artistRepository.findByGenre(genre1.getSlug(), pageRequest);

        assertThat(artistsPage).isNotNull();
        assertThat(artistsPage.getContent().size()).isGreaterThan(0);
    }

    @Test
    void testFindMostRecentArtists() {
        PageRequest pageRequest = PageRequest.of(0, 20, Sort.by("createdAt").descending());
        List<Artist> artistsPage = artistRepository.findMostRecentArtists(pageRequest);

        assertThat(artistsPage).isNotNull();
        assertThat(artistsPage.size()).isGreaterThan(0);
    }

    @Test
    void testDeleteArtist() {
        long artistsCount = artistRepository.count();

        artistRepository.delete(artist1);

        Optional<Artist> retrievedArtist = artistRepository.findById(artist1.getId());

        assertThat(artistRepository.count()).isEqualTo(artistsCount - 1);
        assertThat(retrievedArtist).isEmpty();
    }
}
