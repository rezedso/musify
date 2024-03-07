package com.example.musify.auth.service;

import com.example.musify.auth.dto.request.UserLoginDto;
import com.example.musify.auth.dto.request.UserRegisterDto;
import com.example.musify.auth.dto.response.LoginDto;
import com.example.musify.auth.dto.response.MessageDto;
import com.example.musify.auth.dto.response.TokenRefreshDto;
import com.example.musify.entity.Role;
import com.example.musify.entity.Token;
import com.example.musify.entity.User;
import com.example.musify.enumeration.ERole;
import com.example.musify.exception.TokenRefreshException;
import com.example.musify.exception.UserAlreadyExistsException;
import com.example.musify.repository.RoleRepository;
import com.example.musify.repository.TokenRepository;
import com.example.musify.repository.UserRepository;
import com.example.musify.service.IFileUploadService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final IFileUploadService fileUploadService;

    public MessageDto register(UserRegisterDto userRegisterDto, MultipartFile file) throws IOException {
        validateUser(userRegisterDto);
        User user = createUser(userRegisterDto, file);
        Set<Role> roles = assignUserRoles(user);

        user.setRoles(roles);
        userRepository.save(user);

        return new MessageDto("Successfully registered!");
    }

    public LoginDto login(UserLoginDto request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        var user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        var jwt = jwtService.generateRefreshToken(userDetails);
        var refreshToken = jwtService.generateRefreshToken(userDetails);

        revokeAllUserTokens(user);
        saveUserToken(user, jwt);

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return LoginDto.builder()
                .id(userDetails.getId())
                .email(userDetails.getEmail())
                .roles(roles)
                .username(user.getUsername())
                .imageUrl(user.getImageUrl())
                .accessToken(jwt)
                .refreshToken(refreshToken)
                .build();
    }

    public TokenRefreshDto refreshToken(HttpServletRequest request, HttpServletResponse response) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }

        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractUsername(refreshToken);

        if (userEmail != null) {
            var user = userRepository.findByEmail(userEmail).orElseThrow();
            UserDetails userDetails = UserDetailsImpl.build(user);
            if (jwtService.isTokenValid(refreshToken, userDetails)) {
                var accessToken = jwtService.generateToken(userDetails);
                revokeAllUserTokens(user);
                saveUserToken(user, accessToken);
                return new TokenRefreshDto(accessToken, refreshToken);
            }
        }
        throw new TokenRefreshException(refreshToken, "Refresh token is not in database");
    }

    private Set<Role> assignUserRoles(User user) {
        Set<Role> roles = new HashSet<>();

        if (user.getUsername().equals("reze")) {
            roles.add(createRoleIfNotExists(ERole.ROLE_ADMIN));
        }

        roles.add(createRoleIfNotExists(ERole.ROLE_USER));
        return roles;
    }

    private Role createRoleIfNotExists(ERole name) {
        Optional<Role> existingRole = roleRepository.findByName(name);
        if (existingRole.isPresent()) {
            return existingRole.get();
        } else {
            Role newRole = new Role();
            newRole.setName(name);
            return roleRepository.save(newRole);
        }
    }

    private User createUser(UserRegisterDto userRegisterDto, MultipartFile file) throws IOException {

        User user = User.builder()
                .username(userRegisterDto.getUsername())
                .email(userRegisterDto.getEmail())
                .password(passwordEncoder.encode(userRegisterDto.getPassword()))
                .build();

        if (file != null) {
            String imageUrl = fileUploadService.uploadUserImageFile(file);
            user.setImageUrl(imageUrl);
        }
        return user;
    }

    private void validateUser(UserRegisterDto userRegisterDto) {
        if (userRepository.existsByUsername(userRegisterDto.getUsername())) {
            throw new UserAlreadyExistsException("Username already exists.");
        }

        if (userRepository.existsByEmail(userRegisterDto.getEmail())) {
            throw new UserAlreadyExistsException("Email is already in use.");
        }
    }

    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokensByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }
}
