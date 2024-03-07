package com.example.musify.repository;

import com.example.musify.entity.Album;
import com.example.musify.entity.AlbumList;
import com.example.musify.entity.Artist;
import com.example.musify.entity.User;
import com.example.musify.exception.ResourceNotFoundException;
import jakarta.persistence.EntityManager;
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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class AlbumListRepositoryTests {
    @Autowired
    private AlbumListRepository albumListRepository;
    @Autowired
    private AlbumRepository albumRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ArtistRepository artistRepository;
    @Autowired
    private EntityManager entityManager;

    private User user1;
    private Album album1;
    private Album album2;
    private Artist artist;
    private AlbumList albumList1;

    @BeforeEach
    void setup() {
        artist = Artist.builder()
                .name("artist")
                .formedYear(Year.of(2010))
                .originCountry("country")
                .createdAt(Instant.now())
                .slug("artist")
                .build();
        artistRepository.save(artist);

        album1 = Album.builder()
                .title("Test 1")
                .slug("test-1")
                .artist(artist)
                .originCountry("country")
                .createdAt(Instant.now())
                .rating(4.5)
                .build();

        album2 = Album.builder()
                .title("Test 2")
                .artist(artist)
                .slug("test-2")
                .originCountry("country")
                .createdAt(Instant.now())
                .rating(4.0)
                .build();
        albumRepository.saveAll(List.of(album1, album2));

        user1 = User.builder()
                .username("testuser")
                .email("user@test.com")
                .password("password")
                .build();
        userRepository.save(user1);

        albumList1 = AlbumList.builder()
                .name("list1")
                .user(user1)
                .albums(Set.of(album1, album2))
                .createdAt(Instant.now())
                .build();

        albumListRepository.save(albumList1);
    }

    @Test
    void testSaveAlbumList() {
        Instant beforeSave = Instant.now();

        AlbumList newAlbumList = AlbumList.builder()
                .name("list")
                .user(user1)
                .albums(Set.of(album1, album2))
                .createdAt(Instant.now())
                .build();
        albumListRepository.save(newAlbumList);

        AlbumList retrievedAlbumList = albumListRepository.findById(newAlbumList.getId()).orElseThrow();

        assertThat(retrievedAlbumList).isNotNull();
        assertThat(retrievedAlbumList.getUser()).isEqualTo(user1);
        assertThat(retrievedAlbumList.getName()).isEqualTo("list");
        assertThat(retrievedAlbumList.getCreatedAt()).isBetween(beforeSave, Instant.now());
        assertThat(retrievedAlbumList.getAlbums()).contains(album1, album2);
    }


    @Test
    void testFindById_Success() {
        AlbumList retrievedAlbumList = albumListRepository.findById(albumList1.getId()).orElseThrow();

        assertThat(retrievedAlbumList).isNotNull();
        assertThat(retrievedAlbumList.getName()).isEqualTo("list1");
        assertThat(retrievedAlbumList.getUser()).isEqualTo(user1);
        assertThat(retrievedAlbumList.getAlbums()).contains(album1, album2);
    }

    @Test
    void testFindById_WhenAlbumListNotFound_ThrowsResourceNotFoundException() {
        UUID nonExistentId = UUID.randomUUID();

        assertThrows(ResourceNotFoundException.class, () -> albumListRepository.findById(nonExistentId)
                .orElseThrow(() -> new ResourceNotFoundException("Album List not found.")));
    }

    @Test
    void testFindByNameAndUserUsername() {
        Optional<AlbumList> optionalAlbumList = albumListRepository.findByNameAndUserUsername(albumList1.getName(), user1.getUsername());

        assertThat(optionalAlbumList).isPresent();

        AlbumList albumList = optionalAlbumList.get();

        assertThat(albumList).isNotNull();
        assertThat(albumList.getName()).isEqualTo("list1");
        assertThat(albumList.getUser()).isEqualTo(user1);
        assertThat(albumList.getAlbums()).contains(album1, album2);
    }

    @Test
    void testFindByNameAndUserUsername_WhenAlbumListNotFound_ThrowsResourceNotFoundException() {
        UUID nonExistentId = UUID.randomUUID();

        assertThrows(ResourceNotFoundException.class, () -> albumListRepository.findById(nonExistentId)
                .orElseThrow(() -> new ResourceNotFoundException("Album List not found.")));
    }

    @Test
    void testGetUserListsCount() {
        Long listsCount = albumListRepository.getUserListsCount(user1);

        assertThat(listsCount).isNotNull();
        assertThat(listsCount).isEqualTo(1);
    }

    @Test
    void testFindByUser() {
        List<AlbumList> albumLists = albumListRepository.findAlbumListsByUser(user1.getId());

        assertThat(albumLists).isNotNull();
        assertThat(albumLists.size()).isGreaterThan(0);
    }

    @Test
    void testDeleteAlbumsByArtist() {
        albumListRepository.deleteAlbumsByArtistId(artist.getId());
        entityManager.flush();
        entityManager.clear();

        Optional<AlbumList> optionalAlbumList = albumListRepository.findById(albumList1.getId());

        assertThat(optionalAlbumList).isPresent();

        AlbumList albumList = optionalAlbumList.get();

        assertThat(albumList.getAlbums()).extracting(Album::getArtist).doesNotContain(artist);
    }

    @Test
    void testDeleteAllByUser() {
        AlbumList albumList2 = AlbumList.builder()
                .name("list2")
                .user(user1)
                .albums(Set.of(album1, album2))
                .createdAt(Instant.now())
                .build();

        albumListRepository.save(albumList2);

        albumListRepository.deleteAllAlbumListsByUser(user1);

        List<AlbumList> retrievedAlbumLists = albumListRepository.findAlbumListsByUser(user1.getId());

        assertThat(retrievedAlbumLists.size()).isEqualTo(0);
    }

    @Test
    void testDeleteAlbumList() {
        long listsCount = albumListRepository.count();

        albumListRepository.delete(albumList1);

        Optional<AlbumList> retrievedAlbumList = albumListRepository.findById(albumList1.getId());

        assertThat(albumListRepository.count()).isEqualTo(listsCount - 1);
        assertThat(retrievedAlbumList).isEmpty();
    }
}
