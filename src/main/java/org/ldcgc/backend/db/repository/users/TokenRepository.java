package org.ldcgc.backend.db.repository.users;

import jakarta.transaction.Transactional;
import org.ldcgc.backend.db.model.users.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface TokenRepository extends JpaRepository<Token, UUID> {

    Optional<Token> findByJwtID(String jwtID);

    @Modifying
    @Transactional
    @Query("DELETE from Token t WHERE t.userId = :userId and t.expiresAt < current_timestamp")
    void deleteAllExpiredTokensFromUser(Integer userId);

    @Modifying
    @Transactional
    @Query("DELETE from Token t WHERE t.userId = :userId")
    void deleteAllTokensFromUser(Integer userId);

    @Modifying
    @Transactional
    @Query("DELETE from Token t WHERE t.expiresAt < current_timestamp")
    void deleteExpiredTokens();

    @Query("SELECT t.userId FROM Token t WHERE t.jwtID = :jwtID")
    Optional<Integer> getUserIdFromJwtId(String jwtID);
}
