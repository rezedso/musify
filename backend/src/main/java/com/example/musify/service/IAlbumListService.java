package com.example.musify.service;

import com.example.musify.auth.dto.response.MessageDto;
import com.example.musify.dto.request.AlbumListUserIdDto;
import com.example.musify.dto.request.CreateAlbumListDto;
import com.example.musify.dto.request.UpdateAlbumListDto;
import com.example.musify.dto.response.AlbumListDto;
import com.example.musify.dto.response.AlbumListNameDto;
import com.example.musify.dto.response.AlbumListsCountDto;

import java.util.List;
import java.util.UUID;

public interface IAlbumListService {
    AlbumListsCountDto getUserListsCount(String username);
    List<AlbumListNameDto> getUserListNames(String username);
    AlbumListDto getList(String listName, String username);
    AlbumListDto createAlbumList(CreateAlbumListDto createAlbumListDto);
    AlbumListDto updateAlbumList(UpdateAlbumListDto albumListRequestDto, UUID albumListId);
    MessageDto deleteAlbumList(UUID albumListId, AlbumListUserIdDto request);
}
