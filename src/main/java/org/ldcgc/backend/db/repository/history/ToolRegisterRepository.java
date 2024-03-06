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
                LOWER(:status) LIKE '' OR
                LOWER(:status) LIKE ('opened') AND r.registerTo IS NULL OR
                LOWER(:status) LIKE ('closed') AND r.registerTo IS NOT NULL
            )
            AND (
                LOWER(CONCAT(v.name, ' ', v.lastName)) LIKE LOWER(CONCAT('%', :volunteer, '%')) OR
                (LOWER(v.builderAssistantId) LIKE LOWER(:volunteer) AND v.builderAssistantId <> '')
            )
            AND (
                LOWER(t.name) LIKE LOWER(CONCAT('%', :tool, '%')) OR
                (LOWER(t.barcode) LIKE LOWER(:tool) AND t.barcode <> '')
            )
            """)
    Page<ToolRegister> findAllFiltered(String status, String volunteer, String tool, Pageable pageable);

}
