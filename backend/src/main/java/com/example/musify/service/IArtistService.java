package com.example.musify.service;

import com.example.musify.auth.dto.response.MessageDto;
import com.example.musify.dto.request.CreateArtistDto;
import com.example.musify.dto.request.UpdateArtistDto;
import com.example.musify.dto.response.ArtistDto;
import com.example.musify.dto.response.PageDto;
import com.example.musify.dto.response.RecentArtistDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface IArtistService {

    PageDto<ArtistDto> getArtists(int page);
    List<RecentArtistDto> getMostRecentArtists();
    PageDto<ArtistDto>getArtistsByGenre(String slug, int page);
    ArtistDto getArtist(String artistSlug);
    ArtistDto createArtist(CreateArtistDto createArtistDto, MultipartFile file) throws IOException;
    ArtistDto updateArtist(UpdateArtistDto updateArtistDto, MultipartFile file, UUID artistId) throws IOException;
    MessageDto deleteArtist(UUID artistId);
}
