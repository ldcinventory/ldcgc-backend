package org.ldcgc.backend.category;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EStatus {

    AVAILABLE("Disponible"),
    NOT_AVAILABLE("No disponible"),
    IN_MAINTENANCE("En mantenimiento"),
    NEW("Nueva"),
    DEPRECATED("En desuso");

    private final String desc;

    public boolean equalsAny(EStatus... statuses) {
        for (EStatus status : statuses)
            if (this == status) return true;
        return false;
    }

}
