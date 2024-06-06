package org.ldcgc.backend.payload.mapper.common;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.ldcgc.backend.db.model.resources.Resource;
import org.mapstruct.Named;

import java.util.Arrays;

import static org.ldcgc.backend.util.constants.Google.DRIVE_IMAGES_URL;

public interface MapperMethods {

    static String[] mapStringArrayWithPrefix(String[] stringArray, String driveImagesUrl) {
        if(stringArray == null) return null;

        return Arrays.stream(stringArray)
            .map(url -> String.format(driveImagesUrl, url))
            .toArray(String[]::new);
    }

    @Named("mapBarcode")
    static String mapBarcode(String barcode) {
        return StringUtils.defaultIfBlank(barcode,
            "#" + RandomStringUtils.randomAlphanumeric(8).toUpperCase());
    }

    @Named("mapUrlImages")
    static String[] mapUrlImages(String[] urlImages){
        return mapStringArrayWithPrefix(urlImages, DRIVE_IMAGES_URL);
    }

    @Named("mapResourceName")
    static String mapResourceName(Resource resource) {
        return String.format("%s # %s # %s%s",
            resource.getBarcode(),
            resource.getBrand().getName(),
            resource.getName(),
            StringUtils.isBlank(resource.getModel()) ? "" : " # " + resource.getModel());
    }

}
