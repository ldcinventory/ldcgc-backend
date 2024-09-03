package org.ldcgc.backend.shared.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum EOrder implements EnumMethods {

    ASC ("asc"),
    DESC ("desc");

    private final String order;

}
