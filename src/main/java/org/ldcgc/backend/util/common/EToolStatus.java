package org.ldcgc.backend.util.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.ldcgc.backend.exception.RequestException;
import org.ldcgc.backend.util.constants.Messages;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum EToolStatus implements EnumMethods {

    AVAILABLE("Disponible", 0),
    NOT_AVAILABLE("No disponible", 1),
    IN_MAINTENANCE("En mantenimiento", 2),
    DAMAGED("Dañado", 3),
    NEW("Nueva", 4),
    DEPRECATED("En desuso", 5),
    UNKNOWN("Desconocido", 99);

    private final String desc;
    private final Integer id;

    public static EToolStatus getStatusFromId(Integer id) {
        for (EToolStatus status : EToolStatus.values())
            if (status.getId().equals(id))
                return status;

        throw new RequestException(HttpStatus.NOT_FOUND, String.format(Messages.Error.STATUS_NOT_FOUND, id));
    }

    public static EToolStatus getStatusByName(String name) {
        for (EToolStatus status : EToolStatus.values())
            if (status.getDesc().equalsIgnoreCase(name) || status.name().equalsIgnoreCase(name))
                return status;

        throw new RequestException(HttpStatus.NOT_FOUND, String.format(Messages.Error.STATUS_NOT_FOUND, name));
    }
}
