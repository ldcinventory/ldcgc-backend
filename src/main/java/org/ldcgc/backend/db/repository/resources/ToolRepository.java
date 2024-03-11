package org.ldcgc.backend.db.repository.resources;

import org.ldcgc.backend.db.model.resources.Tool;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ToolRepository extends JpaRepository<Tool, Integer> {

    Optional<Tool> findFirstByBarcode(String barcode);

    @Query(value = """
            SELECT t.* FROM tools t
            JOIN categories cat on t.category_id = cat.id
            JOIN categories b on t.brand_id = b.id
            WHERE unaccent(cat.name) ILIKE unaccent(CONCAT('%', :category,'%'))
              AND unaccent(b.name) ILIKE unaccent(CONCAT('%', :brand,'%'))
              AND unaccent(t.name) ILIKE unaccent(CONCAT('%', :name,'%'))
              AND unaccent(t.model) ILIKE unaccent(CONCAT('%', :model,'%'))
              AND unaccent(t.description) ILIKE unaccent(CONCAT('%', :description,'%'))
              AND t.status = :statusId
            """, nativeQuery = true)
    Page<Tool> findAllFiltered(String category, String brand, String name, String model, String description, Integer statusId, Pageable pageable);

    @Query("SELECT t FROM Tool t ORDER BY random() LIMIT 1")
    Tool getRandomTool();

}
