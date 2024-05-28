package org.ldcgc.backend.util.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum EOrder implements EnumMethods {

    ASC ("asc"),
    DESC ("desc");

    private final String order;

}
