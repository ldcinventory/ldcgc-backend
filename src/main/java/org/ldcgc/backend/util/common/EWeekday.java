package org.ldcgc.backend.util.common;

import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Map;

@Getter
@RequiredArgsConstructor
public enum EWeekday implements EnumMethods {

    MONDAY ("lunes", "L"),
    TUESDAY ("martes", "M"),
    WEDNESDAY ("miércoles", "X"),
    THURSDAY ("jueves", "J"),
    FRIDAY ("viernes", "V"),
    SATURDAY ("sábado", "S"),
    SUNDAY ("domingo", "D"),
    HOLIDAY ("festivo", "F");

    private final String desc;
    private final String abbrv;

    // instantiate a global Map to access a Weekday from its own abbreviation
    // the Map is like this: {'L', MONDAY}, {'M', TUESDAY}, {'X', WEDNESDAY}, {'J', THURSDAY} ...
    public static final Map<String, EWeekday> abbrvWeekdayMap =
            Maps.uniqueIndex(Arrays.asList(EWeekday.values()), EWeekday::getAbbrv);

}
