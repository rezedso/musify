package com.example.musify.repository;

import com.example.musify.dto.response.GenreAlbumCountDto;
import com.example.musify.entity.Album;
import com.example.musify.entity.AlbumRating;
import com.example.musify.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface AlbumRatingRepository extends JpaRepository<AlbumRating, UUID> {
    @Query("SELECT ar from AlbumRating ar WHERE ar.album = :album and ar.user = :user")
    AlbumRating findByAlbumAndUser(@Param("album") Album album, @Param("user") User user);

    @Query("SELECT AVG(ar.rating) FROM AlbumRating ar WHERE ar.album = :album")
    Double averageAlbumRatingByAlbum(@Param("album") Album album);

    @Query("SELECT COUNT(ar) FROM AlbumRating ar WHERE ar.album = :album")
    Long countAlbumRatingsByAlbum(@Param("album") Album album);

    @Query("SELECT ar from AlbumRating ar WHERE ar.user.id = :userId ORDER BY ar.createdAt DESC")
    List<AlbumRating> findByUser(@Param("userId") UUID userId);

    @Query("SELECT ar.rating from AlbumRating ar WHERE ar.user.id = :userId and ar.album.id = :albumId")
    Double findRatingByUserAndAlbum(@Param("userId") UUID userId, @Param("albumId") UUID albumId);

    @Query("SELECT ar FROM AlbumRating ar " +
            "WHERE ar.user.username = :username AND ar.rating = :rating " +
            "ORDER BY ar.album.title")
    List<AlbumRating> findByUserAndRating(@Param("username") String username, @Param("rating") Double rating);

    @Query("SELECT ar FROM AlbumRating ar ORDER BY ar.createdAt DESC")
    List<AlbumRating> findMostRecentRatings(Pageable pageable);

    @Query("SELECT new com.example.musify.dto.response.GenreAlbumCountDto(g.name, COUNT(a.id)) " +
            "FROM AlbumRating ar " +
            "JOIN ar.album a " +
            "JOIN a.albumGenres g " +
            "WHERE ar.user.username = :username " +
            "GROUP BY g.name " +
            "ORDER BY COUNT(a.id) DESC")
    List<GenreAlbumCountDto> getUserGenreOverview(@Param("username") String username, Pageable pageable);

}
