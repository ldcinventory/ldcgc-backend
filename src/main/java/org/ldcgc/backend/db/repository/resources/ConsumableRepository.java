package org.ldcgc.backend.db.repository.resources;

import org.jetbrains.annotations.NotNull;
import org.ldcgc.backend.db.model.resources.Consumable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ConsumableRepository extends JpaRepository<Consumable, Integer> {

    Page<Consumable> findByNameContainingOrDescriptionContaining(String likeNamePattern, String likeDescriptionPattern, Pageable pageable);

    @NotNull Page<Consumable> findAll(@NotNull Pageable pageable);

    @NotNull Consumable getById(@NotNull Integer consumableId);

    void deleteById(@NotNull Integer consumableId);

    List<Consumable> findByBarcodeIn(List<String> barcodes);

    boolean existsByBarcode(String barcode);

    List<Consumable> findAllByBarcode(String barcode);

    Optional<Consumable> findByBarcode(String barcode);

}
