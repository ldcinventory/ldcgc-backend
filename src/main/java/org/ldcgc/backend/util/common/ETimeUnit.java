package org.ldcgc.backend.util.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.ldcgc.backend.exception.RequestException;
import org.ldcgc.backend.util.constants.Messages;
import org.springframework.http.HttpStatus;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum ETimeUnit implements EnumMethods {

    DAYS("días", 1),
    WEEKS("semanas", 2),
    MONTHS("meses", 3),
    YEARS("años", 4);

    private final String desc;
    private final Integer id;

    public static ETimeUnit getTimeUnitFromId(Integer id) {
        return Arrays.stream(ETimeUnit.values())
            .filter(status -> status.getId().equals(id))
            .findFirst()
            .orElseThrow(() -> new RequestException(HttpStatus.NOT_FOUND, String.format(Messages.Error.TIME_UNIT_NOT_FOUND, id)));
    }

    public static ETimeUnit getTimeUnitByName(String name) {
        return Arrays.stream(ETimeUnit.values())
            .filter(timeUnit -> timeUnit.getDesc().equals(name))
            .findFirst()
            .orElseThrow(() -> new RequestException(HttpStatus.NOT_FOUND, String.format(Messages.Error.TIME_UNIT_NOT_FOUND, name)));
    }

}
