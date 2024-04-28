package org.ldcgc.backend.util.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EXlsxConsumablePos {

    BARCODE(0),
    RESOURCE_TYPE(1),
    BRAND(2),
    NAME(3),
    MODEL(4),
    DESCRIPTION(5),
    PRICE(6),
    PURCHASE_DATE(7),
    URL_IMAGES(8),
    QTY_EACH_ITEM(9),
    STOCK(10),
    MIN_STOCK(11),
    STOCK_TYPE(12),
    LOCATION(13),
    GROUP(14);

    private final Integer columnNumber;
}
