package com.example.musify.service.impl;

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
import com.example.musify.service.IFileUploadService;
import com.example.musify.service.IUserService;
import com.example.musify.service.IUtilService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {
    private final UserRepository userRepository;
    private final AlbumListRepository albumListRepository;
    private final RoleRepository roleRepository;
    private final IFileUploadService fileUploadService;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final IUtilService utilService;

    @Override
    public UserDto getUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        return modelMapper.map(user, UserDto.class);
    }

    @Override
    public List<UserDto> getUsers() {
        List<User> users = userRepository.findAll();

        return users.stream().map(user -> modelMapper.map(user, UserDto.class)).toList();
    }

    @Override
    public MessageDto updatePassword(UpdatePasswordDto request) {
        var user = utilService.getCurrentUser();

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalStateException("Wrong password.");
        }
        if (!request.getNewPassword().equals(request.getConfirmationPassword())) {
            throw new IllegalStateException("Passwords don't match.");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        return new MessageDto("Password updated.");
    }

    @Override
    public UpdatedUserDto updateUser(UpdateUserDto updateUserDto, MultipartFile file) throws IOException {
        var user = utilService.getCurrentUser();

        if (updateUserDto.getUsername() != null &&
                !Objects.equals(user.getUsername(), updateUserDto.getUsername())) {
            user.setUsername(updateUserDto.getUsername());
        }

        if (file != null) {
            String imageUrl = fileUploadService.uploadUserImageFile(file);
            user.setImageUrl(imageUrl);
        }

        userRepository.save(user);
        return new UpdatedUserDto(user.getUsername(), user.getEmail(), user.getImageUrl());
    }

    @Override
    @Transactional
    public UserDto updateUserRole(UserRoleDto userRoleDto, UUID userId, boolean addRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        ERole role = ERole.valueOf(userRoleDto.getRole());

        Optional<Role> existingRoleOpt = roleRepository.findByName(role);
        Role existingRole = existingRoleOpt.orElseGet(() -> {
            Role newRole = new Role();
            newRole.setName(role);
            return roleRepository.save(newRole);
        });

        if (addRole) {
            user.getRoles().add(existingRole);
        } else {
            user.getRoles().remove(existingRole);
        }

        userRepository.save(user);

        return modelMapper.map(user, UserDto.class);
    }

    @Override
    @Transactional
    public MessageDto deleteUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        albumListRepository.deleteAllAlbumListsByUser(user);
        userRepository.deleteById(userId);

        return new MessageDto("User deleted.");
    }
}
