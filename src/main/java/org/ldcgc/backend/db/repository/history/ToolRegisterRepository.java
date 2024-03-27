package org.ldcgc.backend.db.repository.history;

import org.ldcgc.backend.db.model.history.ToolRegister;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ToolRegisterRepository extends JpaRepository<ToolRegister, Integer> {

    @Query("""
        SELECT r FROM ToolRegister r
            JOIN r.volunteer v
            JOIN r.tool t
            WHERE
            (
                COALESCE(:status, '') = '' OR
                (COALESCE(:status, '') ILIKE 'opened' AND r.registerTo IS NULL) OR
                (COALESCE(:status, '') ILIKE 'closed'  AND r.registerTo IS NOT NULL)
            )
            AND (
                COALESCE(:volunteer, '') = '' OR
                unaccent(CONCAT(v.name, ' ', v.lastName)) ILIKE unaccent(CONCAT('%', :volunteer, '%')) OR
                v.builderAssistantId ILIKE :volunteer
            )
            AND (
                COALESCE(:tool, '') = '' OR
                unaccent(t.name) ILIKE unaccent(CONCAT('%', :tool, '%')) OR
                t.barcode ILIKE :tool
            )
            """)
    Page<ToolRegister> findAllFiltered(String status, String volunteer, String tool, Pageable pageable);

}
