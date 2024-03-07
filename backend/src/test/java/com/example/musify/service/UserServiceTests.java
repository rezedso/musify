package com.example.musify.service;

import com.example.musify.auth.dto.response.MessageDto;
import com.example.musify.dto.request.UpdatePasswordDto;
import com.example.musify.dto.request.UpdateUserDto;
import com.example.musify.dto.request.UserRoleDto;
import com.example.musify.dto.response.UpdatedUserDto;
import com.example.musify.dto.response.UserDto;
import com.example.musify.entity.Role;
import com.example.musify.entity.User;
import com.example.musify.enumeration.ERole;
import com.example.musify.exception.ResourceNotFoundException;
import com.example.musify.repository.AlbumListRepository;
import com.example.musify.repository.RoleRepository;
import com.example.musify.repository.UserRepository;
import com.example.musify.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private IFileUploadService fileUploadService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AlbumListRepository albumListRepository;
    @Mock
    private IUtilService utilService;

    @Spy
    private ModelMapper modelMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User user1;

    @BeforeEach
    void setup() {
        user1 = User.builder()
                .username("testuser")
                .email("user@test.com")
                .password("password")
                .roles(new HashSet<>())
                .build();
        userRepository.save(user1);
    }


    @Test
    void testGetUser_Success() {
        given(userRepository.findByUsername(user1.getUsername())).willReturn(Optional.of(user1));

        UserDto result = userService.getUser(user1.getUsername());

        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo(user1.getUsername());
        assertThat(result.getEmail()).isEqualTo(user1.getEmail());

        verify(userRepository, times(1)).findByUsername(user1.getUsername());
    }

    @Test
    void testGetUser_WhenUserNotFound_ThrowsResourceNotFoundException() {
        given(userRepository.findByUsername(user1.getUsername())).willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getUser(user1.getUsername()));

        verify(userRepository, times(1)).findByUsername(user1.getUsername());
    }

    @Test
    void testGetUsers() {
        given(userRepository.findAll()).willReturn(List.of(user1));

        List<UserDto> result = userService.getUsers();

        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);

        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testUpdatePassword_WhenPasswordsMatch_UpdatesPassword() {
        UpdatePasswordDto updatePasswordDto = UpdatePasswordDto.builder()
                .currentPassword("oldPassword")
                .newPassword("newPassword")
                .confirmationPassword("newPassword")
                .build();

        user1.setPassword(passwordEncoder.encode("oldPassword"));

        given(utilService.getCurrentUser()).willReturn(user1);
        given(passwordEncoder.matches("oldPassword", user1.getPassword())).willReturn(true);

        MessageDto result = userService.updatePassword(updatePasswordDto);

        assertThat(result).isInstanceOf(MessageDto.class);
        assertThat(result.getMessage()).contains("Password updated.");
    }

    @Test
    void testUpdatePassword_WhenCurrentPasswordIsWrong_ThrowsIllegalStateException() {
        UpdatePasswordDto updatePasswordDto = UpdatePasswordDto.builder()
                .currentPassword("wrongPassword")
                .newPassword("newPassword")
                .confirmationPassword("newPassword")
                .build();

        user1.setPassword(passwordEncoder.encode("correctPassword"));

        given(utilService.getCurrentUser()).willReturn(user1);
        given(passwordEncoder.matches("wrongPassword", user1.getPassword())).willReturn(false);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                userService.updatePassword(updatePasswordDto));

        assertThat(exception.getMessage()).contains("Wrong password.");
    }

    @Test
    void testUpdatePassword_WhenPasswordsDoNotMatch_ThrowsIllegalStateException() {
        UpdatePasswordDto updatePasswordDto = UpdatePasswordDto.builder()
                .currentPassword("oldPassword")
                .newPassword("newPassword")
                .confirmationPassword("newPassword123")
                .build();

        User user = new User();
        user.setPassword(passwordEncoder.encode("oldPassword"));

        given(utilService.getCurrentUser()).willReturn(user);
        given(passwordEncoder.matches("oldPassword", user.getPassword())).willReturn(true);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                userService.updatePassword(updatePasswordDto));

        assertThat(exception.getMessage()).contains("Passwords don't match.");
    }

    @Test
    void testUpdateUserWithFile_Success() throws IOException {
        UpdateUserDto updateUserDto = UpdateUserDto.builder()
                .username("newUsername")
                .build();

        MultipartFile file = mock(MultipartFile.class);

        given(utilService.getCurrentUser()).willReturn(user1);
        given(fileUploadService.uploadUserImageFile(file)).willReturn("testImageUrl");

        UpdatedUserDto result = userService.updateUser(updateUserDto, file);

        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo(updateUserDto.getUsername());
        assertThat(result.getImageUrl()).isEqualTo("testImageUrl");
    }

    @Test
    void testUpdateUserWithoutFile_Success() throws IOException {
        UpdateUserDto updateUserDto = UpdateUserDto.builder()
                .username("newUsername")
                .build();

        given(utilService.getCurrentUser()).willReturn(user1);

        UpdatedUserDto result = userService.updateUser(updateUserDto, null);

        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo(updateUserDto.getUsername());
    }

    @Test
    void testUpdateUserRole_WhenARoleIsAdded_Success() {
        UserRoleDto userRoleDto = UserRoleDto.builder()
                .role("ROLE_ADMIN")
                .build();

        given(userRepository.findById(user1.getId())).willReturn(Optional.of(user1));
        given(roleRepository.findByName(ERole.ROLE_ADMIN)).willReturn(Optional.of(new Role()));

        UserDto result = userService.updateUserRole(userRoleDto, user1.getId(), true);

        assertThat(result).isNotNull();
        assertThat(result.getRoles().size()).isEqualTo(1);

        verify(userRepository, times(1)).findById(user1.getId());
        verify(roleRepository, times(1)).findByName(ERole.ROLE_ADMIN);
    }

    @Test
    void testUpdateUserRole_WhenARoleIsRemoved_Success() {
        Role adminRole = Role.builder()
                .users(Set.of(user1))
                .name(ERole.ROLE_ADMIN)
                .build();

        user1.getRoles().add(adminRole);

        UserRoleDto userRoleDto = UserRoleDto.builder()
                .role("ROLE_ADMIN")
                .build();

        given(userRepository.findById(user1.getId())).willReturn(Optional.of(user1));
        given(roleRepository.findByName(ERole.ROLE_ADMIN)).willReturn(Optional.of(adminRole));

        UserDto result = userService.updateUserRole(userRoleDto, user1.getId(), false);

        assertThat(result).isNotNull();
        assertThat(result.getRoles().size()).isEqualTo(0);

        verify(userRepository, times(1)).findById(user1.getId());
        verify(roleRepository, times(1)).findByName(ERole.ROLE_ADMIN);
    }

    @Test
    void testUpdateUserRole_WhenUserUserNotFound_ThrowsResourceNotFoundException() {
        UserRoleDto userRoleDto = UserRoleDto.builder()
                .role("ROLE_ADMIN")
                .build();

        given(userRepository.findById(user1.getId())).willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                userService.updateUserRole(userRoleDto, user1.getId(), true));

        verify(userRepository, times(1)).findById(user1.getId());
    }

    @Test
    void testDeleteUser_Success() {
        given(userRepository.findById(user1.getId())).willReturn(Optional.of(user1));

        MessageDto result = userService.deleteUser(user1.getId());

        assertThat(result).isNotNull();
        assertThat(result.getMessage()).isEqualTo("User deleted.");

        verify(userRepository, times(1)).findById(user1.getId());
    }

    @Test
    void testDeleteUser_WhenUserNotFound_ThrowsResourceNotFoundException() {
        given(userRepository.findById(user1.getId())).willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.deleteUser(user1.getId()));

        verify(userRepository, times(1)).findById(user1.getId());
    }
}
