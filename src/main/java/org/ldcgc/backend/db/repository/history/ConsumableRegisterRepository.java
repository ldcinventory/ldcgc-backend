package org.ldcgc.backend.db.repository.history;

import org.ldcgc.backend.db.model.history.ConsumableRegister;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface ConsumableRegisterRepository extends JpaRepository<ConsumableRegister, Integer> {

    List<ConsumableRegister> findAllByConsumable_Barcode(String barcode);

    @Query("""
            SELECT c FROM ConsumableRegister c
            WHERE c.volunteer.builderAssistantId LIKE CONCAT('%', :builderAssistantId,'%')
               OR c.consumable.barcode LIKE CONCAT('%', :consumableBarcode,'%')
               OR c.registrationIn >= :dateFrom
               OR c.registrationOut <= :dateTo
            """)
    Page<ConsumableRegister> findAllFiltered(String builderAssistantId, String consumableBarcode, LocalDateTime dateFrom, LocalDateTime dateTo, Pageable pageable);

}
