package org.ldcgc.backend.db.repository.users;

import org.ldcgc.backend.db.model.users.Token;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TokenRepository extends JpaRepository<Token, UUID> {

    //@Query("SELECT t.publicKey FROM Token t WHERE t.publicKey = :publicKey")
    //Optional<Integer> getUserIdFromPublicKey(String publicKey);

}
