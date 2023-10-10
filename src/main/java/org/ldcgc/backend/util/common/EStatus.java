package org.ldcgc.backend.util.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.ldcgc.backend.util.compare.EnumMethods;

@Getter
@RequiredArgsConstructor
public enum EStatus implements EnumMethods {

    AVAILABLE("Disponible", 0),
    NOT_AVAILABLE("No disponible", 1),
    IN_MAINTENANCE("En mantenimiento", 2),
    DAMAGED("Dañado", 3),
    NEW("Nueva", 4),
    DEPRECATED("En desuso", 5);

    private final String desc;
    private final Integer id;

    public static EStatus getStatusFromId(Integer id) {
        for(EStatus status : EStatus.values())
            if(status.getId().equals(id))
                return status;

        return null;
    }

}
