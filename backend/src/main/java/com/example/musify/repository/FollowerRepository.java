package com.example.musify.repository;

import com.example.musify.entity.Artist;
import com.example.musify.entity.Follower;
import com.example.musify.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FollowerRepository extends JpaRepository<Follower, UUID> {
    @Query("SELECT f from Follower f WHERE f.artist = :artist")
    List<Follower> findArtistFollowers(@Param("artist") Artist artist);

    @Query("SELECT f.artist FROM Follower f WHERE f.user.username = :username")
    List<Artist> findUserFollowingArtists(@Param("username") String username);

    @Query("SELECT COUNT(f) FROM Follower f WHERE f.artist.id = :artistId")
    Long countArtistFollowers(@Param("artistId") UUID artistId);

    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN TRUE ELSE FALSE END " +
            "FROM Follower f WHERE f.artist = :artist AND f.user = :user")
    Boolean isUserFollowing(@Param("artist") Artist artist, @Param("user") User user);

    @Query("SELECT f FROM Follower f WHERE f.artist.id = :artistId AND f.user.id = :userId")
    Optional<Follower> findByArtistIdAndUserId(@Param("artistId") UUID artistId, @Param("userId") UUID userId);

    @Modifying
    @Query("DELETE FROM Follower f WHERE f.artist.id = :artistId AND f.user.id = :userId")
    void deleteByArtistIdAndUserId(@Param("artistId") UUID artistId, @Param("userId") UUID userId);
}
