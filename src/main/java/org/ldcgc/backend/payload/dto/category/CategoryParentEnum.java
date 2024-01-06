package org.ldcgc.backend.payload.dto.category;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CategoryParentEnum {

    MANUFACTURERS("Fabricantes", "Manufacturer"),
    RESPONSIBILITIES("Responsabilidades", "Responsibility"),
    RESOURCES("Recursos", "Resource"),
    BRANDS("Marcas", "Brand"),
    CATEGORIES("Categorías", "Category"),
    MAINTENANCE_TIME("Unidad periodo de mantenimiento", "Maintenance time");

    private final String bbddName;
    private final String name;
}
