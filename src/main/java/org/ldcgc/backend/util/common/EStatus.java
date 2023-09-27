package org.ldcgc.backend.util.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.ldcgc.backend.util.compare.EnumMethods;

@Getter
@RequiredArgsConstructor
public enum EStatus implements EnumMethods {

    AVAILABLE("Disponible"),
    NOT_AVAILABLE("No disponible"),
    IN_MAINTENANCE("En mantenimiento"),
    DAMAGED("Dañado"),
    NEW("Nueva"),
    DEPRECATED("En desuso");

    private final String desc;

}
