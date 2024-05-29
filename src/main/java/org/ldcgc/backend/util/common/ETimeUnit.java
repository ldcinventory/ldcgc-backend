package org.ldcgc.backend.util.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.ldcgc.backend.exception.RequestException;
import org.ldcgc.backend.util.constants.Messages;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ETimeUnit implements EnumMethods {

    DAYS("días", 1),
    WEEKS("semanas", 2),
    MONTHS("meses", 3),
    YEARS("años", 4),
    HOURS("horas", 5),
    NEVER("nunca", 6);

    private final String name;
    private final Integer id;

    public static ETimeUnit getTimeUnitFromId(Integer id) {
        for (ETimeUnit timeUnit : ETimeUnit.values())
            if (timeUnit.getId().equals(id))
                return timeUnit;

        throw new RequestException(HttpStatus.NOT_FOUND, String.format(Messages.Error.TIME_UNIT_NOT_FOUND, id));
    }

    public static ETimeUnit getTimeUnitByName(String name) {
        for (ETimeUnit timeUnit : ETimeUnit.values())
            if (timeUnit.getName().equals(name))
                return timeUnit;

        throw new RequestException(HttpStatus.NOT_FOUND, String.format(Messages.Error.TIME_UNIT_NOT_FOUND, name));
    }

}
