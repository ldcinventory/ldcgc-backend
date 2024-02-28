package org.ldcgc.backend.util.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.ldcgc.backend.exception.RequestException;
import org.ldcgc.backend.util.constants.Messages;
import org.springframework.http.HttpStatus;

import java.util.Arrays;

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
        return Arrays.stream(EStatus.values())
                .filter(status -> status.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new RequestException(HttpStatus.NOT_FOUND, String.format(Messages.Error.STATUS_NOT_FOUND, id)));
    }

    public static EStatus getStatusByName(String name){
        return Arrays.stream(EStatus.values())
                .filter(status -> status.getDesc().equals(name))
                .findFirst()
                .orElseThrow(() -> new RequestException(HttpStatus.NOT_FOUND, String.format(Messages.Error.STATUS_NOT_FOUND, name)));
    }
}
