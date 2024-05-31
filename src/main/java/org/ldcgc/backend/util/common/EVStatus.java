package org.ldcgc.backend.util.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EVStatus implements EnumMethods {

    ACTIVE,
    INACTIVE,
    LOCKED,
    DELETED

}
