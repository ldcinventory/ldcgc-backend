package org.ldcgc.backend.db.repository.resources;

import org.jetbrains.annotations.NotNull;
import org.ldcgc.backend.db.model.resources.Consumable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ConsumableRepository extends JpaRepository<Consumable, Integer> {

    @Query("""
            SELECT c FROM Consumable c
            WHERE LOWER(c.category.name) LIKE LOWER(CONCAT('%', :category,'%'))
            AND LOWER(c.brand) LIKE LOWER(CONCAT('%', :brand,'%'))
            AND LOWER(c.name) LIKE LOWER(CONCAT('%', :name,'%'))
            AND LOWER(c.model) LIKE LOWER(CONCAT('%', :model,'%'))
            AND LOWER(c.description) LIKE LOWER(CONCAT('%', :description,'%'))
            """)
    Page<Consumable> findAllFiltered(String category, String brand, String name, String model, String description, Pageable pageable);

    @NotNull Page<Consumable> findAll(@NotNull Pageable pageable);

    @NotNull Consumable getById(@NotNull Integer consumableId);

    void deleteById(@NotNull Integer consumableId);

    boolean existsByBarcode(String barcode);

    List<Consumable> findAllByBarcode(String barcode);

    Optional<Consumable> findByBarcode(String barcode);

    @Query("SELECT c FROM Consumable c ORDER BY random() LIMIT 1")
    Consumable getRandomConsumable();

}
