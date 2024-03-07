package com.example.musify.controller;


import com.example.musify.auth.dto.response.MessageDto;
import com.example.musify.auth.service.JwtService;
import com.example.musify.auth.service.UserDetailsServiceImpl;
import com.example.musify.dto.request.CreateArtistDto;
import com.example.musify.dto.request.UpdateArtistDto;
import com.example.musify.dto.response.ArtistDto;
import com.example.musify.dto.response.PageDto;
import com.example.musify.dto.response.RecentArtistDto;
import com.example.musify.exception.ResourceNotFoundException;
import com.example.musify.repository.AlbumListRepository;
import com.example.musify.repository.ArtistRepository;
import com.example.musify.repository.GenreRepository;
import com.example.musify.repository.TokenRepository;
import com.example.musify.service.IFileUploadService;
import com.example.musify.service.IUtilService;
import com.example.musify.service.impl.ArtistServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.time.Year;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ArtistController.class)
@WithMockUser(username = "user", password = "test", roles = {"USER", "ADMIN"})
@AutoConfigureMockMvc(addFilters = false)
public class ArtistControllerTests {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private TokenRepository tokenRepository;
    @MockBean
    private JwtService jwtService;
    @MockBean
    private IUtilService utilService;
    @MockBean
    private UserDetailsServiceImpl userDetailsService;
    @MockBean
    private ArtistRepository artistRepository;
    @MockBean
    private GenreRepository genreRepository;
    @MockBean
    private AlbumListRepository albumListRepository;
    @MockBean
    private IFileUploadService fileUploadService;
    @MockBean
    private ArtistServiceImpl artistService;

    @Test
    public void testGetArtists() throws Exception {
        given(artistService.getArtists(anyInt())).willReturn(
                new PageDto<>(List.of(new ArtistDto()), 1, 1, 1L));

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/artists/page/{page}", 1)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isNotEmpty())
                .andExpect(jsonPath("$.currentPage").isNotEmpty())
                .andExpect(jsonPath("$.totalElements").isNotEmpty())
                .andExpect(jsonPath("$.totalPages").isNotEmpty());
    }

    @Test
    public void testGetMostRecentArtists() throws Exception {
        given(artistService.getMostRecentArtists()).willReturn(List.of(new RecentArtistDto()));

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/artists/recent")
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk());
    }


    @Test
    public void testGetArtistsByGenre() throws Exception {
        given(artistService.getArtistsByGenre(anyString(), anyInt())).willReturn(
                new PageDto<>(List.of(new ArtistDto()), 1, 1, 1L));

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/artists/genres/{genreSlug}/page/{page}", "genre",1)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isNotEmpty())
                .andExpect(jsonPath("$.currentPage").isNotEmpty())
                .andExpect(jsonPath("$.totalElements").isNotEmpty())
                .andExpect(jsonPath("$.totalPages").isNotEmpty());
    }

    @Test
    public void testGetArtist_Success() throws Exception {
        given(artistService.getArtist(anyString())).willReturn(new ArtistDto());

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/artists/{artistSlug}", "artist")
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk());
    }

    @Test
    public void testGetArtist_WhenArtistNotFound_ThrowsResourceNotFoundException() throws Exception {
        given(artistService.getArtist(anyString()))
                .willThrow(new ResourceNotFoundException("Artist not found."));

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/artists/{artistSlug}", "artist")
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCreateArtistWithFile() throws Exception {
        CreateArtistDto createArtistDto =CreateArtistDto.builder()
                .name("artist")
                .artistGenres(Set.of("genre1","genre2"))
                .slug("artist")
                .originCountry("Argentina")
                .formedYear(Year.of(2011))
                .build();

        String artistDto = objectMapper.writeValueAsString(createArtistDto);

        given(artistService.createArtist(any(CreateArtistDto.class), any(MultipartFile.class)))
                .willReturn(new ArtistDto());

        MockMultipartFile imageFile = new MockMultipartFile("image", "image.jpg", MediaType.IMAGE_JPEG_VALUE, "image content".getBytes());
        MockMultipartFile jsonFile = new MockMultipartFile("artist", "", "application/json", artistDto.getBytes());

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .multipart("/artists")
                .file(imageFile)
                .file(jsonFile)
                .contentType(MediaType.MULTIPART_FORM_DATA);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isCreated());
    }

    @Test
    public void testCreateArtistWithoutFile() throws Exception {
        CreateArtistDto createArtistDto =CreateArtistDto.builder()
                .name("artist")
                .artistGenres(Set.of("genre1","genre2"))
                .slug("artist")
                .originCountry("Argentina")
                .formedYear(Year.of(2011))
                .build();
        String artistDto = objectMapper.writeValueAsString(createArtistDto);

        given(artistService.createArtist(any(CreateArtistDto.class), eq(null)))
                .willReturn(new ArtistDto());

        MockMultipartFile jsonFile = new MockMultipartFile("artist", "", "application/json", artistDto.getBytes());

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .multipart("/artists")
                .file(jsonFile)
                .contentType(MediaType.MULTIPART_FORM_DATA);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isCreated());
    }

    @Test
    public void testUpdateArtistWithFile_Success() throws Exception {
        UpdateArtistDto updateAlbumDto =new UpdateArtistDto();

        String artistDtoJson = objectMapper.writeValueAsString(updateAlbumDto);
        UUID artistId = UUID.randomUUID();

        given(artistService.updateArtist(any(UpdateArtistDto.class), any(MultipartFile.class), eq(artistId)))
                .willReturn(new ArtistDto());

        MockMultipartFile imageFile = new MockMultipartFile("image", "image.jpg", MediaType.IMAGE_JPEG_VALUE, "image content".getBytes());
        MockMultipartFile jsonFile = new MockMultipartFile("artist", "", "application/json", artistDtoJson.getBytes());

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .multipart(HttpMethod.PUT, "/artists/{artistId}", artistId)
                .file(imageFile)
                .file(jsonFile)
                .contentType(MediaType.MULTIPART_FORM_DATA);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk());
    }

    @Test
    public void testUpdateArtistWithoutFile_Success() throws Exception {
        UpdateArtistDto updateAlbumDto =new UpdateArtistDto();

        String artistDtoJson = objectMapper.writeValueAsString(updateAlbumDto);
        UUID artistId = UUID.randomUUID();

        given(artistService.updateArtist(any(UpdateArtistDto.class), eq(null), eq(artistId)))
                .willReturn(new ArtistDto());

        MockMultipartFile jsonFile = new MockMultipartFile("artist", "", "application/json", artistDtoJson.getBytes());

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .multipart(HttpMethod.PUT, "/artists/{artistId}", artistId)
                .file(jsonFile)
                .contentType(MediaType.MULTIPART_FORM_DATA);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk());
    }

    @Test
    public void testUpdateArtist_WhenAlbumNotFound_ThrowsResourceNotFoundException() throws Exception {
        UpdateArtistDto updateAlbumDto =new UpdateArtistDto();

        String artistDtoJson = objectMapper.writeValueAsString(updateAlbumDto);

        given(artistService.updateArtist(any(UpdateArtistDto.class), eq(null), any(UUID.class)))
                .willThrow(new ResourceNotFoundException("Artist not found."));

        MockMultipartFile jsonFile = new MockMultipartFile("artist", "", "application/json", artistDtoJson.getBytes());

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .multipart(HttpMethod.PUT, "/artists/{artistId}", UUID.randomUUID())
                .file(jsonFile)
                .contentType(MediaType.MULTIPART_FORM_DATA);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteArtist_Success() throws Exception {
        given(artistService.deleteArtist(any(UUID.class))).willReturn(new MessageDto("Artist deleted."));

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/artists/{artistId}", UUID.randomUUID())
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Artist deleted."));
    }

    @Test
    public void testDeleteArtist_WhenArtistNotFound_ThrowsResourceNotFoundException() throws Exception {
        given(artistService.deleteArtist(any(UUID.class)))
                .willThrow(new ResourceNotFoundException("Artist not found"));

        mockMvc.perform(MockMvcRequestBuilders.delete("/artists/{artistId}", UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }
}
