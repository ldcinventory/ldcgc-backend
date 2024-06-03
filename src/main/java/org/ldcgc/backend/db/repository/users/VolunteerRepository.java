package org.ldcgc.backend.db.repository.users;

import org.ldcgc.backend.db.model.users.Volunteer;
import org.ldcgc.backend.util.common.EVStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface VolunteerRepository extends JpaRepository<Volunteer, Integer>, JpaSpecificationExecutor<Volunteer> {

    @Query("""
            SELECT v FROM Volunteer v
            WHERE v.builderAssistantId = :builderAssistantId
              AND v.status != 'DELETED' AND v.status != 'LOCKED'
            """)
    Optional<Volunteer> findByBuilderAssistantId(String builderAssistantId);

    @Query("""
            SELECT v FROM Volunteer v
            WHERE (:builderAssistantIds IS NULL OR v.builderAssistantId in (:builderAssistantIds))
              AND v.status != 'DELETED' AND v.status != 'LOCKED'
            """)
    List<Volunteer> findAllByBuilderAssistantIdIn(List<String> builderAssistantIds);

    @Query("SELECT v FROM Volunteer v WHERE v.status != 'DELETED' AND v.status != 'LOCKED'")
    Page<Volunteer> findAll(Pageable pageable);

    @Query(value = """
            SELECT v.* FROM volunteers v
            WHERE (unaccent(v.name) ILIKE unaccent(CONCAT('%', :filterString, '%'))
               OR  unaccent(v.last_name) ILIKE unaccent(CONCAT('%', :filterString, '%'))
               OR  unaccent(CONCAT(v.name, ' ', v.last_name)) ILIKE unaccent(CONCAT('%', :filterString, '%')))
            AND (
                  CASE WHEN :status IS NOT NULL
                        AND :status != 'DELETED'
                        AND :status != 'LOCKED' THEN v.status = :status
                  ELSE v.status != 'DELETED' AND v.status != 'LOCKED'
                  END
              )
            """, nativeQuery = true)
    Page<Volunteer> findAllFiltered(String filterString, String status, Pageable pageable);

    @Query("""
            SELECT count(v) > 0 FROM Volunteer v
            WHERE v.builderAssistantId = :builderAssistantId
              AND v.status != 'DELETED' AND v.status != 'LOCKED'
            """)
    boolean existsByBuilderAssistantId(String builderAssistantId);

    @Query("""
            SELECT v FROM Volunteer v
            WHERE v.status != 'DELETED'
              AND v.status != 'LOCKED'
            ORDER BY random() LIMIT 1
            """)
    Volunteer getRandomVolunteer();
}
