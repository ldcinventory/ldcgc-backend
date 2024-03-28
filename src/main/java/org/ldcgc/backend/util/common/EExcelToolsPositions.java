package org.ldcgc.backend.util.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EExcelToolsPositions {

    BARCODE(0),
    NAME(1),
    BRAND(2),
    MODEL(3),
    RESOURCE_TYPE(4),
    DESCRIPTION(5),
    URL_IMAGES(6),
    STATUS(7),
    LOCATION(8),
    MAINTENANCE_PERIOD(9),
    MAINTENANCE_TIME(10),
    LAST_MAINTENANCE(11),
    GROUP(12);

    private final Integer columnNumber;
}
