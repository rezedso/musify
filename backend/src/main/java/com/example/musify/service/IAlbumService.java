package com.example.musify.service;

import com.example.musify.auth.dto.response.MessageDto;
import com.example.musify.dto.request.CreateAlbumDto;
import com.example.musify.dto.request.RateAlbumDto;
import com.example.musify.dto.request.UpdateAlbumDto;
import com.example.musify.dto.request.UserIdDto;
import com.example.musify.dto.response.AlbumDto;
import com.example.musify.dto.response.PageDto;
import com.example.musify.dto.response.RecentAlbumDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface IAlbumService {
    PageDto<AlbumDto> getAlbums(int page);

    AlbumDto getAlbum(String artistSlug, String albumSlug);

    List<RecentAlbumDto> getMostRecentAlbums();

    List<AlbumDto> getAlbumsByArtist(String artistSlug);

    PageDto<AlbumDto> getAlbumsByGenre(String slug, int page);

    AlbumDto createAlbum(CreateAlbumDto createAlbumDto, MultipartFile file) throws IOException;

    AlbumDto rateAlbum(RateAlbumDto rateAlbumDto, UUID albumId);

    AlbumDto updateAlbum(UpdateAlbumDto updateAlbumDto, MultipartFile file, UUID albumId) throws IOException;

    MessageDto addAlbumToList(UUID listId, UUID albumId, UserIdDto request);

    MessageDto removeAlbumFromList(UUID listId, UUID albumId, UserIdDto request);

    MessageDto deleteAlbum(UUID albumId);
}
