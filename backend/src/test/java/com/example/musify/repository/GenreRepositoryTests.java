package com.example.musify.repository;

import com.example.musify.entity.Genre;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class GenreRepositoryTests {
    @Autowired
    private GenreRepository genreRepository;

    private Genre genre1;
    private Genre genre2;

    @BeforeEach
    void setup() {
        genre1 = Genre.builder()
                .name("Genre 1")
                .slug("genre-1")
                .build();

        genre2 = Genre.builder()
                .name("Genre 2")
                .slug("genre-2")
                .build();
    }

    @Test
    void testSaveGenre() {
        Genre newGenre = Genre.builder()
                .name("New Genre")
                .slug("new-genre")
                .build();
        genreRepository.save(newGenre);

        Genre retrievedGenre = genreRepository.findById(newGenre.getId()).orElseThrow();

        assertThat(retrievedGenre).isNotNull();
        assertThat(retrievedGenre.getName()).isEqualTo("New Genre");
        assertThat(retrievedGenre.getSlug()).isEqualTo("new-genre");
        assertThat(genreRepository.count()).isGreaterThan(0);
    }

    @Test
    void testSaveAllGenres() {
        long genreCountBeforeSave = genreRepository.count();

        genreRepository.saveAll(List.of(genre1, genre2));

        long genreCountAfterSave = genreRepository.count();

        assertThat(genreCountAfterSave).isEqualTo(genreCountBeforeSave + 2);
    }

    @Test
    void testFindGenreByName() {
        genreRepository.save(genre1);
        Optional<Genre> genreOptional = genreRepository.findByName(genre1.getName());

        assertThat(genreOptional).isPresent();
        Genre genre = genreOptional.get();
        assertThat(genre.getName()).isEqualTo("Genre 1");
        assertThat(genre.getSlug()).isEqualTo("genre-1");
    }

    @Test
    void testFindGenreByName_WhenNotFound() {
        Optional<Genre> genreOptional = genreRepository.findByName("Non-existent Genre");

        assertThat(genreOptional).isEmpty();
    }

    @Test
    void testFindGenreBySlug() {
        genreRepository.save(genre1);
        Optional<Genre> genreOptional = genreRepository.findBySlug(genre1.getSlug());

        assertThat(genreOptional).isPresent();
        Genre genre = genreOptional.get();
        assertThat(genre.getName()).isEqualTo("Genre 1");
        assertThat(genre.getSlug()).isEqualTo("genre-1");
    }

    @Test
    void testFindGenreBySlug_WhenNotFound() {
        Optional<Genre> genreOptional = genreRepository.findBySlug("Non-existent Slug");

        assertThat(genreOptional).isEmpty();
    }

    @Test
    void testFindGenresByNameIn() {
        genreRepository.saveAll(List.of(genre1, genre2));

        Set<String> genreNames = Set.of("Genre 1", "Genre 2", "Non-existent Genre");

        List<Genre> genres = genreRepository.findByNameIn(genreNames);

        assertThat(genres).isNotNull();
        assertThat(genres.size()).isEqualTo(2);
        assertThat(genres).extracting(Genre::getName).containsExactlyInAnyOrder("Genre 1", "Genre 2");
    }
}
