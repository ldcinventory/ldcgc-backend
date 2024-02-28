package org.ldcgc.backend.util.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.ldcgc.backend.exception.RequestException;
import org.ldcgc.backend.util.compare.EnumMethods;
import org.ldcgc.backend.util.constants.Messages;
import org.springframework.http.HttpStatus;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum EStockType implements EnumMethods {

    UNITS("unidades", "ud", 0),

    LITERS("litros", "l", 1),
    MILLILITERS("mililitros", "ml", 2),

    KILOGRAMS("kilogramos", "kg", 3),
    GRAMS("gramos", "g", 4),

    METERS("metros", "m", 5),
    CENTIMETERS("centímetros", "cm", 6),
    MILLIMETERS("milímetros", "mm", 7),

    POUNDS("libras", "lb", 8),
    OUNCES("onzas", "oz", 9);

    private final String desc;
    private final String abbr;
    private final Integer id;

    public static EStockType getStockTypeFromId(Integer id) {
        return Arrays.stream(EStockType.values())
            .filter(status -> status.getId().equals(id))
            .findFirst()
            .orElseThrow(() -> new RequestException(HttpStatus.NOT_FOUND, String.format(Messages.Error.STOCK_TYPE_NOT_FOUND, id)));
    }

    public static EStockType getStockTypeByName(String name) {
        return Arrays.stream(EStockType.values())
            .filter(timeUnit -> timeUnit.getDesc().equals(name))
            .findFirst()
            .orElseThrow(() -> new RequestException(HttpStatus.NOT_FOUND, String.format(Messages.Error.STOCK_TYPE_NOT_FOUND, name)));
    }
}
