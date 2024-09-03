package org.ldcgc.backend.shared.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ELocationType implements EnumMethods {
    LOCATION  ("localización", 0),
    WAREHOUSE ("almacén", 1),
    PLACEMENT ("ubicación", 2);

    private final String name;
    private final Integer level;

    public static ELocationType fromLevel(Integer level) {
        for (ELocationType locationType : ELocationType.values())
            if (locationType.level.equals(level))
                return locationType;

        return null;
    }
}
