package org.ldcgc.backend.util.conversion;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Convert {

    public static String nowToTimeStampString(){
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSS", Locale.ENGLISH).format(LocalDateTime.now());
    }

    public static LocalDate stringToLocalDate(String dbString, String pattern) {
        return LocalDate.parse(dbString, DateTimeFormatter.ofPattern(pattern));
    }

    public static Float convertToFloat2Decimals(String number) {
        if(StringUtils.isEmpty(number))
            return null;

        return BigDecimal
            .valueOf(Float.parseFloat(number))
            .setScale(2, RoundingMode.HALF_EVEN)
            .floatValue();
    }

    public static Float convertToFloat(String number) {
        if(StringUtils.isEmpty(number))
            return null;

        return Float.parseFloat(number);
    }

}
