package com.example.musify.repository;

import com.example.musify.entity.Artist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ArtistRepository extends JpaRepository<Artist, UUID> {
    @Query("SELECT DISTINCT a FROM Artist a LEFT JOIN FETCH a.artistGenres ORDER BY a.name ASC, a.formedYear ASC")
    Page<Artist> findAll(Pageable p);

    @Query("SELECT DISTINCT a FROM Artist a JOIN FETCH a.artistGenres g WHERE g.slug = :slug " +
            "ORDER BY a.formedYear asc")
    Page<Artist>findByGenre(@Param("slug")String slug,Pageable p);

    @Query("SELECT a FROM Artist a WHERE a.name = :artistName")
    Optional<Artist> findByName(@Param("artistName") String artistName);

    @Query("SELECT a FROM Artist a WHERE a.slug = :artistSlug")
    Optional<Artist> findBySlug(@Param("artistSlug") String artistSlug);

    @Query("SELECT a FROM Artist a ORDER BY a.createdAt DESC")
    List<Artist> findMostRecentArtists(Pageable pageable);
}
