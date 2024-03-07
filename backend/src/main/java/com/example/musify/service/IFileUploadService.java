package com.example.musify.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface IFileUploadService {
    String uploadUserImageFile(MultipartFile multipartFile) throws IOException;
    String uploadArtistImageFile(MultipartFile multipartFile) throws IOException;
    String uploadAlbumImageFile(MultipartFile multipartFile) throws IOException;
}
