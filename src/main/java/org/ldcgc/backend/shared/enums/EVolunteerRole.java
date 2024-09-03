package org.ldcgc.backend.shared.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum EVolunteerRole implements EnumMethods {

    USE_ELECTRIC_TOOLS("use_electric_tools");

    private final String name;

}
