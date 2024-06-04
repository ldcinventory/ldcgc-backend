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

    boolean existsByBarcode(String barcode);

    @Query(value = """
            SELECT t.* FROM tools t
            JOIN "resource-types" r on t.resource_type_id = r.id
            JOIN brands b on t.brand_id = b.id
            JOIN locations l on t.location_id = l.id
            WHERE unaccent(r.name) ILIKE unaccent(CONCAT('%', :resourceType, '%'))
              AND unaccent(b.name) ILIKE unaccent(CONCAT('%', :brand, '%'))
              AND unaccent(t.name) ILIKE unaccent(CONCAT('%', :name, '%'))
              AND unaccent(t.model) ILIKE unaccent(CONCAT('%', :model, '%'))
              AND unaccent(t.description) ILIKE unaccent(CONCAT('%', :description, '%'))
              AND unaccent(t.barcode) ILIKE unaccent(CONCAT('%', :barcode, '%'))
              AND unaccent(l.name) ILIKE unaccent(CONCAT('%', :location, '%'))
              AND (:statusId IS NULL OR t.status = :statusId)
            """, nativeQuery = true)
    Page<Tool> findAllFiltered(String resourceType, String brand, String name, String model, String description, String barcode, String location, Integer statusId, Pageable pageable);
    @Query(value = """
            SELECT t.* FROM tools t
            JOIN "resource-types" r on t.resource_type_id = r.id
            JOIN brands b on t.brand_id = b.id
            WHERE (unaccent(r.name) ILIKE unaccent(CONCAT('%', :filterString, '%'))
              OR unaccent(b.name) ILIKE unaccent(CONCAT('%', :filterString, '%'))
              OR unaccent(t.name) ILIKE unaccent(CONCAT('%', :filterString, '%'))
              OR unaccent(t.model) ILIKE unaccent(CONCAT('%', :filterString, '%'))
              OR unaccent(t.description) ILIKE unaccent(CONCAT('%', :filterString, '%'))
              OR t.barcode = :filterString)
              AND (
                CASE
                    WHEN :statusId IS NOT NULL THEN t.status = :statusId
                    ELSE TRUE
                END
              )
            """, nativeQuery = true)
    Page<Tool> findAllFiltered(String filterString, Integer statusId, Pageable pageable);

    @Query("SELECT t FROM Tool t ORDER BY random() LIMIT 1")
    Tool getRandomTool();

    List<Tool> findAllByBarcodeIn(List<String> barcodes);

    @Query(value = "SELECT count(t) > 0 FROM tools t WHERE t.brand_id = :brandId", nativeQuery = true)
    boolean brandUsed(Integer brandId);

}
