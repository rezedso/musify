package com.example.musify.repository;

import com.example.musify.entity.Album;
import com.example.musify.entity.Artist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AlbumRepository extends JpaRepository<Album, UUID> {
    @Query("SELECT DISTINCT a FROM Album a " +
            "LEFT JOIN FETCH a.albumGenres " +
            "ORDER BY a.rating DESC")
    Page<Album> findAll(Pageable p);

    @Query("SELECT DISTINCT a FROM Album a WHERE a.artist = :artist ORDER BY a.releaseDate asc")
    List<Album> findByArtist(@Param("artist") Artist artist);

    @Query("SELECT a FROM Album a ORDER BY a.createdAt DESC")
    List<Album> findMostRecentAlbums(Pageable pageable);

    @Modifying
    @Query("DELETE FROM Album a WHERE a.id = :albumId")
    void deleteById(@Param("albumId") UUID albumId);

    @Modifying
    @Query("DELETE FROM Album a WHERE a.artist.id = :artistId")
    void deleteByArtist(@Param("artistId") UUID artistId);

    @Query("SELECT a FROM Album a WHERE a.artist.slug = :artistSlug AND a.slug = :albumSlug")
    Optional<Album> findByArtistSlugAndAlbumSlug(String artistSlug, String albumSlug);

    @Query("SELECT DISTINCT a FROM Album a JOIN a.albumGenres g WHERE g.slug = :slug ORDER BY a.rating DESC")
    Page<Album> findByGenreSlug(@Param("slug") String slug, Pageable p);
}
