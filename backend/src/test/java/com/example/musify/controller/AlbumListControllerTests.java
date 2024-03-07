package com.example.musify.controller;

import com.example.musify.auth.dto.response.MessageDto;
import com.example.musify.auth.service.JwtService;
import com.example.musify.auth.service.UserDetailsServiceImpl;
import com.example.musify.dto.request.AlbumListUserIdDto;
import com.example.musify.dto.request.CreateAlbumListDto;
import com.example.musify.dto.request.UpdateAlbumListDto;
import com.example.musify.dto.response.AlbumListDto;
import com.example.musify.dto.response.AlbumListNameDto;
import com.example.musify.dto.response.AlbumListsCountDto;
import com.example.musify.exception.ResourceNotFoundException;
import com.example.musify.repository.AlbumListRepository;
import com.example.musify.repository.TokenRepository;
import com.example.musify.repository.UserRepository;
import com.example.musify.service.IUtilService;
import com.example.musify.service.impl.AlbumListServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AlbumListController.class)
@WithMockUser(username = "user", password = "test", roles = {"USER", "ADMIN"})
@AutoConfigureMockMvc(addFilters = false)
public class AlbumListControllerTests {
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
    private AlbumListRepository albumListRepository;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private AlbumListServiceImpl albumListService;

    @Test
    public void getListsCount_Success() throws Exception {
        given(albumListService.getUserListsCount(anyString())).willReturn(new AlbumListsCountDto());

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/album-lists/count/users/{username}", "user")
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk());
    }

    @Test
    public void getListsCount_WhenUserNotFound_ThrowsResourceNotFoundException() throws Exception {
        given(albumListService.getUserListsCount(anyString()))
                .willThrow(new ResourceNotFoundException("User not found"));

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/album-lists/count/users/{username}", "user")
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound());
    }

    @Test
    public void getUserListsSummary_Success() throws Exception {
        given(albumListService.getUserListNames(anyString()))
                .willReturn(List.of(new AlbumListNameDto()));

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/album-lists/summary/users/{username}", "user")
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk());
    }

    @Test
    public void getUserListsSummary_WhenUserNotFound_ThrowsResourceNotFoundException() throws Exception {
        given(albumListService.getUserListNames(anyString()))
                .willThrow(new ResourceNotFoundException("User not found"));

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/album-lists/summary/users/{username}", "user")
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound());
    }

    @Test
    public void getUserList_Success() throws Exception {
        given(albumListService.getList(anyString(), anyString()))
                .willReturn(new AlbumListDto());

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/album-lists/{listName}/users/{username}", "list", "user")
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk());
    }

    @Test
    public void getUserList_WhenListNotFound_ThrowsResourceNotFoundException() throws Exception {
        given(albumListService.getList(anyString(), anyString()))
                .willThrow(new ResourceNotFoundException("List not found"));

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/album-lists/{listName}/users/{username}", "list", "user")
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCreateList() throws Exception {
        CreateAlbumListDto createAlbumListDto = CreateAlbumListDto.builder()
                .name("list")
                .build();

        given(albumListService.createAlbumList(any(CreateAlbumListDto.class)))
                .willReturn(new AlbumListDto());

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/album-lists")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createAlbumListDto));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isCreated());
    }

    @Test
    public void testUpdateAlbumList_Success() throws Exception {
        UpdateAlbumListDto updateAlbumListDto = UpdateAlbumListDto.builder()
                .name("new name")
                .userId(UUID.randomUUID())
                .build();

        UUID listId = UUID.randomUUID();

        given(albumListService.updateAlbumList(any(UpdateAlbumListDto.class), eq(listId)))
                .willReturn(new AlbumListDto());

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/album-lists/{albumListId}", listId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateAlbumListDto));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk());
    }

    @Test
    public void testUpdateAlbumList_WhenListNotFound_ThrowsResourceNotFoundException() throws Exception {
        UpdateAlbumListDto updateAlbumListDto = UpdateAlbumListDto.builder()
                .name("new name")
                .userId(UUID.randomUUID())
                .build();

        given(albumListService.updateAlbumList(any(UpdateAlbumListDto.class), any(UUID.class)))
                .willThrow(new ResourceNotFoundException("List not found"));

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/album-lists/{albumListId}", UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateAlbumListDto));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteAlbumList_Success() throws Exception {
        UUID listId = UUID.randomUUID();

        AlbumListUserIdDto albumListUserIdDto = AlbumListUserIdDto.builder()
                .id(UUID.randomUUID())
                .build();

        given(albumListService.deleteAlbumList(listId, albumListUserIdDto)).willReturn(new MessageDto("List deleted."));

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/album-lists/{listId}", listId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(albumListUserIdDto));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("List deleted."));
    }

    @Test
    public void testDeleteAlbumList_WhenListNotFound_ThrowsResourceNotFoundException() throws Exception {
        UUID listId = UUID.randomUUID();

        AlbumListUserIdDto albumListUserIdDto = AlbumListUserIdDto.builder()
                .id(UUID.randomUUID())
                .build();

        given(albumListService.deleteAlbumList(listId, albumListUserIdDto))
                .willThrow(new ResourceNotFoundException("List not found."));

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/album-lists/{listId}", listId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(albumListUserIdDto));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound());
    }
}
