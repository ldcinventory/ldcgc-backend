package org.ldcgc.backend.util.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EExcelConsumablesPositions {

    BRAND(0),
    RESOURCE_TYPE(1),
    MIN_STOCK(2),
    PRICE(3),
    PURCHASE_DATE(4),
    STOCK(5),
    STOCK_TYPE(6),
    QTY_EACH_ITEM(7),
    BARCODE(8),
    DESCRIPTION(8),
    MODEL(10),
    NAME(11),
    URL_IMAGES(12),
    GROUP(13),
    LOCATION(14);

    private final Integer columnNumber;
}
