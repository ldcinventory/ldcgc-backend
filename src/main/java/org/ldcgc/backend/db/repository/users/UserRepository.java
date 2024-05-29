package org.ldcgc.backend.db.repository.users;

import org.jetbrains.annotations.NotNull;
import org.ldcgc.backend.db.model.users.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    boolean existsById(@NotNull Integer id);

    void deleteById(@NotNull Integer id);

    Optional<User> findByEmail(String email);

    @Query(value = """
            SELECT u.* FROM users u
            WHERE LOWER(u.email) LIKE LOWER(CONCAT('%', :filterString,'%'))
            AND (
                  CASE WHEN :isEnabled IS NOT NULL THEN
                    CASE
                        WHEN :isEnabled = TRUE THEN u.enabled = TRUE
                        ELSE u.enabled = FALSE
                    END
                  ELSE TRUE
                  END
              )
            """, nativeQuery = true)
    Page<User> findAllFiltered(String filterString, Boolean isEnabled, Pageable pageable);

    Optional<User> findByVolunteer_Id(Integer id);

    Optional<User> findByVolunteer_BuilderAssistantId(String barcode);

    @Query(value = "SELECT u.enabled FROM User u WHERE u.id = :id")
    boolean userIsEnabled(Integer id);
}
