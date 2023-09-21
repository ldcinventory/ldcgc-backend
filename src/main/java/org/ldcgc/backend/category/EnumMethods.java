package org.ldcgc.backend.category;

public interface EnumMethods {

    default Object equalsAny(Object... objects) {
        for (Object o : objects)
            if (this == o) return true;
        return false;
    }
}
