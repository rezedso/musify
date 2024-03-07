package com.example.musify.repository;

import com.example.musify.entity.AlbumList;
import com.example.musify.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AlbumListRepository extends JpaRepository<AlbumList, UUID> {
    @Query("SELECT DISTINCT al FROM AlbumList al WHERE al.user.id = :userId")
    List<AlbumList> findAlbumListsByUser(@Param("userId") UUID userId);

    @Query("SELECT COUNT(al) FROM AlbumList al WHERE al.user = :user")
    Long getUserListsCount(@Param("user") User user);

    @Modifying
    @Query("DELETE FROM AlbumList al WHERE al.id = :id")
    void deleteAlbumList(@Param("id") UUID id);

    @Query("SELECT al FROM AlbumList al JOIN FETCH " +
            "al.user u WHERE al.name = :listName AND u.username = :username")
    Optional<AlbumList> findByNameAndUserUsername(@Param("listName") String listName, @Param("username") String username);

    @Modifying
    @Query("DELETE FROM AlbumList al WHERE al.user = :user")
    void deleteAllAlbumListsByUser(@Param("user") User user);

    @Modifying
    @Query("DELETE FROM AlbumList al WHERE :artistId IN (SELECT a.artist.id FROM al.albums a)")
    void deleteAlbumsByArtistId(@Param("artistId") UUID artistId);
}
