package org.ldcgc.backend.util.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ERegisterStatus implements EnumMethods {
    OPENED ("opened"),
    CLOSED ("closed");

    private final String name;
}
