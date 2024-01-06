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

    @Query("SELECT u FROM User u WHERE LOWER(u.email) LIKE LOWER(CONCAT('%', :filterString,'%'))")
    Page<User> findAllFiltered(String filterString, Pageable pageable);

    Optional<User> findByVolunteer_Id(Integer id);
}
