package org.ldcgc.backend.configuration;

import org.ldcgc.backend.util.common.EOrder;
import org.springframework.core.convert.converter.Converter;

public class ConverterTypes implements Converter<String, EOrder> {

    public EOrder convert(String source) {
        return switch (source.toLowerCase()) {
            case "desc" -> EOrder.DESC;
            case "asc"  -> EOrder.ASC;
            default     -> null;
        };
    }
}
