package org.ldcgc.backend.app.configuration;

import org.ldcgc.backend.shared.enums.EOrder;
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
