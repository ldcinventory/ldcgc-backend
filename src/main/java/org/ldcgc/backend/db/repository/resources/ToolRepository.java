package org.ldcgc.backend.db.repository.resources;

import org.ldcgc.backend.db.model.resources.Tool;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ToolRepository extends JpaRepository<Tool, Integer> {

    Optional<Tool> findFirstByBarcode(String barcode);

    List<Tool> findByBarcodeIn(List<String> barcodes);

    @Query("""
            SELECT t FROM Tool t
            WHERE LOWER(t.brand.name) LIKE LOWER(CONCAT('%', :brand,'%'))
            AND LOWER(t.model) LIKE LOWER(CONCAT('%', :model,'%'))
            AND LOWER(t.description) LIKE LOWER(CONCAT('%', :description,'%'))
            AND (:statusId IS NULL OR t.status = :statusId)
            """)
    Page<Tool> findAllFiltered(String brand, String model, String description, Integer statusId, Pageable pageable);


}
