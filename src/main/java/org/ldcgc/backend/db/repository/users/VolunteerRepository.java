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

    @Query(value = """
            SELECT v.* FROM volunteers v
            WHERE unaccent(v.name) ILIKE unaccent(CONCAT('%', :filterString,'%'))
               OR unaccent(v.last_name) ILIKE unaccent(CONCAT('%', :filterString,'%'))
               OR unaccent(CONCAT(v.name, ' ', v.last_name)) ILIKE unaccent(:filterString)
            """, nativeQuery = true)
    Page<Volunteer> findAllFiltered(String filterString, Pageable pageable);

    boolean existsByBuilderAssistantId(String builderAssistantId);

    @Query("SELECT v FROM Volunteer v ORDER BY random() LIMIT 1")
    Volunteer getRandomVolunteer();
}
