package org.ldcgc.backend.util.conversion;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Convert {

    public static String nowToTimeStampString(){
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSS", Locale.ENGLISH).format(LocalDateTime.now());
    }

}
