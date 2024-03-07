package com.example.musify.service;

import com.example.musify.dto.request.CreateAlbumListDto;
import com.example.musify.dto.request.UpdateAlbumListDto;
import com.example.musify.dto.response.AlbumListDto;
import com.example.musify.dto.response.AlbumListsCountDto;
import com.example.musify.entity.Album;
import com.example.musify.entity.AlbumList;
import com.example.musify.entity.Artist;
import com.example.musify.entity.User;
import com.example.musify.exception.ResourceNotFoundException;
import com.example.musify.repository.AlbumListRepository;
import com.example.musify.repository.AlbumRepository;
import com.example.musify.repository.ArtistRepository;
import com.example.musify.repository.UserRepository;
import com.example.musify.service.impl.AlbumListServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.Instant;
import java.time.Year;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AlbumListServiceTests {
    @Mock
    private AlbumListRepository albumListRepository;
    @Mock
    private AlbumRepository albumRepository;
    @Mock
    private ArtistRepository artistRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private IUtilService utilService;
    @Spy
    private ModelMapper modelMapper;

    @InjectMocks
    private AlbumListServiceImpl albumListService;

    private User user1;
    private AlbumList albumList1;

    @BeforeEach
    void setup() {
        Artist artist = Artist.builder()
                .name("Artist")
                .formedYear(Year.of(2010))
                .originCountry("Country")
                .createdAt(Instant.now())
                .slug("artist")
                .build();
        artistRepository.save(artist);

        Album album1 = Album.builder()
                .title("Test 1")
                .slug("test-1")
                .artist(artist)
                .originCountry("Country")
                .createdAt(Instant.now())
                .rating(4.5)
                .build();

        Album album2 = Album.builder()
                .title("Test 2")
                .artist(artist)
                .slug("test-2")
                .originCountry("Country")
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
                .user(user1)
                .createdAt(Instant.now())
                .name("List")
                .albums(Set.of(album1, album2))
                .build();

        albumListRepository.save(albumList1);
    }

    @Test
    void getUserListsCount_Success() {
        given(userRepository.findByUsername(user1.getUsername())).willReturn(Optional.of(user1));

        AlbumListsCountDto result = albumListService.getUserListsCount(user1.getUsername());

        assertNotNull(result);
        assertThat(result).isInstanceOf(AlbumListsCountDto.class);

        verify(userRepository, times(1)).findByUsername(user1.getUsername());
        verify(albumListRepository, times(1)).getUserListsCount(user1);
    }

    @Test
    void getUserListsCount_WhenUserNotFound_ThrowsResourceNotFoundException() {
        given(userRepository.findByUsername(user1.getUsername())).willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                albumListService.getUserListsCount(user1.getUsername()));

        verify(userRepository, times(1)).findByUsername(user1.getUsername());
        verify(userRepository, times(1)).save(user1);
        verify(albumListRepository, never()).getUserListsCount(user1);
    }

//    @Test
//    void testGetUserListsSummary_Success() {
//        given(userRepository.findByUsername(user1.getUsername())).willReturn(Optional.of(user1));
//        given(albumListRepository.findAlbumListsByUser(user1.getId())).willReturn(List.of(albumList1));
//
//        List<AlbumListNameDto> result = albumListService.getUserListsSummary(user1.getUsername());
//
//        assertThat(result).isNotNull();
//        assertThat(result.size()).isEqualTo(1);
//        assertThat(result.get(0)).isInstanceOf(AlbumListNameDto.class);
//
//        verify(userRepository, times(1)).findByUsername(user1.getUsername());
//        verify(albumListRepository, times(1)).findAlbumListsByUser(user1.getId());
//    }
//
//    @Test
//    void testGetUserListsSummary_WhenUserNotFound_ThrowsResourceNotFoundException() {
//        given(userRepository.findByUsername(user1.getUsername())).willReturn(Optional.empty());
//
//        assertThrows(ResourceNotFoundException.class, () ->
//                albumListService.getUserListsSummary(user1.getUsername()));
//
//        verify(userRepository, times(1)).findByUsername(anyString());
//        verify(albumListRepository, never()).findAlbumListsByUser(user1.getId());
//    }

    @Test
    void testGetList_Success() {
        given(albumListRepository.findByNameAndUserUsername(albumList1.getName(), user1.getUsername()))
                .willReturn(Optional.of(albumList1));

        AlbumListDto result = albumListService.getList(albumList1.getName(), user1.getUsername());

        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(AlbumListDto.class);
        assertThat(result.getName()).isEqualTo(albumList1.getName());

        verify(albumListRepository,times(1)).findByNameAndUserUsername(albumList1.getName(),user1.getUsername());
    }

    @Test
    void testGetList_WhenAlbumListNotFound_ThrowsResourceNotFoundException() {
        given(albumListRepository.findByNameAndUserUsername(albumList1.getName(), user1.getUsername()))
                .willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                albumListService.getList(albumList1.getName(), user1.getUsername()));

        verify(albumListRepository, times(1))
                .findByNameAndUserUsername(albumList1.getName(), user1.getUsername());
    }

    @Test
    void testCreateAlbumList() {
        CreateAlbumListDto requestDto = CreateAlbumListDto.builder()
                .name("Test List")
                .build();

        AlbumListDto result = albumListService.createAlbumList(requestDto);

        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(AlbumListDto.class);
        assertThat(result.getName()).isEqualTo(requestDto.getName());
    }

    @Test
    void testUpdateAlbumList_Success() {
        given(albumListRepository.findById(albumList1.getId())).willReturn(Optional.of(albumList1));

        UpdateAlbumListDto requestDto = UpdateAlbumListDto.builder()
                .name("Updated Name")
                .build();

        AlbumListDto result = albumListService.updateAlbumList(requestDto, albumList1.getId());

        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(AlbumListDto.class);
        assertThat(result.getName()).isEqualTo(requestDto.getName());

        verify(albumListRepository,times(1)).findById(albumList1.getId());
    }

    @Test
    void testUpdateAlbumList_WhenAlbumListNotFound_ThrowsResourceNotFoundException() {
        given(albumListRepository.findById(albumList1.getId())).willReturn(Optional.empty());

        UpdateAlbumListDto requestDto = UpdateAlbumListDto.builder()
                .name("Updated Name")
                .build();

        assertThrows(ResourceNotFoundException.class, () ->
                albumListService.updateAlbumList(requestDto, albumList1.getId()));

        verify(albumListRepository, times(1)).findById(albumList1.getId());
    }

//    @Test
//    void testDeleteAlbumList_Success() {
//        given(albumListRepository.findById(albumList1.getId())).willReturn(Optional.of(albumList1));
//
//        MessageDto result = albumListService.deleteAlbumList(albumList1.getId());
//
//        assertThat(result).isNotNull();
//        assertThat(result).isInstanceOf(MessageDto.class);
//        assertThat(result.getMessage()).isEqualTo("Album List deleted.");
//
//        verify(albumListRepository, times(1)).findById(albumList1.getId());
//    }
//
//
//    @Test
//    void testDeleteAlbumList_WhenAlbumListNotFound_ThrowsResourceNotFoundException() {
//        given(albumListRepository.findById(albumList1.getId())).willReturn(Optional.empty());
//
//        assertThrows(ResourceNotFoundException.class, () ->
//                albumListService.deleteAlbumList(albumList1.getId()));
//
//        verify(albumListRepository, times(1)).findById(albumList1.getId());
//        verify(albumListRepository, never()).deleteAlbumList(albumList1.getId());
//    }
}
