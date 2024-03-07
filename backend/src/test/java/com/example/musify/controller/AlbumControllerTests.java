package com.example.musify.controller;

import com.example.musify.auth.dto.response.MessageDto;
import com.example.musify.auth.service.JwtService;
import com.example.musify.auth.service.UserDetailsServiceImpl;
import com.example.musify.dto.request.CreateAlbumDto;
import com.example.musify.dto.request.RateAlbumDto;
import com.example.musify.dto.request.UpdateAlbumDto;
import com.example.musify.dto.request.UserIdDto;
import com.example.musify.dto.response.AlbumDto;
import com.example.musify.dto.response.PageDto;
import com.example.musify.dto.response.RecentAlbumDto;
import com.example.musify.exception.ResourceNotFoundException;
import com.example.musify.repository.*;
import com.example.musify.service.IFileUploadService;
import com.example.musify.service.IUtilService;
import com.example.musify.service.impl.AlbumServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
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

import java.time.OffsetDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AlbumController.class)
@WithMockUser(username = "user", password = "test", roles = {"USER", "ADMIN"})
@AutoConfigureMockMvc(addFilters = false)
public class AlbumControllerTests {
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
    private AlbumServiceImpl albumService;
    @MockBean
    private AlbumRepository albumRepository;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private AlbumListRepository albumListRepository;
    @MockBean
    private ArtistRepository artistRepository;
    @MockBean
    private AlbumRatingRepository albumRatingRepository;
    @MockBean
    private GenreRepository genreRepository;
    @MockBean
    private IFileUploadService fileUploadService;

    private UserIdDto userIdDto;
    private CreateAlbumDto createAlbumDto;
    private UpdateAlbumDto updateAlbumDto;
    private final UUID listId = UUID.randomUUID();
    private final UUID albumId = UUID.randomUUID();

    @BeforeEach
    void setup() {
        userIdDto = new UserIdDto(UUID.randomUUID().toString());

        createAlbumDto = CreateAlbumDto.builder()
                .title("album")
                .slug("album")
                .artistName("test")
                .albumGenres(Set.of("genre1", "genre2"))
                .releaseDate(OffsetDateTime.parse("2010-11-08T00:00:00-03:00").toInstant())
                .build();

        updateAlbumDto = UpdateAlbumDto.builder()
                .title("new title")
                .albumGenres(Set.of("new genre"))
                .slug("slug")
                .releaseDate(OffsetDateTime.parse("2007-04-29T00:00:00-03:00").toInstant())
                .build();
    }

    @Test
    public void testGetAlbums() throws Exception {
        given(albumService.getAlbums(1)).willReturn(
                new PageDto<>(List.of(new AlbumDto()), 1, 1, 1L));

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/albums/page/{page}", 1)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isNotEmpty())
                .andExpect(jsonPath("$.currentPage").isNotEmpty())
                .andExpect(jsonPath("$.totalElements").isNotEmpty())
                .andExpect(jsonPath("$.totalPages").isNotEmpty());
    }

    @Test
    public void testGetAlbum() throws Exception {
        given(albumService.getAlbum(anyString(), anyString())).willReturn(new AlbumDto());

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/albums/{artistSlug}/{albumSlug}", "artist", "album")
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk());
    }

    @Test
    public void testGetAlbum_WhenAlbumNotFound_ThrowsResourceNotFoundException() throws Exception {
        given(albumService.getAlbum(anyString(), anyString()))
                .willThrow(new ResourceNotFoundException("Album not found"));

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/albums/{artistSlug}/{albumSlug}", "artist", "album")
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetMostRecentAlbums() throws Exception {
        given(albumService.getMostRecentAlbums()).willReturn(List.of(new RecentAlbumDto()));

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/albums/recent")
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk());
    }


    @Test
    public void testGetAlbumsByArtist() throws Exception {
        given(albumService.getAlbumsByArtist(anyString())).willReturn(List.of(new AlbumDto()));

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/albums/artists/{artistSlug}", "test")
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk());
    }

    @Test
    public void testGetAlbumsByArtist_WhenArtistNotFound_ThrowsResourceNotFoundException() throws Exception {
        given(albumService.getAlbumsByArtist(anyString()))
                .willThrow(new ResourceNotFoundException("Artist not found"));

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/albums/artists/{artistSlug}", "artist")
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetAlbumsByGenre() throws Exception {
        given(albumService.getAlbumsByGenre(anyString(), anyInt())).willReturn
                (new PageDto<>(List.of(new AlbumDto()), 1, 1, 1L));

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/albums/genres/{genreSlug}/page/{page}", "genre", 1)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk());
    }

    @Test
    public void testGetAlbumsByGenre_WhenGenreNotFound_ThrowsResourceNotFoundException() throws Exception {
        given(albumService.getAlbumsByGenre(anyString(), anyInt()))
                .willThrow(new ResourceNotFoundException("Genre not found"));

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/albums/genres/{genreSlug}/page/{page}", "genre", 1)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCreateAlbumWithFile_Success() throws Exception {
        String albumDto = objectMapper.writeValueAsString(createAlbumDto);

        given(albumService.createAlbum(any(CreateAlbumDto.class), any(MultipartFile.class)))
                .willReturn(new AlbumDto());

        MockMultipartFile imageFile = new MockMultipartFile("image", "image.jpg", MediaType.IMAGE_JPEG_VALUE, "image content".getBytes());
        MockMultipartFile jsonFile = new MockMultipartFile("album", "", "application/json", albumDto.getBytes());

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .multipart("/albums")
                .file(imageFile)
                .file(jsonFile)
                .contentType(MediaType.MULTIPART_FORM_DATA);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isCreated());
    }

    @Test
    public void testCreateAlbumWithoutFile_Success() throws Exception {
        String albumDto = objectMapper.writeValueAsString(createAlbumDto);

        given(albumService.createAlbum(any(CreateAlbumDto.class), eq(null)))
                .willReturn(new AlbumDto());

        MockMultipartFile jsonFile = new MockMultipartFile("album", "", "application/json", albumDto.getBytes());

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .multipart("/albums")
                .file(jsonFile)
                .contentType(MediaType.MULTIPART_FORM_DATA);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isCreated());
    }

    @Test
    public void testCreateAlbum_WhenArtistNotFound_ThrowsResourceNotFoundException() throws Exception {
        String albumDto = objectMapper.writeValueAsString(createAlbumDto);

        given(albumService.createAlbum(any(CreateAlbumDto.class), any(MultipartFile.class)))
                .willThrow(new ResourceNotFoundException("Artist not found."));

        MockMultipartFile imageFile = new MockMultipartFile("image", "image.jpg", MediaType.IMAGE_JPEG_VALUE, "image content".getBytes());
        MockMultipartFile jsonFile = new MockMultipartFile("album", "", "application/json", albumDto.getBytes());

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .multipart("/albums")
                .file(imageFile)
                .file(jsonFile)
                .contentType(MediaType.MULTIPART_FORM_DATA);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUpdateAlbumWithFile_Success() throws Exception {
        String postDtoJson = objectMapper.writeValueAsString(updateAlbumDto);
        UUID albumId = UUID.randomUUID();

        given(albumService.updateAlbum(any(UpdateAlbumDto.class), any(MultipartFile.class), eq(albumId)))
                .willReturn(new AlbumDto());

        MockMultipartFile imageFile = new MockMultipartFile("image", "image.jpg", MediaType.IMAGE_JPEG_VALUE, "image content".getBytes());
        MockMultipartFile jsonFile = new MockMultipartFile("album", "", "application/json", postDtoJson.getBytes());

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .multipart(HttpMethod.PUT, "/albums/{albumId}", albumId)
                .file(imageFile)
                .file(jsonFile)
                .contentType(MediaType.MULTIPART_FORM_DATA);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk());
    }

    @Test
    public void testUpdateAlbumWithoutFile_Success() throws Exception {
        UUID albumId = UUID.randomUUID();

        String postDtoJson = objectMapper.writeValueAsString(updateAlbumDto);

        given(albumService.updateAlbum(any(UpdateAlbumDto.class), eq(null), eq(albumId)))
                .willReturn(new AlbumDto());

        MockMultipartFile jsonFile = new MockMultipartFile("album", "", "application/json", postDtoJson.getBytes());

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .multipart(HttpMethod.PUT, "/albums/{albumId}", albumId)
                .file(jsonFile)
                .contentType(MediaType.MULTIPART_FORM_DATA);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk());
    }

    @Test
    public void testUpdateAlbum_WhenAlbumNotFound_ThrowsResourceNotFoundException() throws Exception {
        String albumDtoJson = objectMapper.writeValueAsString(updateAlbumDto);

        given(albumService.updateAlbum(any(UpdateAlbumDto.class), any(MultipartFile.class), any(UUID.class)))
                .willThrow(new ResourceNotFoundException("Album not found"));

        MockMultipartFile imageFile = new MockMultipartFile("image", "image.jpg", MediaType.IMAGE_JPEG_VALUE, new byte[0]);
        MockMultipartFile jsonFile = new MockMultipartFile("album", "", "application/json", albumDtoJson.getBytes());

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .multipart(HttpMethod.PUT, "/albums/{albumId}", UUID.randomUUID())
                .file(imageFile)
                .file(jsonFile)
                .contentType(MediaType.MULTIPART_FORM_DATA);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound());
    }

    @Test
    public void testRateAlbum_Success() throws Exception {
        RateAlbumDto rateAlbumDto = RateAlbumDto.builder()
                .rating(4.5)
                .build();

        UUID albumId = UUID.randomUUID();

        given(albumService.rateAlbum(any(RateAlbumDto.class), eq(albumId)))
                .willReturn(new AlbumDto());

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/albums/rate/{albumId}", albumId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rateAlbumDto));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk());
    }

    @Test
    public void testRateAlbum_WhenAlbumNotFound_ThrowsResourceNotFoundException() throws Exception {
        RateAlbumDto rateAlbumDto = RateAlbumDto.builder()
                .rating(4.5)
                .build();

        given(albumService.rateAlbum(any(RateAlbumDto.class), any(UUID.class)))
                .willThrow(new ResourceNotFoundException("Album not found."));

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/albums/rate/{albumId}", UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rateAlbumDto));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound());
    }

    @Test
    public void testAddAlbumToList_Success() throws Exception {
        UUID listId = UUID.randomUUID();
        UUID albumId = UUID.randomUUID();

        given(albumService.addAlbumToList(listId, albumId, userIdDto))
                .willReturn(new MessageDto("Album added to list."));

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/albums/add/{listId}/{albumId}", listId, albumId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userIdDto));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Album added to list."));
    }

    @Test
    public void testAddAlbumToList_WhenListNotFound_ThrowsResourceNotFoundException() throws Exception {
        given(albumService.addAlbumToList(listId, albumId, userIdDto))
                .willThrow(new ResourceNotFoundException("Album List not found."));

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/albums/add/{listId}/{albumId}", listId, albumId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userIdDto));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound());
    }

    @Test
    public void testAddAlbumToList_WhenAlbumNotFound_ThrowsResourceNotFoundException() throws Exception {
        UUID listId = UUID.randomUUID();
        UUID albumId = UUID.randomUUID();

        given(albumService.addAlbumToList(listId, albumId, userIdDto))
                .willThrow(new ResourceNotFoundException("Album not found."));

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/albums/add/{listId}/{albumId}", listId, albumId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userIdDto));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound());
    }

    @Test
    public void testRemoveAlbumFromList_Success() throws Exception {
        given(albumService.removeAlbumFromList(listId, albumId, userIdDto))
                .willReturn(new MessageDto("Album removed from list."));

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/albums/remove/{listId}/{albumId}", listId, albumId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userIdDto));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Album removed from list."));
    }

    @Test
    public void testRemoveAlbumFromList_WhenListNotFound_ThrowsResourceNotFoundException() throws Exception {
        given(albumService.removeAlbumFromList(listId, albumId, userIdDto))
                .willThrow(new ResourceNotFoundException("List not found"));

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/albums/remove/{listId}/{albumId}", listId, albumId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userIdDto));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound());
    }

    @Test
    public void testRemoveAlbumFromList_WhenAlbumNotFound_ThrowsResourceNotFoundException() throws Exception {
        given(albumService.removeAlbumFromList(listId, albumId, userIdDto))
                .willThrow(new ResourceNotFoundException("Album not found."));

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/albums/remove/{listId}/{albumId}", listId, albumId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userIdDto));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteAlbum_Success() throws Exception {
        given(albumService.deleteAlbum(any(UUID.class))).willReturn(new MessageDto("Album deleted."));

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/albums/{albumId}", UUID.randomUUID())
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Album deleted."));
    }

    @Test
    public void testDeleteAlbum_WhenCommentNotFound_ThrowsResourceNotFoundException() throws Exception {
        given(albumService.deleteAlbum(any(UUID.class)))
                .willThrow(new ResourceNotFoundException("Album not found."));

        mockMvc.perform(MockMvcRequestBuilders.delete("/albums/{albumId}", UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }
}
