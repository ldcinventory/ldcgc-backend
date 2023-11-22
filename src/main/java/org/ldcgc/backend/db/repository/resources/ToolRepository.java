package org.ldcgc.backend.db.repository.resources;

import org.ldcgc.backend.db.model.resources.Tool;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ToolRepository extends JpaRepository<Tool, Integer> {

    Optional<Tool> findFirstByBarcode(String barcode);

    List<Tool> findAllByBrand_Name(String brand);

    List<Tool> findByBarcodeIn(List<String> barcodes);
}
