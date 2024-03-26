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

    @Query(value = """
            SELECT c.* FROM consumables c
            JOIN categories cat on c.category_id = cat.id
            JOIN categories b on c.brand_id = b.id
            WHERE unaccent(cat.name) ILIKE unaccent(CONCAT('%', :category, '%'))
              AND unaccent(b.name) ILIKE unaccent(CONCAT('%', :brand, '%'))
              AND unaccent(c.name) ILIKE unaccent(CONCAT('%', :name, '%'))
              AND unaccent(c.model) ILIKE unaccent(CONCAT('%', :model, '%'))
              AND unaccent(c.description) ILIKE unaccent(CONCAT('%', :description, '%'))
            """, nativeQuery = true)
    Page<Consumable> findAllFiltered(String category, String brand, String name, String model, String description, Pageable pageable);

    @Query(value = """
            SELECT c.* FROM consumables c
            JOIN categories cat on c.category_id = cat.id
            JOIN categories b on c.brand_id = b.id
            WHERE unaccent(cat.name) ILIKE unaccent(CONCAT('%', :filterString, '%'))
              OR unaccent(b.name) ILIKE unaccent(CONCAT('%', :filterString, '%'))
              OR unaccent(c.name) ILIKE unaccent(CONCAT('%', :filterString, '%'))
              OR unaccent(c.model) ILIKE unaccent(CONCAT('%', :filterString, '%'))
              OR unaccent(c.description) ILIKE unaccent(CONCAT('%', :filterString, '%'))
            """, nativeQuery = true)
    Page<Consumable> findAllFiltered(String filterString, Pageable pageable);

    @NotNull Page<Consumable> findAll(@NotNull Pageable pageable);

    @NotNull Consumable getById(@NotNull Integer consumableId);

    void deleteById(@NotNull Integer consumableId);

    boolean existsByBarcode(String barcode);

    List<Consumable> findAllByBarcode(String barcode);

    Optional<Consumable> findByBarcode(String barcode);

    @Query("SELECT c FROM Consumable c ORDER BY random() LIMIT 1")
    Consumable getRandomConsumable();

}
