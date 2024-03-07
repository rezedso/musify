package com.example.musify.service;

import com.example.musify.auth.dto.response.MessageDto;
import com.example.musify.dto.request.CreateAlbumDto;
import com.example.musify.dto.request.RateAlbumDto;
import com.example.musify.dto.request.UpdateAlbumDto;
import com.example.musify.dto.request.UserIdDto;
import com.example.musify.dto.response.AlbumDto;
import com.example.musify.dto.response.PageDto;
import com.example.musify.dto.response.RecentAlbumDto;
import com.example.musify.entity.*;
import com.example.musify.exception.ResourceNotFoundException;
import com.example.musify.repository.*;
import com.example.musify.service.impl.AlbumServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.time.Year;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AlbumServiceTests {
    @Mock
    private AlbumRepository albumRepository;
    @Mock
    private ArtistRepository artistRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private GenreRepository genreRepository;
    @Mock
    private AlbumRatingRepository albumRatingRepository;
    @Mock
    private AlbumListRepository albumListRepository;
    @Mock
    private IUtilService utilService;
    @Mock
    private IFileUploadService fileUploadService;
    @Spy
    private ModelMapper modelMapper;

    @InjectMocks
    private AlbumServiceImpl albumService;

    private User user1;
    private Album album1;
    private Album album2;
    private Artist artist1;
    private Genre genre1;
    private AlbumList albumList1;
    private UserIdDto userIdDto;

    @BeforeEach
    void setup() {
        artist1 = Artist.builder()
                .name("artist")
                .formedYear(Year.of(2010))
                .originCountry("country")
                .createdAt(Instant.now())
                .slug("artist")
                .build();
        artistRepository.save(artist1);

        genre1 = Genre.builder()
                .name("Test Genre")
                .slug("test-genre")
                .build();
        genreRepository.save(genre1);

        album1 = Album.builder()
                .title("Test 1")
                .slug("test-1")
                .artist(artist1)
                .originCountry("country")
                .createdAt(Instant.now())
                .rating(4.5)
                .build();

        album2 = Album.builder()
                .title("Test 2")
                .artist(artist1)
                .slug("test-2")
                .originCountry("country")
                .lists(new HashSet<>())
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
                .albums(new HashSet<>())
                .createdAt(Instant.now())
                .build();
        albumList1.setAlbums(Set.of(album1));
        albumListRepository.save(albumList1);

        userIdDto = UserIdDto.builder().id(UUID.randomUUID()).build();
    }

    @Test
    void testGetAlbums() {
        given(albumRepository.findAll(any(Pageable.class))).willReturn(new PageImpl<>(List.of(album1)));

        PageDto<AlbumDto> result = albumService.getAlbums(1);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalPages()).isEqualTo(1);
        assertThat(result.getCurrentPage()).isEqualTo(1);
        assertThat(result.getTotalElements()).isEqualTo(1L);
        assertThat(result.getContent().get(0)).isInstanceOf(AlbumDto.class);

        verify(albumRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void testGetAlbum_Success() {
        given(albumRepository.findByArtistSlugAndAlbumSlug(artist1.getSlug(), album1.getSlug()))
                .willReturn(Optional.of(album1));

        AlbumDto result = albumService.getAlbum(artist1.getSlug(), album1.getSlug());

        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(AlbumDto.class);
        assertThat(result.getTitle()).isEqualTo("Test 1");
        assertThat(result.getSlug()).isEqualTo("test-1");
        assertThat(result.getOriginCountry()).isEqualTo("country");
        assertThat(result.getRating()).isEqualTo(4.5);

        verify(albumRepository, times(1)).findByArtistSlugAndAlbumSlug(artist1.getSlug(), album1.getSlug());
    }

    @Test
    void testGetAlbum_WhenAlbumNotFound_ThrowsResourceNotFoundException() {
        given(albumRepository.findByArtistSlugAndAlbumSlug(artist1.getSlug(), album1.getSlug()))
                .willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                albumService.getAlbum(artist1.getSlug(), album1.getSlug()));

        verify(albumRepository, times(1)).findByArtistSlugAndAlbumSlug(artist1.getSlug(), album1.getSlug());
    }

    @Test
    void testGetMostRecentAlbums() {
        given(albumRepository.findMostRecentAlbums(any(Pageable.class))).willReturn(List.of(album1));

        List<RecentAlbumDto> result = albumService.getMostRecentAlbums();

        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0)).isInstanceOf(RecentAlbumDto.class);

        verify(albumRepository, times(1)).findMostRecentAlbums(any(Pageable.class));
    }

    @Test
    void testGetAlbumsByArtist_Success() {
        given(artistRepository.findBySlug(artist1.getSlug())).willReturn(Optional.of(artist1));
        given(albumRepository.findByArtist(artist1)).willReturn(List.of(album1));

        List<AlbumDto> result = albumService.getAlbumsByArtist(artist1.getSlug());

        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0)).isInstanceOf(AlbumDto.class);

        verify(artistRepository, times(1)).findBySlug(artist1.getSlug());
        verify(albumRepository, times(1)).findByArtist(artist1);
    }

    @Test
    void testGetAlbumsByArtist_WhenArtistNotFound_ThrowsResourceNotFoundException() {
        given(artistRepository.findBySlug(artist1.getSlug())).willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                albumService.getAlbumsByArtist(artist1.getSlug()));

        verify(artistRepository, times(1)).findBySlug(artist1.getSlug());
        verify(albumRepository, never()).findByArtist(artist1);
    }

    @Test
    void testGetAlbumsByGenre_Success() {
        given(genreRepository.findBySlug(genre1.getSlug())).willReturn(Optional.of(genre1));
        given(albumRepository.findByGenreSlug(eq(genre1.getSlug()), any(Pageable.class)))
                .willReturn(new PageImpl<>(List.of(album1)));

        PageDto<AlbumDto> result = albumService.getAlbumsByGenre(genre1.getSlug(), 1);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalPages()).isEqualTo(1);
        assertThat(result.getCurrentPage()).isEqualTo(1);
        assertThat(result.getTotalElements()).isEqualTo(1L);
        assertThat(result.getContent().get(0)).isInstanceOf(AlbumDto.class);

        verify(genreRepository, times(1)).findBySlug(genre1.getSlug());
        verify(albumRepository, times(1)).findByGenreSlug(eq(genre1.getSlug()), any(Pageable.class));
    }

    @Test
    void testGetAlbumsByGenre_WhenGenreNotFound_ThrowsResourceNotFoundException() {
        given(genreRepository.findBySlug(genre1.getSlug())).willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                albumService.getAlbumsByGenre(genre1.getSlug(), 1));

        verify(genreRepository, times(1)).findBySlug(genre1.getSlug());
        verify(albumRepository, never()).findByGenreSlug(eq(genre1.getSlug()), any(Pageable.class));
    }

    @Test
    void testCreateAlbum_WithFile_Success() throws IOException {
        CreateAlbumDto createAlbumDto = CreateAlbumDto.builder()
                .albumGenres(Set.of("Jazz"))
                .releaseDate(Instant.now())
                .artistName(artist1.getName())
                .title("Test Album")
                .slug("test-album")
                .build();

        MultipartFile file = mock(MultipartFile.class);

        given(artistRepository.findByName(artist1.getName())).willReturn(Optional.of(artist1));
        given(genreRepository.findByNameIn(any())).willReturn(List.of());
        given(fileUploadService.uploadAlbumImageFile(file)).willReturn("testImageUrl");
        given(albumRepository.save(any(Album.class))).willReturn(album1);

        AlbumDto result = albumService.createAlbum(createAlbumDto, file);

        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(AlbumDto.class);

        verify(artistRepository, times(1)).findByName(artist1.getName());
        verify(fileUploadService, times(1)).uploadAlbumImageFile(file);
    }

    @Test
    void testCreateAlbum_WithoutFile_Success() throws IOException {
        CreateAlbumDto createAlbumDto = CreateAlbumDto.builder()
                .albumGenres(Set.of("Jazz"))
                .releaseDate(Instant.now())
                .artistName(artist1.getName())
                .title("Test Album")
                .slug("test-album")
                .build();

        given(artistRepository.findByName(artist1.getName())).willReturn(Optional.of(artist1));
        given(genreRepository.findByNameIn(any())).willReturn(List.of());
        given(albumRepository.save(any(Album.class))).willReturn(album1);

        AlbumDto result = albumService.createAlbum(createAlbumDto, null);

        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(AlbumDto.class);

        verify(artistRepository, times(1)).findByName(artist1.getName());
        verify(fileUploadService, never()).uploadAlbumImageFile(any(MultipartFile.class));
    }

    @Test
    void testCreateAlbum_WhenArtistNotFound_ThrowsResourceNotFoundException() throws IOException {
        CreateAlbumDto createAlbumDto = CreateAlbumDto.builder()
                .albumGenres(Set.of("Jazz"))
                .releaseDate(Instant.now())
                .artistName(artist1.getName())
                .title("Test Album")
                .slug("test-album")
                .build();

        given(artistRepository.findByName(artist1.getName())).willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                albumService.createAlbum(createAlbumDto, null));

        verify(artistRepository, times(1)).findByName(artist1.getName());
        verify(albumRepository, never()).save(album1);
    }

    @Test
    void testRateAlbum_Success() {
        RateAlbumDto rateAlbumDto = RateAlbumDto.builder()
                .rating(4.0)
                .build();

        given(albumRepository.findById(album1.getId())).willReturn(Optional.of(album1));
        given(utilService.getCurrentUser()).willReturn(user1);
        given(albumRatingRepository.findByAlbumAndUser(album1, user1)).willReturn(null);
        given(albumRatingRepository.averageAlbumRatingByAlbum(album1)).willReturn(rateAlbumDto.getRating());
        given(albumRepository.save(album1)).willReturn(album1);

        AlbumDto result = albumService.rateAlbum(rateAlbumDto, album1.getId());

        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(AlbumDto.class);
        assertThat(result.getRating()).isEqualTo(4.0);

        verify(albumRepository, times(1)).findById(album1.getId());
        verify(albumRatingRepository, times(1)).findByAlbumAndUser(album1, user1);
        verify(albumRatingRepository, times(1)).averageAlbumRatingByAlbum(album1);
        verify(albumRepository, times(1)).save(album1);
    }

    @Test
    void testRateAlbum_WhenAlbumNotFound_ThrowsResourceNotFoundException() throws IOException {
        RateAlbumDto rateAlbumDto = RateAlbumDto.builder()
                .rating(4.0)
                .build();

        given(albumRepository.findById(album1.getId())).willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                albumService.rateAlbum(rateAlbumDto, album1.getId()));

        verify(albumRepository, times(1)).findById(album1.getId());
        verify(albumRepository, never()).save(album1);
    }

    @Test
    void testAddAlbumToList_Success() {
        AlbumList albumList2 = AlbumList.builder()
                .name("list2")
                .user(user1)
                .albums(new HashSet<>())
                .createdAt(Instant.now())
                .build();

        albumListRepository.save(albumList2);
        given(albumRepository.findById(album2.getId())).willReturn(Optional.of(album2));
        given(albumListRepository.findById(albumList2.getId())).willReturn(Optional.of(albumList2));

        MessageDto result = albumService.addAlbumToList(albumList2.getId(), album2.getId(),userIdDto);

        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(MessageDto.class);
        assertThat(result.getMessage()).isEqualTo("Album added to list.");
        assertThat(albumList2.getAlbums()).contains(album2);
        assertThat(album2.getLists()).contains(albumList2);

        verify(albumRepository, times(1)).findById(album2.getId());
        verify(albumListRepository, times(1)).findById(albumList2.getId());
    }

    @Test
    void testAddAlbumToList_WhenAlbumNotFound_ThrowsResourceNotFoundException() {
        AlbumList albumList2 = AlbumList.builder()
                .name("list2")
                .user(user1)
                .albums(new HashSet<>())
                .createdAt(Instant.now())
                .build();

        albumListRepository.save(albumList2);

        given(albumRepository.findById(album2.getId())).willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, ()->
                albumService.addAlbumToList(albumList2.getId(), album2.getId(),userIdDto));

        verify(albumRepository, times(1)).findById(album2.getId());
        verify(albumListRepository, never()).findById(albumList2.getId());
    }

    @Test
    void testAddAlbumToList_WhenAlbumListNotFound_ThrowsResourceNotFoundException() {
        AlbumList albumList2 = AlbumList.builder()
                .name("list2")
                .user(user1)
                .albums(new HashSet<>())
                .createdAt(Instant.now())
                .build();
        albumListRepository.save(albumList2);

        given(albumRepository.findById(album2.getId())).willReturn(Optional.of(album2));
        given(albumListRepository.findById(albumList2.getId())).willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, ()->
                albumService.addAlbumToList(albumList2.getId(), album2.getId(),userIdDto));

        verify(albumRepository, times(1)).findById(album2.getId());
        verify(albumListRepository, times(1)).findById(albumList2.getId());
    }

    @Test
    void testRemoveAlbumFromList_Success() {
        AlbumList albumList2 = AlbumList.builder()
                .name("list2")
                .user(user1)
                .albums(new HashSet<>())
                .createdAt(Instant.now())
                .build();
        albumListRepository.save(albumList2);

        Album album3 = Album.builder()
                .title("Test 3")
                .artist(artist1)
                .slug("test-3")
                .originCountry("country")
                .createdAt(Instant.now())
                .lists(new HashSet<>())
                .rating(4.0)
                .build();
        albumRepository.save(album3);

        albumList2.getAlbums().add(album3);

        given(albumRepository.findById(album3.getId())).willReturn(Optional.of(album3));
        given(albumListRepository.findById(albumList2.getId())).willReturn(Optional.of(albumList2));

        MessageDto result = albumService.removeAlbumFromList(albumList2.getId(), album3.getId(),userIdDto);

        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(MessageDto.class);
        assertThat(result.getMessage()).isEqualTo("Album removed from list.");
        assertThat(albumList2.getAlbums()).doesNotContain(album3);
        assertThat(album3.getLists()).doesNotContain(albumList2);

        verify(albumRepository, times(1)).findById(album3.getId());
        verify(albumListRepository, times(1)).findById(albumList2.getId());
    }

    @Test
    void testRemoveAlbumFromList_WhenAlbumNotFound_ThrowsResourceNotFoundException() {
        given(albumRepository.findById(album1.getId())).willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, ()->
                albumService.removeAlbumFromList(albumList1.getId(), album1.getId(),userIdDto));

        verify(albumRepository, times(1)).findById(album1.getId());
        verify(albumListRepository, never()).findById(albumList1.getId());
    }

    @Test
    void testRemoveAlbumFromList_WhenAlbumListNotFound_ThrowsResourceNotFoundException() {
        given(albumRepository.findById(album1.getId())).willReturn(Optional.of(album1));
        given(albumListRepository.findById(albumList1.getId())).willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, ()->
                albumService.removeAlbumFromList(albumList1.getId(), album1.getId(),userIdDto));

        verify(albumRepository, times(1)).findById(album1.getId());
        verify(albumListRepository, times(1)).findById(albumList1.getId());
    }

    @Test
    void testUpdateAlbumWithFile_Success() throws IOException {
        UpdateAlbumDto updateAlbumDto = UpdateAlbumDto.builder()
                .title("New Title")
                .slug("new-title")
                .albumGenres(Set.of("New Genre"))
                .releaseDate(Instant.now())
                .build();

        MultipartFile file = mock(MultipartFile.class);

        given(albumRepository.findById(album1.getId())).willReturn(Optional.of(album1));
        given(genreRepository.findByName(anyString())).willReturn(Optional.empty());
        given(fileUploadService.uploadAlbumImageFile(file)).willReturn("testImageUrl");

        AlbumDto result = albumService.updateAlbum(updateAlbumDto, file, album1.getId());

        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(AlbumDto.class);
        assertThat(result.getTitle()).isEqualTo(updateAlbumDto.getTitle());
        assertThat(result.getSlug()).isEqualTo(updateAlbumDto.getSlug());

        verify(albumRepository,times(1)).findById(album1.getId());
        verify(genreRepository,times(1)).findByName(anyString());
    }

    @Test
    void testUpdateAlbumWithoutFile_Success() throws IOException {
        UpdateAlbumDto updateAlbumDto = UpdateAlbumDto.builder()
                .title("New Title")
                .slug("new-title")
                .albumGenres(Set.of("New Genre"))
                .releaseDate(Instant.now())
                .build();

        given(albumRepository.findById(album1.getId())).willReturn(Optional.of(album1));
        given(genreRepository.findByName(anyString())).willReturn(Optional.empty());

        AlbumDto result = albumService.updateAlbum(updateAlbumDto, null, album1.getId());

        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(AlbumDto.class);
        assertThat(result.getTitle()).isEqualTo(updateAlbumDto.getTitle());
        assertThat(result.getSlug()).isEqualTo(updateAlbumDto.getSlug());

        verify(albumRepository,times(1)).findById(album1.getId());
        verify(genreRepository,times(1)).findByName(anyString());
    }

    @Test
    void testUpdateAlbum_WhenAlbumNotFound_ThrowsResourceNotFoundException() throws IOException {
        UpdateAlbumDto updateAlbumDto = UpdateAlbumDto.builder()
                .title("New Title")
                .slug("new-title")
                .albumGenres(Set.of("New Genre"))
                .releaseDate(Instant.now())
                .build();

        MultipartFile file = mock(MultipartFile.class);

        given(albumRepository.findById(album1.getId())).willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                albumService.updateAlbum(updateAlbumDto, file, album1.getId()));

        verify(albumRepository,times(1)).findById(album1.getId());
        verify(genreRepository,never()).findByName(anyString());
    }

    @Test
    public void testDeleteAlbum_Success() {
        given(albumRepository.findById(album1.getId())).willReturn(Optional.of(album1));

        MessageDto result = albumService.deleteAlbum(album1.getId());

        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(MessageDto.class);
        assertThat( result.getMessage()).isEqualTo("Album deleted.");
        verify(albumRepository, times(1)).deleteById(album1.getId());
    }

    @Test
    public void testDeleteAlbum_WhenAlbumNotFound_ThrowsResourceNotFoundException() {
        given(albumRepository.findById(album1.getId())).willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> albumService.deleteAlbum(album1.getId()));
        verify(albumRepository, never()).deleteById(album1.getId());
    }
}
