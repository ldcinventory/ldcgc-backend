package org.ldcgc.backend.util.conversion;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

public class Convert {

    public static String nowToTimeStampString(){
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS", Locale.ENGLISH).format(LocalDateTime.now());
    }

    public static String localDateTimeToTimeStampString(LocalDateTime localDateTime){
        return DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.ENGLISH).format(localDateTime);
    }

    public static LocalDate stringToLocalDate(String dbString, String pattern) {
        return LocalDate.parse(dbString, DateTimeFormatter.ofPattern(pattern));
    }

    public static Float toFloat2Decimals(String number) {
        if(StringUtils.isEmpty(number))
            return null;

        return BigDecimal
            .valueOf(Float.parseFloat(number))
            .setScale(2, RoundingMode.HALF_EVEN)
            .floatValue();
    }

    public static Float toFloat(String number) {
        if(StringUtils.isEmpty(number))
            return null;

        return Float.parseFloat(number);
    }

    public static LocalDateTime dateToLocalDateTime(Date dateToConvert) {
        return dateToConvert.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public static LocalDate dateToLocalDate(Date dateToConvert) {
        return dateToConvert.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

}
