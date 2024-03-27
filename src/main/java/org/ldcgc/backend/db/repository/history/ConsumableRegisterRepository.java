package org.ldcgc.backend.db.repository.history;

import org.ldcgc.backend.db.model.history.ConsumableRegister;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface ConsumableRegisterRepository extends JpaRepository<ConsumableRegister, Integer>, JpaSpecificationExecutor<ConsumableRegister> {

    List<ConsumableRegister> findAllByConsumable_Barcode(String barcode);

    @Query("""
        SELECT cr FROM ConsumableRegister cr
            JOIN cr.volunteer v
            JOIN cr.consumable c
            WHERE
            (
                COALESCE(:status, '') = '' OR
                (COALESCE(:status, '') ILIKE 'opened' AND cr.registerTo IS NULL) OR
                (COALESCE(:status, '') ILIKE 'closed'  AND cr.registerTo IS NOT NULL)
            )
            AND (
                COALESCE(:volunteer, '') = '' OR
                unaccent(CONCAT(v.name, ' ', v.lastName)) ILIKE unaccent(CONCAT('%', :volunteer, '%')) OR
                v.builderAssistantId ILIKE :volunteer
            )
            AND (
                COALESCE(:consumable, '') = '' OR
                unaccent(c.name) ILIKE unaccent(CONCAT('%', :consumable, '%')) OR
                c.barcode ILIKE :consumable
            )
            AND (
                COALESCE(:dateFrom, '') = '' OR
                cr.registerFrom >= :dateFrom
            )
            AND (
                COALESCE(:dateTo, '') = '' OR
                cr.registerTo <= :dateTo
            )
            """)
    Page<ConsumableRegister> findAllFiltered(String status, String volunteer, String consumable, LocalDateTime dateFrom, LocalDateTime dateTo, Pageable pageable);

}
