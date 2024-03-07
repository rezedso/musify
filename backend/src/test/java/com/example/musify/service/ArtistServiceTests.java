package com.example.musify.service;

import com.example.musify.auth.dto.response.MessageDto;
import com.example.musify.dto.request.CreateArtistDto;
import com.example.musify.dto.request.UpdateArtistDto;
import com.example.musify.dto.response.ArtistDto;
import com.example.musify.dto.response.PageDto;
import com.example.musify.dto.response.RecentArtistDto;
import com.example.musify.entity.Artist;
import com.example.musify.entity.Genre;
import com.example.musify.exception.ResourceNotFoundException;
import com.example.musify.repository.AlbumListRepository;
import com.example.musify.repository.AlbumRepository;
import com.example.musify.repository.ArtistRepository;
import com.example.musify.repository.GenreRepository;
import com.example.musify.service.impl.ArtistServiceImpl;
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
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ArtistServiceTests {
    @Mock
    private ArtistRepository artistRepository;
    @Mock
    private GenreRepository genreRepository;
    @Mock
    private AlbumListRepository albumListRepository;
    @Mock
    private AlbumRepository albumRepository;
    @Mock
    private IFileUploadService fileUploadService;
    @Spy
    private ModelMapper modelMapper;

    @InjectMocks
    private ArtistServiceImpl artistService;

    private Artist artist1;
    private Genre genre1;

    @BeforeEach
    void setup() {
        artist1 = Artist.builder()
                .name("Artist")
                .formedYear(Year.of(2010))
                .originCountry("country")
                .createdAt(Instant.now())
                .artistGenres(new HashSet<>())
                .slug("artist")
                .build();
        artistRepository.save(artist1);

        genre1 = Genre.builder()
                .name("Test Genre")
                .slug("test-genre")
                .build();
        genreRepository.save(genre1);
    }

    @Test
    void testGetArtists() {
        given(artistRepository.findAll(any(Pageable.class))).willReturn(new PageImpl<>(List.of(artist1)));

        PageDto<ArtistDto> result = artistService.getArtists(1);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0)).isInstanceOf(ArtistDto.class);
        assertThat(result.getTotalPages()).isEqualTo(1);
        assertThat(result.getCurrentPage()).isEqualTo(1);
        assertThat(result.getTotalElements()).isEqualTo(1L);

        verify(artistRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void testGetArtist_Success() {
        given(artistRepository.findBySlug(artist1.getSlug()))
                .willReturn(Optional.of(artist1));

        ArtistDto result = artistService.getArtist(artist1.getSlug());

        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(ArtistDto.class);
        assertThat(result.getName()).isEqualTo("Artist");
        assertThat(result.getSlug()).isEqualTo("artist");
        assertThat(result.getOriginCountry()).isEqualTo("country");
        assertThat(result.getFormedYear()).isEqualTo(Year.of(2010));

        verify(artistRepository, times(1)).findBySlug(artist1.getSlug());
    }

    @Test
    void testGetArtist_WhenArtistNotFound_ThrowsResourceNotFoundException() {
        given(artistRepository.findBySlug(artist1.getSlug()))
                .willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                artistService.getArtist(artist1.getSlug()));

        verify(artistRepository, times(1)).findBySlug(artist1.getSlug());
    }

    @Test
    void testGetMostRecentArtists() {
        given(artistRepository.findMostRecentArtists(any(Pageable.class))).willReturn(List.of(artist1));

        List<RecentArtistDto> result = artistService.getMostRecentArtists();

        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0)).isInstanceOf(RecentArtistDto.class);

        verify(artistRepository, times(1)).findMostRecentArtists(any(Pageable.class));
    }

    @Test
    void testGetArtistsByGenre() {
        given(artistRepository.findByGenre(eq(genre1.getSlug()), any(Pageable.class)))
                .willReturn(new PageImpl<>(List.of(artist1)));

        PageDto<ArtistDto> result = artistService.getArtistsByGenre(genre1.getSlug(), 1);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0)).isInstanceOf(ArtistDto.class);
        assertThat(result.getTotalPages()).isEqualTo(1);
        assertThat(result.getCurrentPage()).isEqualTo(1);
        assertThat(result.getTotalElements()).isEqualTo(1L);

        verify(artistRepository, times(1)).findByGenre(eq(genre1.getSlug()), any(Pageable.class));
        verify(artistRepository, times(1)).findByGenre(eq(genre1.getSlug()), any(Pageable.class));
    }

    @Test
    void testCreateArtist_WithFile() throws IOException {
        CreateArtistDto createArtistDto = CreateArtistDto.builder()
                .name("New Name")
                .slug("new-name")
                .originCountry("New Country")
                .formedYear(Year.of(2016))
                .build();

        MultipartFile file = mock(MultipartFile.class);

        given(fileUploadService.uploadArtistImageFile(file)).willReturn("testImageUrl");
        given(artistRepository.save(any(Artist.class))).willReturn(artist1);

        ArtistDto result = artistService.createArtist(createArtistDto, file);

        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(ArtistDto.class);

        verify(fileUploadService, times(1)).uploadArtistImageFile(file);
    }

    @Test
    void testCreateArtist_WithoutFile() throws IOException {
        CreateArtistDto createAlbumDto = CreateArtistDto.builder()
                .name("New Name")
                .slug("new-name")
                .originCountry("New Country")
                .formedYear(Year.of(2016))
                .build();

        given(artistRepository.save(any(Artist.class))).willReturn(artist1);

        ArtistDto result = artistService.createArtist(createAlbumDto, null);

        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(ArtistDto.class);
    }

    @Test
    void testUpdateArtistWithFile_Success() throws IOException {
        UpdateArtistDto updateArtistDto = UpdateArtistDto.builder()
                .name("New Name")
                .originCountry("New Country")
                .formedYear(Year.of(2007))
                .build();

        MultipartFile file = mock(MultipartFile.class);

        given(artistRepository.findById(artist1.getId())).willReturn(Optional.of(artist1));
        given(fileUploadService.uploadArtistImageFile(file)).willReturn("testImageUrl");

        ArtistDto result = artistService.updateArtist(updateArtistDto, file, artist1.getId());

        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(ArtistDto.class);
        assertThat(result.getName()).isEqualTo(updateArtistDto.getName());
        assertThat(result.getOriginCountry()).isEqualTo(updateArtistDto.getOriginCountry());
        assertThat(result.getFormedYear()).isEqualTo(updateArtistDto.getFormedYear());

        verify(artistRepository, times(1)).findById(artist1.getId());
    }

    @Test
    void testUpdateArtistWithoutFile_Success() throws IOException {
        UpdateArtistDto updateArtistDto = UpdateArtistDto.builder()
                .name("New Name")
                .originCountry("New Country")
                .formedYear(Year.of(2007))
                .build();

        given(artistRepository.findById(artist1.getId())).willReturn(Optional.of(artist1));

        ArtistDto result = artistService.updateArtist(updateArtistDto, null, artist1.getId());

        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(ArtistDto.class);
        assertThat(result.getName()).isEqualTo(updateArtistDto.getName());
        assertThat(result.getOriginCountry()).isEqualTo(updateArtistDto.getOriginCountry());
        assertThat(result.getFormedYear()).isEqualTo(updateArtistDto.getFormedYear());

        verify(artistRepository, times(1)).findById(artist1.getId());
    }

    @Test
    void testUpdateAlbum_WhenAlbumNotFound_ThrowsResourceNotFoundException() throws IOException {
        UpdateArtistDto updateArtistDto = UpdateArtistDto.builder()
                .name("New Name")
                .originCountry("New Country")
                .formedYear(Year.of(2007))
                .build();

        MultipartFile file = mock(MultipartFile.class);

        given(artistRepository.findById(artist1.getId())).willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                artistService.updateArtist(updateArtistDto, file, artist1.getId()));

        verify(artistRepository, times(1)).findById(artist1.getId());
    }

    @Test
    public void testDeleteArtist_Success() {
        given(artistRepository.findById(artist1.getId())).willReturn(Optional.of(artist1));

        MessageDto result = artistService.deleteArtist(artist1.getId());

        assertThat(result).isInstanceOf(MessageDto.class);
        assertThat(result.getMessage()).isEqualTo("Artist deleted.");

        verify(albumListRepository, times(1)).deleteAlbumsByArtistId(artist1.getId());
        verify(artistRepository, times(1)).delete(artist1);
    }

    @Test
    public void testDeleteArtist_ThrowsNotFound() {
        given(artistRepository.findById(ArtistServiceTests.this.artist1.getId())).willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                artistService.deleteArtist(artist1.getId()));

        verify(albumListRepository, never()).deleteAlbumsByArtistId(ArtistServiceTests.this.artist1.getId());
        verify(artistRepository, never()).delete(artist1);
    }
}
