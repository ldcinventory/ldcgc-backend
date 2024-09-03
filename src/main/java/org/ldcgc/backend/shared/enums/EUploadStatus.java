package org.ldcgc.backend.shared.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EUploadStatus implements EnumMethods {

    INSERTED, UPDATED, SKIPPED
}
