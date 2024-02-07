package org.ldcgc.backend.payload.dto.category;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CategoryParentEnum {
    RESPONSIBILITIES("Responsabilidades", "Responsibility"),
    RESOURCES("Recursos", "Resource"),
    BRANDS("Marcas", "Brand"),
    MANUFACTURERS("Fabricantes", "Manufacturer"),
    CATEGORIES("Categorías", "Category"),
    STOCKTYPE("TipoStock", "StockType"),
    MAINTENANCE_TIME("Unidad periodo de mantenimiento", "Maintenance_Time");

    private final String bbddName;
    private final String name;
}
