package org.ldcgc.backend.payload.mapper.common;

import java.util.Arrays;

public class MapperMethods {

    public static String[] mapStringArrayWithPrefix(String[] stringArray, String driveImagesUrl) {
        if(stringArray == null) return null;

        return Arrays.stream(stringArray)
            .map(url -> String.format(driveImagesUrl, url))
            .toArray(String[]::new);
    }
}
