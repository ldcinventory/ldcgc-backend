package org.ldcgc.backend.util.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.ldcgc.backend.exception.RequestException;
import org.ldcgc.backend.util.compare.EnumMethods;
import org.ldcgc.backend.util.retrieving.Message;
import org.springframework.http.HttpStatus;

import java.util.Arrays;

import static org.ldcgc.backend.util.retrieving.Message.getErrorMessage;

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
                .orElseThrow(() ->new RequestException(HttpStatus.NOT_FOUND, getErrorMessage(Message.ErrorMessage.STATUS_NOT_FOUND)));
    }

    public static EStatus getStatusFromName(String name){
        return Arrays.stream(EStatus.values())
                .filter(status -> status.toString().equals(name))
                .findFirst()
                .orElseThrow(() ->new RequestException(HttpStatus.NOT_FOUND, getErrorMessage(Message.ErrorMessage.STATUS_NOT_FOUND)));
    }

}
