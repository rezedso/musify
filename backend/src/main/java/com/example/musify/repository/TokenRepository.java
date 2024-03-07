package com.example.musify.repository;

import com.example.musify.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TokenRepository extends JpaRepository<Token, UUID> {
    @Query(value = """
            SELECT t FROM Token t INNER JOIN User u\s
            ON t.user.id = u.id\s
            WHERE u.id = :id AND (t.expired = false or t.revoked = false)\s
            """)
    List<Token> findAllValidTokensByUser(UUID id);

    @Query("SELECT t FROM Token t where t.token = :token")
    Optional<Token> findByToken(@Param("token") String token);
}
