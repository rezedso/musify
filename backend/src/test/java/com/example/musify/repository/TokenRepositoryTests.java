package com.example.musify.repository;

import com.example.musify.entity.Token;
import com.example.musify.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class TokenRepositoryTests {
    @Autowired
    private TokenRepository tokenRepository;
    @Autowired
    private UserRepository userRepository;

    private User user1;

    @BeforeEach
    void setup() {
        user1 = User.builder()
                .username("testuser")
                .email("user@test.com")
                .password("password")
                .build();

        userRepository.save(user1);
    }

    @Test
    void testSaveToken() {
        Token newToken = Token.builder()
                .token(UUID.randomUUID().toString())
                .expired(false)
                .revoked(false)
                .user(user1)
                .build();

        tokenRepository.save(newToken);

        Optional<Token> retrievedToken = tokenRepository.findById(newToken.getId());

        assertThat(retrievedToken).isPresent();

        Token token = retrievedToken.get();

        assertThat(token).isNotNull();
        assertThat(token.isExpired()).isFalse();
        assertThat(token.isRevoked()).isFalse();
        assertThat(token.getUser()).isEqualTo(user1);
    }

    @Test
    void testSaveAllTokens() {
        long tokensCountBeforeSave = tokenRepository.count();

        List<Token> tokensToSave = List.of(
                Token.builder().token(UUID.randomUUID().toString()).expired(false).revoked(false).user(user1).build(),
                Token.builder().token(UUID.randomUUID().toString()).expired(false).revoked(false).user(user1).build()
        );

        tokenRepository.saveAll(tokensToSave);

        List<Token> savedTokens = tokenRepository.findAll();

        assertThat(savedTokens).isNotNull();
        assertThat(savedTokens.size()).isEqualTo(tokensCountBeforeSave + 2);
    }

    @Test
    void testFindByToken() {
        Token savedToken = Token.builder()
                .token(UUID.randomUUID().toString())
                .expired(false)
                .revoked(false)
                .user(user1)
                .build();

        tokenRepository.save(savedToken);

        Optional<Token> optionalToken = tokenRepository.findByToken(savedToken.getToken());

        assertThat(optionalToken).isPresent();

        Token token = optionalToken.get();

        assertThat(token).isNotNull();
        assertThat(token.user).isEqualTo(user1);
        assertThat(token.isExpired()).isFalse();
        assertThat(token.isRevoked()).isFalse();
    }

    @Test
    void testFindAllValidTokensByUser() {
        List<Token> tokens = List.of(
                Token.builder().token(UUID.randomUUID().toString()).expired(false).revoked(false).user(user1).build(),
                Token.builder().token(UUID.randomUUID().toString()).expired(false).revoked(false).user(user1).build(),
                Token.builder().token(UUID.randomUUID().toString()).expired(true).revoked(true).user(user1).build()
        );

        tokenRepository.saveAll(tokens);

        List<Token> userTokens = tokenRepository.findAllValidTokensByUser(user1.getId());

        assertThat(userTokens).isNotNull();
        assertThat(userTokens.size()).isEqualTo(2);
    }
}
