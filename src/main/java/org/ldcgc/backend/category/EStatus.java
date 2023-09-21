package org.ldcgc.backend.category;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EStatus implements EnumMethods {

    AVAILABLE("Disponible"),
    NOT_AVAILABLE("No disponible"),
    IN_MAINTENANCE("En mantenimiento"),
    NEW("Nueva"),
    DEPRECATED("En desuso");

    private final String desc;

}
