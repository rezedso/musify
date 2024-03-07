package com.example.musify.service.impl;

import com.example.musify.auth.dto.response.MessageDto;
import com.example.musify.dto.request.CreateArtistDto;
import com.example.musify.dto.request.UpdateArtistDto;
import com.example.musify.dto.response.ArtistDto;
import com.example.musify.dto.response.PageDto;
import com.example.musify.dto.response.RecentArtistDto;
import com.example.musify.entity.Album;
import com.example.musify.entity.Artist;
import com.example.musify.entity.Genre;
import com.example.musify.exception.ResourceNotFoundException;
import com.example.musify.repository.AlbumListRepository;
import com.example.musify.repository.AlbumRepository;
import com.example.musify.repository.ArtistRepository;
import com.example.musify.repository.GenreRepository;
import com.example.musify.service.IArtistService;
import com.example.musify.service.IFileUploadService;
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
public class ArtistServiceImpl implements IArtistService {
    private final ArtistRepository artistRepository;
    private final GenreRepository genreRepository;
    private final AlbumListRepository albumListRepository;
    private final IFileUploadService fileUploadService;
    private final AlbumRepository albumRepository;
    private final ModelMapper modelMapper;

    @Override
    public PageDto<ArtistDto> getArtists(int page) {
        Page<Artist> pageRequest = artistRepository.findAll(PageRequest.of(page - 1, 20));
        int totalPages = pageRequest.getTotalPages();
        int currentPage = pageRequest.getNumber() + 1;
        Long totalElements = pageRequest.getTotalElements();

        List<ArtistDto> content = pageRequest.getContent().stream().
                map(artist -> modelMapper.map(artist, ArtistDto.class)).toList();
        return new PageDto<>(content, totalPages, currentPage, totalElements);
    }

    @Override
    public List<RecentArtistDto> getMostRecentArtists() {
        Pageable pageable = PageRequest.of(0, 4);

        return artistRepository.findMostRecentArtists(pageable).stream().map(
                artist -> modelMapper.map(artist, RecentArtistDto.class)).toList();
    }

    @Override
    public PageDto<ArtistDto> getArtistsByGenre(String slug, int page) {
        Page<Artist> pageRequest = artistRepository.findByGenre(slug, PageRequest.of(page - 1, 20));
        int totalPages = pageRequest.getTotalPages();
        int currentPage = pageRequest.getNumber() + 1;
        Long totalElements = pageRequest.getTotalElements();

        List<ArtistDto> content = pageRequest.getContent().stream().
                map(artist -> modelMapper.map(artist, ArtistDto.class)).toList();
        return new PageDto<>(content, totalPages, currentPage, totalElements);
    }

    @Override
    public ArtistDto getArtist(String artistSlug) {
        Artist artist = artistRepository.findBySlug(artistSlug)
                .orElseThrow(() -> new ResourceNotFoundException("Artist not found."));

        return modelMapper.map(artist, ArtistDto.class);
    }

    @Override
    public ArtistDto createArtist(CreateArtistDto request, MultipartFile file) throws IOException {
        Artist artist = Artist.builder()
                .name(request.getName())
                .originCountry(request.getOriginCountry())
                .formedYear(request.getFormedYear())
                .slug(request.getSlug())
                .build();

        Set<Genre> genres = new HashSet<>();

        if (request.getArtistGenres() != null) {
            Set<String> genreNames = request.getArtistGenres();
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
            String artistImage = fileUploadService.uploadArtistImageFile(file);
            artist.setImage(artistImage);
        }

        artist.setArtistGenres(genres);
        Artist savedArtist = artistRepository.save(artist);

        return modelMapper.map(savedArtist, ArtistDto.class);
    }

    @Override
    @Transactional
    public ArtistDto updateArtist(UpdateArtistDto request, MultipartFile file, UUID artistId) throws IOException {
        Artist artist = artistRepository.findById(artistId)
                .orElseThrow(() -> new ResourceNotFoundException("Artist not found."));

        Set<Genre> genres = new HashSet<>();
        List<Genre> newGenres = new ArrayList<>();

        if (request.getArtistGenres() != null && !request.getArtistGenres().isEmpty()) {
            for (String genreName : request.getArtistGenres()) {
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
            artist.setArtistGenres(genres);
        }

        if (file != null) {
            String imageUrl = fileUploadService.uploadArtistImageFile(file);
            artist.setImage(imageUrl);
        }

        if (!artist.getName().equals(request.getName())) {
            artist.setName(request.getName());
        }

        if (!artist.getFormedYear().equals(request.getFormedYear())) {
            artist.setFormedYear(request.getFormedYear());
        }

        if (!artist.getOriginCountry().equals(request.getOriginCountry())) {
            artist.setOriginCountry(request.getOriginCountry());
            updateArtistAlbumsCountry(artist,request.getOriginCountry());
        }

        return modelMapper.map(artist, ArtistDto.class);
    }

    private void updateArtistAlbumsCountry(Artist artist,String newCountry){
        List<Album> albums = albumRepository.findByArtist(artist);
        for (Album album: albums){
            album.setOriginCountry(newCountry);
        }
        albumRepository.saveAll(albums);
    }

    @Override
    @Transactional
    public MessageDto deleteArtist(UUID artistId) {
        Artist artist = artistRepository.findById(artistId)
                .orElseThrow(() -> new ResourceNotFoundException("Artist not found."));

        albumRepository.deleteByArtist(artistId);
        albumListRepository.deleteAlbumsByArtistId(artistId);
        artistRepository.delete(artist);

        return new MessageDto("Artist deleted.");
    }
}
