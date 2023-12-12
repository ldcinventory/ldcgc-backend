package org.ldcgc.backend.db.repository.users;

import org.ldcgc.backend.db.model.users.Volunteer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface VolunteerRepository extends JpaRepository<Volunteer, Integer> {

    Optional<Volunteer> findByBuilderAssistantId(String builderAssistantId);

    @Query("""
            SELECT v FROM Volunteer v
            WHERE LOWER(v.name) LIKE LOWER(CONCAT('%', :filterString,'%'))
              AND LOWER(v.lastName) LIKE LOWER(CONCAT('%', :filterString,'%'))
            """)
    Page<Volunteer> findAllFiltered(String filterString, Pageable pageable);

}
