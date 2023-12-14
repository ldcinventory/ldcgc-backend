package org.ldcgc.backend.util.common;

public enum EEULAStatus {

    // EULA
    ACCEPT, PENDING, REJECT, REMOVE, DELETE;

    public final boolean equalsAny(EEULAStatus ...eulaStatuses) {
        for (EEULAStatus eulaStatus : eulaStatuses)
            if (this == eulaStatus) return true;
        return false;
    }

}
