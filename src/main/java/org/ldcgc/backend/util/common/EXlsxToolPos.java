package org.ldcgc.backend.util.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EXlsxToolPos {

    BARCODE(0),
    RESOURCE_TYPE(1),
    BRAND(2),
    NAME(3),
    MODEL(4),
    DESCRIPTION(5),
    WEIGHT(6),
    STOCK_WEIGHT_TYPE(7),
    PRICE(8),
    PURCHASE_DATE(9),
    URL_IMAGES(10),
    MAINTENANCE_PERIOD(11),
    MAINTENANCE_TIME(12),
    LAST_MAINTENANCE(13),
    NEXT_MAINTENANCE(14),
    STATUS(15),
    LOCATION(16),
    GROUP(17);

    private final Integer columnNumber;
}
