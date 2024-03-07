package com.example.musify.service;

import com.example.musify.auth.dto.response.MessageDto;
import com.example.musify.dto.response.FollowerDto;
import com.example.musify.dto.response.FollowingArtistDto;
import com.example.musify.entity.Artist;
import com.example.musify.entity.Follower;
import com.example.musify.entity.User;
import com.example.musify.exception.ResourceNotFoundException;
import com.example.musify.repository.ArtistRepository;
import com.example.musify.repository.FollowerRepository;
import com.example.musify.repository.UserRepository;
import com.example.musify.service.impl.FollowerServiceImpl;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FollowerServiceTests {
    @Mock
    private FollowerRepository followerRepository;
    @Mock
    private ArtistRepository artistRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private IUtilService utilService;
    @Spy
    private ModelMapper modelMapper;

    @InjectMocks
    private FollowerServiceImpl followerService;

    private Artist artist1;
    private User user1;

    @BeforeEach
    void setup() {
        artist1 = Artist.builder()
                .name("Artist")
                .formedYear(Year.of(2010))
                .originCountry("country")
                .createdAt(Instant.now())
                .slug("artist")
                .build();
        artistRepository.save(artist1);

        user1 = User.builder()
                .username("testuser")
                .email("user@test.com")
                .password("password")
                .build();
        userRepository.save(user1);
    }

    @Test
    void testFollowArtist_Success() {
        given(artistRepository.findById(artist1.getId())).willReturn(Optional.of(artist1));
        given(utilService.getCurrentUser()).willReturn(user1);

        FollowerDto result = followerService.followArtist(artist1.getId());

        assertThat(result).isNotNull();
        verify(artistRepository, times(1)).findById(artist1.getId());
    }

    @Test
    void testFollowArtist_WhenArtistNotFound_ThrowsResourceNotFoundException() {
        given(artistRepository.findById(artist1.getId())).willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                followerService.followArtist(artist1.getId()));

        verify(artistRepository, times(1)).findById(artist1.getId());
    }

    @Test
    public void testUnFollowArtist_Success() {
        Follower follower = Follower.builder()
                .user(user1)
                .artist(artist1)
                .build();
        followerRepository.save(follower);

        when(utilService.getCurrentUser()).thenReturn(user1);
        when(followerRepository.findByArtistIdAndUserId(artist1.getId(), user1.getId())).thenReturn(Optional.of(follower));

        MessageDto result = followerService.unFollowArtist(artist1.getId());

        assertThat(result.getMessage()).isEqualTo("\"" + artist1.getName() + "\" was unfollowed.");
        verify(followerRepository,times(1)).findByArtistIdAndUserId(artist1.getId(),user1.getId());
        verify(followerRepository, times(1)).deleteByArtistIdAndUserId(artist1.getId(), user1.getId());
    }

    @Test
    public void testUnFollowArtist_WhenFollowerNotFound_ThrowsResourceNotFoundException() {
        when(utilService.getCurrentUser()).thenReturn(user1);
        when(followerRepository.findByArtistIdAndUserId(artist1.getId(), user1.getId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> followerService.unFollowArtist(artist1.getId()));

        verify(followerRepository,times(1)).findByArtistIdAndUserId(artist1.getId(),user1.getId());
        verify(followerRepository, never()).deleteByArtistIdAndUserId(artist1.getId(), user1.getId());
    }

    @Test
    void testIsFollowing_ReturnsTrue() {
        given(artistRepository.findById(artist1.getId())).willReturn(Optional.of(artist1));
        given(utilService.getCurrentUser()).willReturn(user1);
        given(followerRepository.isUserFollowing(artist1, user1)).willReturn(true);

        Boolean result = followerService.isFollowing(artist1.getId());

        assertThat(result).isNotNull();
        assertThat(result).isTrue();
        verify(artistRepository, times(1)).findById(artist1.getId());
        verify(followerRepository, times(1)).isUserFollowing(artist1,user1);
    }

    @Test
    void testIsFollowing_ReturnsFalse() {
        given(artistRepository.findById(artist1.getId())).willReturn(Optional.of(artist1));
        given(utilService.getCurrentUser()).willReturn(user1);
        given(followerRepository.isUserFollowing(artist1, user1)).willReturn(false);

        Boolean result = followerService.isFollowing(artist1.getId());

        assertThat(result).isNotNull();
        assertThat(result).isFalse();
        verify(artistRepository, times(1)).findById(artist1.getId());
        verify(followerRepository, times(1)).isUserFollowing(artist1,user1);
    }

    @Test
    public void testGetArtistFollowers_Success() {
        List<Follower> followers = new ArrayList<>();
        followers.add(new Follower());
        followers.add(new Follower());

        given(artistRepository.findById(artist1.getId())).willReturn(Optional.of(artist1));
        given(followerRepository.findArtistFollowers(artist1)).willReturn(followers);

        List<FollowerDto> result = followerService.getArtistFollowers(artist1.getId());

        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(2);
        verify(artistRepository,times(1)).findById(artist1.getId());
        verify(followerRepository,times(1)).findArtistFollowers(artist1);
    }

    @Test
    public void testGetArtistFollowers_WhenArtistNotFound_ThrowsResourceNotFoundException() {
        given(artistRepository.findById(artist1.getId())).willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> followerService.getArtistFollowers(artist1.getId()));
        verify(artistRepository,times(1)).findById(artist1.getId());
        verify(followerRepository, never()).findArtistFollowers(any());
    }

    @Test
    public void testGetUserFollowingArtists_Success() {
        List<Artist> artists = new ArrayList<>();
        artists.add(new Artist());
        artists.add(new Artist());

        given(userRepository.findByUsername(user1.getUsername())).willReturn(Optional.of(user1));
        given(followerRepository.findUserFollowingArtists(user1.getUsername())).willReturn(artists);

        List<FollowingArtistDto> result = followerService.getUserFollowingArtists(user1.getUsername());

        assertThat(result.size()).isEqualTo(2);
        verify(userRepository,times(1)).findByUsername(user1.getUsername());
        verify(followerRepository,times(1)).findUserFollowingArtists(user1.getUsername());
    }

    @Test
    public void testGetUserFollowingArtists_WhenUserNotFound_ThrowsResourceNotFoundException() {
        given(userRepository.findByUsername(user1.getUsername())).willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> followerService.getUserFollowingArtists(user1.getUsername()));

        verify(userRepository,times(1)).findByUsername(user1.getUsername());
        verify(followerRepository, never()).findUserFollowingArtists(any());
    }

    @Test
    public void testGetArtistFollowersCount_Success() {
        when(artistRepository.findById(artist1.getId())).thenReturn(Optional.of(artist1));
        when(followerRepository.countArtistFollowers(artist1.getId())).thenReturn(1L);

        Long result = followerService.getArtistFollowersCount(artist1.getId());

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(1L);

        verify(artistRepository,times(1)).findById(artist1.getId());
        verify(followerRepository, times(1)).countArtistFollowers(artist1.getId());
    }

    @Test
    public void testGetArtistFollowersCount_WhenArtistNotFound_ThrowsResourceNotFoundException() {
        when(artistRepository.findById(artist1.getId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> followerService.getArtistFollowersCount(artist1.getId()));

        verify(artistRepository,times(1)).findById(artist1.getId());
        verify(followerRepository, never()).countArtistFollowers(artist1.getId());
    }
}
