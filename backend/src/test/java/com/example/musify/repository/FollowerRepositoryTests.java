package com.example.musify.repository;

import com.example.musify.entity.Artist;
import com.example.musify.entity.Follower;
import com.example.musify.entity.Genre;
import com.example.musify.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.Instant;
import java.time.Year;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class FollowerRepositoryTests {
    @Autowired
    private ArtistRepository artistRepository;
    @Autowired
    private GenreRepository genreRepository;
    @Autowired
    private FollowerRepository followerRepository;
    @Autowired
    private UserRepository userRepository;

    private Artist artist1;
    private User user1;

    @BeforeEach
    void setup() {
        Genre genre1 = Genre.builder()
                .name("Test Genre")
                .slug("test-genre")
                .build();
        genreRepository.save(genre1);

        artist1 = Artist.builder()
                .createdAt(Instant.now())
                .formedYear(Year.of(1999))
                .artistGenres(Set.of(genre1))
                .originCountry("country")
                .artistGenres(Set.of(genre1))
                .name("Artist")
                .slug("artist")
                .build();
        artistRepository.save(artist1);

        user1 = User.builder()
                .username("testuser")
                .email("user@test.com")
                .password("password")
                .build();
        userRepository.save(user1);

        Follower follower1 = Follower.builder()
                .artist(artist1)
                .user(user1)
                .build();
        followerRepository.save(follower1);
    }

    @Test
    void testSaveFollower() {
        Follower newFollower = Follower.builder()
                .artist(artist1)
                .user(user1)
                .build();
        followerRepository.save(newFollower);

        Follower retrievedFollower = followerRepository.findById(newFollower.getId()).orElseThrow();

        assertThat(retrievedFollower).isNotNull();
        assertThat(retrievedFollower.getArtist()).isEqualTo(artist1);
        assertThat(retrievedFollower.getUser()).isEqualTo(user1);
        assertThat(followerRepository.count()).isGreaterThan(0);
    }

    @Test
    void testIsUserFollowing() {
        Boolean isFollowing = followerRepository.isUserFollowing(artist1, user1);

        assertThat(isFollowing).isNotNull();
        assertThat(isFollowing).isTrue();
    }

    @Test
    void testFindUserFollowingArtists() {
        List<Artist> artistsList = followerRepository.findUserFollowingArtists(user1.getUsername());

        assertThat(artistsList).isNotNull();
        assertThat(artistsList.size()).isGreaterThan(0);
    }

    @Test
    void testCountArtistFollowers() {
        Long followersCount = followerRepository.countArtistFollowers(artist1.getId());

        assertThat(followersCount).isNotNull();
        assertThat(followersCount).isGreaterThan(0);
    }

    @Test
    void testFindByArtistAndUser() {
        Optional<Follower> follower = followerRepository.findByArtistIdAndUserId(artist1.getId(), user1.getId());

        assertThat(follower).isNotNull();
    }

    @Test
    void testFindArtistFollowers() {
        List<Follower> followers = followerRepository.findArtistFollowers(artist1);

        assertThat(followers).isNotNull();
        assertThat(followers.size()).isGreaterThan(0);
    }

    @Test
    void testDeleteByArtistAndUser() {
        long followersCount = followerRepository.count();

        followerRepository.deleteByArtistIdAndUserId(artist1.getId(), user1.getId());

        Optional<Follower> retrievedFollower = followerRepository.findByArtistIdAndUserId(artist1.getId(), user1.getId());
        assertThat(followerRepository.count()).isEqualTo(followersCount - 1);
        assertThat(retrievedFollower).isEmpty();
    }
}
