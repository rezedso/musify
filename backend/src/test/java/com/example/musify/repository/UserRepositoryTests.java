package com.example.musify.repository;

import com.example.musify.entity.User;
import com.example.musify.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTests {
    @Autowired
    private UserRepository userRepository;

    private User user1;

    @BeforeEach
    void setup() {
        user1 = User.builder()
                .username("testuser1")
                .email("user1@test.com")
                .password("password")
                .build();
        userRepository.save(user1);

    }

    @Test
    void testSaveUser() {
        User newUser = User.builder()
                .username("testuser")
                .email("user@test.com")
                .password("password")
                .build();

        userRepository.save(newUser);

        User retrievedUser = userRepository.findById(newUser.getId()).orElseThrow();

        assertThat(retrievedUser).isNotNull();
        assertThat(retrievedUser.getUsername()).isEqualTo("testuser");
        assertThat(retrievedUser.getEmail()).isEqualTo("user@test.com");
    }

    @Test
    void testFindById_Success() {
        User retrievedUser = userRepository.findById(user1.getId()).orElseThrow();

        assertThat(retrievedUser).isNotNull();
        assertThat(retrievedUser.getUsername()).isEqualTo("testuser1");
        assertThat(retrievedUser.getEmail()).isEqualTo("user1@test.com");
    }

    @Test
    void testFindById_WhenUserNotFound_ThrowsResourceNotFoundException() {
        UUID nonExistentId = UUID.randomUUID();

        assertThrows(ResourceNotFoundException.class, () -> userRepository.findById(nonExistentId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found.")));
    }

    @Test
    void testFindByUsername() {
        Optional<User> retrievedUser = userRepository.findByUsername(user1.getUsername());

        assertThat(retrievedUser).isPresent();
    }

    @Test
    void testFindByUsername_WhenUserNotFound_ThrowsResourceNotFoundException() {
        assertThrows(ResourceNotFoundException.class, () -> userRepository.findByUsername("nonExistentUsername")
                .orElseThrow(() -> new ResourceNotFoundException("User not found.")));
    }

    @Test
    void testFindByEmail() {
        Optional<User> retrievedUser = userRepository.findByEmail(user1.getEmail());

        assertThat(retrievedUser).isPresent();
    }

    @Test
    void testFindByEmail_WhenUserNotFound_ThrowsResourceNotFoundException() {
        assertThrows(ResourceNotFoundException.class, () -> userRepository.findByEmail("nonExistentEmail")
                .orElseThrow(() -> new ResourceNotFoundException("User not found.")));
    }

    @Test
    void testDeleteUserById_Success() {
        long usersCount = userRepository.count();

        userRepository.deleteById(user1.getId());

        Optional<User> retrievedUser = userRepository.findById(user1.getId());

        assertThat(userRepository.count()).isEqualTo(usersCount - 1);
        assertThat(retrievedUser).isEmpty();
    }

    @Test
    void testDeleteUserById_WhenReviewNotFound_ThrowsResourceNotFoundException() {
        UUID nonExistentId = UUID.randomUUID();

        assertThrows(ResourceNotFoundException.class, () -> userRepository.findById(nonExistentId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found.")));
    }
}
