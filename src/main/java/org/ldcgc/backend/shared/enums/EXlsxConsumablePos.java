package org.ldcgc.backend.shared.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EXlsxConsumablePos implements EnumMethods {

    //A - 0
    BARCODE(0),
    //B - 1
    RESOURCE_TYPE(1),
    //C - 2
    BRAND(2),
    //D - 3
    NAME(3),
    //E - 4
    MODEL(4),
    //F - 5
    DESCRIPTION(5),
    //G - 6
    PRICE(6),
    //H - 7
    PURCHASE_DATE(7),
    //I - 8
    QTY_EACH_ITEM(8),
    //J - 9
    STOCK(9),
    //K - 10
    MIN_STOCK(10),
    //L - 11
    STOCK_TYPE(11),
    //M - 12
    LOCATION(12),
    //N - 13
    //O - 14
    //P - 15
    GROUP(15);

    private final Integer columnNumber;
}
