package org.ldcgc.backend.db.repository.users;

import org.ldcgc.backend.db.model.users.Volunteer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface VolunteerRepository extends JpaRepository<Volunteer, Integer>, JpaSpecificationExecutor<Volunteer> {

    Optional<Volunteer> findByBuilderAssistantId(String builderAssistantId);
    List<Volunteer> findAllByBuilderAssistantId(String builderAssistantId);

    @Query("""
            SELECT v FROM Volunteer v
            WHERE LOWER(v.name) LIKE LOWER(CONCAT('%', :filterString,'%'))
               OR LOWER(v.lastName) LIKE LOWER(CONCAT('%', :filterString,'%'))
            """)
    Page<Volunteer> findAllFiltered(String filterString, Pageable pageable);

    @Query("""
            SELECT v FROM Volunteer v
            WHERE LOWER(v.name) LIKE LOWER(CONCAT('%', :name,'%'))
              AND LOWER(v.lastName) LIKE LOWER(CONCAT('%', :lastName,'%'))
            """)
    List<Volunteer> findAllByNameAndLastName(String name, String lastName);

<<<<<<< HEAD
    Optional<Volunteer> findTopByIdNotNull();
=======
    boolean existsByBuilderAssistantId(String builderAssistantId);

    @Query("SELECT v FROM Volunteer v ORDER BY random() LIMIT 1")
    Volunteer getRandomvolunteer();
>>>>>>> develop
}
