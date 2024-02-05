package org.ldcgc.backend.util.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ERegisterStatus {
    OPEN ("open"),
    CLOSED("closed");

    private final String name;
}
