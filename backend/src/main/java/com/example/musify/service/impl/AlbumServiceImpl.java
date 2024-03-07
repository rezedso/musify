package com.example.musify.service.impl;

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
import com.example.musify.service.IAlbumService;
import com.example.musify.service.IFileUploadService;
import com.example.musify.service.IUtilService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlbumServiceImpl implements IAlbumService {
    private final AlbumRepository albumRepository;
    private final AlbumListRepository albumListRepository;
    private final ArtistRepository artistRepository;
    private final AlbumRatingRepository albumRatingRepository;
    private final GenreRepository genreRepository;
    private final IFileUploadService fileUploadService;
    private final IUtilService utilService;
    private final ModelMapper modelMapper;

    @Override
    public PageDto<AlbumDto> getAlbums(int page) {
        Page<Album> pageRequest = albumRepository.findAll(PageRequest.of(page - 1, 20));
        int totalPages = pageRequest.getTotalPages();
        int currentPage = pageRequest.getNumber() + 1;
        Long totalElements = pageRequest.getTotalElements();

        List<AlbumDto> content = pageRequest.getContent().stream().
                map(tutorial -> modelMapper.map(tutorial, AlbumDto.class)).toList();
        return new PageDto<>(content, totalPages, currentPage, totalElements);
    }

    @Override
    public AlbumDto getAlbum(String artistSlug, String albumSlug) {
        Album album = albumRepository.findByArtistSlugAndAlbumSlug(artistSlug, albumSlug)
                .orElseThrow(() -> new ResourceNotFoundException("Album not found."));

        return modelMapper.map(album, AlbumDto.class);
    }

    @Override
    public List<RecentAlbumDto> getMostRecentAlbums() {
        Pageable pageable = PageRequest.of(0, 4);

        List<Album> albums = albumRepository.findMostRecentAlbums(pageable);

        return albums.stream().map(album -> modelMapper.map(album, RecentAlbumDto.class)).toList();
    }

    @Override
    public List<AlbumDto> getAlbumsByArtist(String artistSlug) {
        Artist artist = artistRepository.findBySlug(artistSlug)
                .orElseThrow(() -> new ResourceNotFoundException("Artist not found."));

        List<Album> albums = albumRepository.findByArtist(artist);

        return albums.stream().map(comment -> modelMapper.map(comment, AlbumDto.class)).toList();
    }

    @Override
    public PageDto<AlbumDto> getAlbumsByGenre(String slug, int page) {
        genreRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Genre not found."));

        Page<Album> pageRequest = albumRepository.findByGenreSlug(slug,
                PageRequest.of(page - 1, 6));

        int totalPages = pageRequest.getTotalPages();
        int currentPage = pageRequest.getNumber() + 1;
        Long totalElements = pageRequest.getTotalElements();

        List<AlbumDto> content = pageRequest.getContent().stream().
                map(tutorial -> modelMapper.map(tutorial, AlbumDto.class)).toList();

        return new PageDto<>(content, totalPages, currentPage, totalElements);
    }

    @Override
    public AlbumDto createAlbum(CreateAlbumDto request, MultipartFile file) throws IOException {
        Artist artist = artistRepository.findByName(request.getArtistName())
                .orElseThrow(() -> new ResourceNotFoundException("Artist not found."));

        Album album = Album.builder()
                .artist(artist)
                .title(request.getTitle())
                .releaseDate(request.getReleaseDate())
                .slug(request.getSlug())
                .originCountry(artist.getOriginCountry())
                .build();

        Set<Genre> genres = new HashSet<>();

        if (request.getAlbumGenres() != null) {
            Set<String> genreNames = request.getAlbumGenres();
            List<Genre> existingGenres = genreRepository.findByNameIn(genreNames);

            Map<String, Genre> existingGenreMap = existingGenres.stream()
                    .collect(Collectors.toMap(Genre::getName, Function.identity()));

            for (String genreName : genreNames) {
                Genre existingGenre = existingGenreMap.get(genreName);
                if (existingGenre != null) {
                    genres.add(existingGenre);
                } else {
                    if (!genreName.isEmpty()) {
                        Genre newGenre = Genre.builder().name(genreName).build();
                        String slug = newGenre.getName().toLowerCase().replaceAll("\\s+", "-");

                        newGenre.setSlug(slug);
                        genres.add(newGenre);
                    }
                }
            }
        }

        if (file != null) {
            String albumImage = fileUploadService.uploadAlbumImageFile(file);
            album.setImage(albumImage);
        }

        album.setAlbumGenres(genres);

        Album savedAlbum = albumRepository.save(album);

        return modelMapper.map(savedAlbum, AlbumDto.class);
    }

    @Override
    @Transactional
    public AlbumDto rateAlbum(RateAlbumDto request, UUID albumId) {
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new ResourceNotFoundException("Album not found."));

        User user = utilService.getCurrentUser();

        AlbumRating albumRating = albumRatingRepository.findByAlbumAndUser(album, user);

        if (albumRating != null) {
            albumRating.setRating(request.getRating());
        } else {
            AlbumRating newRating = AlbumRating.builder()
                    .album(album)
                    .user(user)
                    .rating(request.getRating())
                    .build();

            albumRatingRepository.save(newRating);
        }

        Double averageRating = albumRatingRepository.averageAlbumRatingByAlbum(album);
        album.setRating(averageRating);

        albumRepository.save(album);

        return modelMapper.map(album, AlbumDto.class);
    }

    @Override
    public MessageDto addAlbumToList(UUID listId, UUID albumId, UserIdDto request) {
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new ResourceNotFoundException("Album not found."));

        AlbumList albumList = albumListRepository.findById(listId)
                .orElseThrow(() -> new ResourceNotFoundException("List not found."));

        albumList.addAlbum(album);

        albumListRepository.save(albumList);
        return new MessageDto("Album added to list.");
    }

    @Override
    public MessageDto removeAlbumFromList(UUID listId, UUID albumId, UserIdDto request) {
        albumRepository.findById(albumId)
                .orElseThrow(() -> new ResourceNotFoundException("Album not found."));

        AlbumList albumList = albumListRepository.findById(listId)
                .orElseThrow(() -> new ResourceNotFoundException("List not found."));

        albumList.removeAlbum(albumId);

        albumListRepository.save(albumList);
        return new MessageDto("Album removed from list.");
    }

    @Override
    @Transactional
    public AlbumDto updateAlbum(UpdateAlbumDto request, MultipartFile file, UUID albumId) throws IOException {
        Album albumToUpdate = albumRepository.findById(albumId)
                .orElseThrow(() -> new ResourceNotFoundException("Album not found."));

        Set<Genre> genres = new HashSet<>();
        List<Genre> newGenres = new ArrayList<>();

        if (request.getAlbumGenres() != null && !request.getAlbumGenres().isEmpty()) {
            for (String genreName : request.getAlbumGenres()) {
                Optional<Genre> existingGenre = genreRepository.findByName(genreName);
                if (existingGenre.isPresent()) {
                    genres.add(existingGenre.get());
                } else {
                    if (!genreName.isEmpty()) {
                        Genre newGenre = Genre.builder()
                                .name(genreName)
                                .build();

                        String slug = newGenre.getName().toLowerCase().replaceAll("\\s+", "-");
                        newGenre.setSlug(slug);
                        newGenres.add(newGenre);
                    }
                }
            }

            if (!newGenres.isEmpty()) {
                newGenres = genreRepository.saveAll(newGenres);
                genres.addAll(newGenres);
            }
            albumToUpdate.setAlbumGenres(genres);
        }

        if (file != null) {
            String imageUrl = fileUploadService.uploadAlbumImageFile(file);
            albumToUpdate.setImage(imageUrl);
        }

        if (!request.getTitle().equals(albumToUpdate.getTitle())) {
            albumToUpdate.setTitle(request.getTitle());
        }

        if (request.getReleaseDate() != null &&
                !request.getReleaseDate().equals(albumToUpdate.getReleaseDate())) {
            albumToUpdate.setReleaseDate(request.getReleaseDate());
        }

        if (!request.getSlug().equals(albumToUpdate.getSlug())) {
            albumToUpdate.setSlug(request.getSlug());
        }

        return modelMapper.map(albumToUpdate, AlbumDto.class);
    }

    @Override
    @Transactional
    public MessageDto deleteAlbum(UUID albumId) {
        albumRepository.findById(albumId)
                .orElseThrow(() -> new ResourceNotFoundException("Album not found."));

        albumRepository.deleteById(albumId);

        return new MessageDto("Album deleted.");
    }
}
