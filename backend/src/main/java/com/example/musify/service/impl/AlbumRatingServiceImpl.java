package com.example.musify.service.impl;

import com.example.musify.dto.response.*;
import com.example.musify.entity.Album;
import com.example.musify.entity.AlbumRating;
import com.example.musify.entity.User;
import com.example.musify.exception.ResourceNotFoundException;
import com.example.musify.repository.AlbumRatingRepository;
import com.example.musify.repository.AlbumRepository;
import com.example.musify.repository.UserRepository;
import com.example.musify.service.IAlbumRatingService;
import com.example.musify.service.IUtilService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AlbumRatingServiceImpl implements IAlbumRatingService {
    private final AlbumRatingRepository albumRatingRepository;
    private final AlbumRepository albumRepository;
    private final UserRepository userRepository;
    private final IUtilService utilService;
    private final ModelMapper modelMapper;

    @Override
    public AlbumRatingStatsDto getAlbumRating(UUID albumId) {
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new ResourceNotFoundException("Album not found."));

        Long totalRatings = albumRatingRepository.countAlbumRatingsByAlbum(album);
        Double averageRating = albumRatingRepository.averageAlbumRatingByAlbum(album);

        return new AlbumRatingStatsDto(averageRating, totalRatings);
    }

    @Override
    public List<RecentAlbumRatingDto> getMostRecentAlbumRatings() {
        Pageable pageable = PageRequest.of(0, 4);

        return albumRatingRepository.findMostRecentRatings(pageable).stream().map(
                albumRating -> modelMapper.map(albumRating, RecentAlbumRatingDto.class)).toList();
    }

    @Override
    public List<AlbumRatingSummaryDto> getAlbumRatingsByUser(UUID userId) {
        List<AlbumRating> albumRatings = albumRatingRepository.findByUser(userId);

        return albumRatings.stream().map(albumRating ->
                modelMapper.map(albumRating, AlbumRatingSummaryDto.class)).toList();
    }

    @Override
    public UserRatingDto getUserAlbumRating(UUID albumId) {
        User user = utilService.getCurrentUser();

        Double rating = albumRatingRepository.findRatingByUserAndAlbum(user.getId(), albumId);
        rating = rating != null ? rating : 0.0;

        return new UserRatingDto(rating);
    }

    @Override
    public List<AlbumRatingCollectionDto> getAlbumRatingsByUserAndRating(String username, Double rating) {
        List<AlbumRating> albumRatingList = albumRatingRepository.findByUserAndRating(username, rating);

        return albumRatingList.stream().map(albumRating ->
                modelMapper.map(albumRating, AlbumRatingCollectionDto.class)).toList();
    }

    @Override
    public List<GenreAlbumCountDto> getUserGenreOverview(String username) {
        userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        Pageable pageable = PageRequest.of(0, 4);

        return albumRatingRepository.getUserGenreOverview(username, pageable);
    }
}
