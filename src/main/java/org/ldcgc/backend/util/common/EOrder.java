package org.ldcgc.backend.util.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum EOrder {

    ASC ("asc"),
    DESC ("desc");

    private final String order;

}
