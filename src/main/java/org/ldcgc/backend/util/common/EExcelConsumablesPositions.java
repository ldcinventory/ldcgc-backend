package org.ldcgc.backend.util.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EExcelConsumablesPositions {

    BRAND(0),
    CATEGORY(1),
    GROUP(2),
    LOCATION(3),
    MIN_STOCK(4),
    PRICE(5),
    PURCHASE_DATE(6),
    STOCK(7),
    STOCK_TYPE(8),
    BARCODE(9),
    DESCRIPTION(10),
    MODEL(11),
    NAME(12),
    URL_IMAGES(13);

    private final Integer columnNumber;
}
