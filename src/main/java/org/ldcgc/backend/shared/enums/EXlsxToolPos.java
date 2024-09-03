package org.ldcgc.backend.shared.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EXlsxToolPos implements EnumMethods{

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
    WEIGHT(6),
    //H - 7
    STOCK_WEIGHT_TYPE(7),
    //I - 8
    PRICE(8),
    //J - 9
    PURCHASE_DATE(9),
    //K - 10
    MAINTENANCE_FREQUENCY(10),
    //L - 11
    MAINTENANCE_PERIOD(11),
    //M - 12
    LAST_MAINTENANCE(12),
    //N - 13
    NEXT_MAINTENANCE(13),
    //O - 14
    STATUS(14),
    //P - 15
    //Q - 16
    //R - 17
    LOCATION(17);

    private final Integer columnNumber;
}
