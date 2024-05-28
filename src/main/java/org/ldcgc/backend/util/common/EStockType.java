package org.ldcgc.backend.util.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.ldcgc.backend.exception.RequestException;
import org.ldcgc.backend.util.constants.Messages;
import org.springframework.http.HttpStatus;

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
    OUNCES("onzas", "oz", 9),

    UNKNOWN("desconocido", "un", 999);

    private final String desc;
    private final String abbr;
    private final Integer id;

    public static EStockType getStockTypeFromId(Integer id) {
        for (EStockType stockType : EStockType.values())
            if (stockType.getId().equals(id))
                return stockType;

       throw new RequestException(HttpStatus.NOT_FOUND, String.format(Messages.Error.STOCK_TYPE_NOT_FOUND, id));
    }

    public static EStockType getStockTypeByDesc(String desc) {
        for (EStockType stockType : EStockType.values())
            if (stockType.getDesc().equals(desc))
                return stockType;

        throw new RequestException(HttpStatus.NOT_FOUND, String.format(Messages.Error.STOCK_TYPE_NOT_FOUND, desc));
    }
}
