package com.example.musify.controller;

import com.example.musify.auth.dto.response.MessageDto;
import com.example.musify.auth.service.JwtService;
import com.example.musify.auth.service.UserDetailsServiceImpl;
import com.example.musify.dto.request.UpdatePasswordDto;
import com.example.musify.dto.request.UpdateUserDto;
import com.example.musify.dto.request.UserRoleDto;
import com.example.musify.dto.response.UpdatedUserDto;
import com.example.musify.dto.response.UserDto;
import com.example.musify.exception.ResourceNotFoundException;
import com.example.musify.repository.*;
import com.example.musify.service.IUtilService;
import com.example.musify.service.impl.UserServiceImpl;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@WithMockUser(username = "user", password = "test", roles = {"USER", "ADMIN"})
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTests {
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
    private UserServiceImpl userService;
    @MockBean
    private UserDetailsServiceImpl userDetailsService;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private AlbumListRepository albumListRepository;
    @MockBean
    private RoleRepository roleRepository;

    @Test
    public void testGetUser_Success() throws Exception {
        given(userService.getUser(anyString())).willReturn(new UserDto());

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/users/{username}", "username")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk());
    }

    @Test
    public void testGetUser_WhenUserNotFound_ThrowsResourceNotFoundException() throws Exception {
        given(userService.getUser(anyString()))
                .willThrow(new ResourceNotFoundException("User not found."));

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/users/{username}","username")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetUsers() throws Exception {
        given(userService.getUsers()).willReturn(List.of(new UserDto()));

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/users")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk());
    }

    @Test
    public void testUpdateUser_WithImage() throws Exception {
        UpdateUserDto updateUserDto = UpdateUserDto.builder()
                .username("new username")
                .build();

        String userDtoJson = objectMapper.writeValueAsString(updateUserDto);

        given(userService.updateUser(any(UpdateUserDto.class), any(MultipartFile.class)))
                .willReturn(new UpdatedUserDto());

        MockMultipartFile file = new MockMultipartFile("image", "test.jpg", "image/jpeg", "some image content".getBytes());
        MockMultipartFile jsonFile = new MockMultipartFile("user", "", "application/json", userDtoJson.getBytes());

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .multipart(HttpMethod.PUT, "/users/update")
                .file(file)
                .file(jsonFile)
                .contentType(MediaType.MULTIPART_FORM_DATA);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk());
    }

    @Test
    public void testUpdateUser_WithoutImage() throws Exception {
        UpdateUserDto updateUserDto = UpdateUserDto.builder()
                .username("new username")
                .build();

        String userDtoJson = objectMapper.writeValueAsString(updateUserDto);

        given(userService.updateUser(any(UpdateUserDto.class), eq(null)))
                .willReturn(new UpdatedUserDto());

        MockMultipartFile jsonFile = new MockMultipartFile("user", "", "application/json", userDtoJson.getBytes());

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .multipart(HttpMethod.PUT, "/users/update")
                .file(jsonFile)
                .contentType(MediaType.MULTIPART_FORM_DATA);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk());
    }

    @Test
    public void testUpdatePassword_Success() throws Exception {
        UpdatePasswordDto requestDto = UpdatePasswordDto.builder()
                .currentPassword("oldPassword")
                .newPassword("newPassword")
                .confirmationPassword("newPassword")
                .build();

        given(userService.updatePassword(requestDto)).willReturn(new MessageDto("Password updated."));

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/users/update-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Password updated."));
    }

    @Test
    public void testUpdatePassword_WhenPasswordIsWrong_ThrowsIllegalStateException() throws Exception {
        UpdatePasswordDto requestDto = UpdatePasswordDto.builder()
                .currentPassword("oldPassword")
                .newPassword("newPassword")
                .confirmationPassword("mismatchedPassword")
                .build();

        given(userService.updatePassword(requestDto))
                .willThrow(new IllegalStateException("Password is wrong."));

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/users/update-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdatePassword_WhenPasswordsDoNotMatch_ThrowsIllegalStateException() throws Exception {
        UpdatePasswordDto requestDto = UpdatePasswordDto.builder()
                .currentPassword("oldPassword")
                .newPassword("newPassword")
                .confirmationPassword("newPassword")
                .build();

        given(userService.updatePassword(requestDto)).willThrow(new IllegalStateException("Password don't match."));

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/users/update-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdateUserRole_Success() throws Exception {
        UserRoleDto userRoleDto = UserRoleDto.builder()
                .role("ROLE_ADMIN")
                .build();

        given(userService.updateUserRole(any(UserRoleDto.class),any(UUID.class),anyBoolean()))
                .willReturn(new UserDto());

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/users/{userId}/update-role?addRole={addRole}", UUID.randomUUID(),true)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRoleDto));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk());
    }

    @Test
    public void testUpdateUserRole_WhenUserNotFound_ThrowsResourceNotFoundException() throws Exception {
        UserRoleDto userRoleDto = UserRoleDto.builder()
                .role("ROLE_ADMIN")
                .build();

        given(userService.updateUserRole(any(UserRoleDto.class),any(UUID.class),anyBoolean()))
                .willThrow(new ResourceNotFoundException("User not found."));

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/users/{userId}/update-role?addRole={addRole}", UUID.randomUUID(),true)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRoleDto));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteUser_Success() throws Exception {
        given(userService.deleteUser(any(UUID.class)))
                .willReturn(new MessageDto("User deleted successfully."));

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/users/{userId}", UUID.randomUUID())
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("User deleted successfully."));
    }

    @Test
    public void testDeleteUser_WhenUserNotFound_ThrowsResourceNotFoundException() throws Exception {
        given(userService.deleteUser(any(UUID.class)))
                .willThrow(new ResourceNotFoundException("User not found"));

        mockMvc.perform(MockMvcRequestBuilders.delete("/users/{userId}", UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }
}
