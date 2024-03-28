package org.ldcgc.backend.util.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EUploadStatus implements EnumMethods {

    INSERTED, UPDATED, SKIPPED
}
