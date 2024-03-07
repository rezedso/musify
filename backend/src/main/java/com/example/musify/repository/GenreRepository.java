package com.example.musify.repository;

import com.example.musify.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface GenreRepository extends JpaRepository<Genre, UUID> {
    @Query("SELECT g FROM Genre g WHERE g.name = :genreName")
    Optional<Genre> findByName(@Param("genreName") String genreName);

    @Query("SELECT g FROM Genre g WHERE g.slug = :slug")
    Optional<Genre> findBySlug(String slug);

    @Query("SELECT g FROM Genre g WHERE g.name IN :genreNames")
    List<Genre> findByNameIn(@Param("genreNames") Set<String> genreNames);
}
