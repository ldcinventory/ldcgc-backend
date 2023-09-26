package org.ldcgc.backend.util.compare;

public interface EnumMethods {

    default Object equalsAny(Object... objects) {
        for (Object o : objects)
            if (this == o) return true;
        return false;
    }
}
