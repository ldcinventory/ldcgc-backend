package org.ldcgc.backend.transformer.status;


import org.mapstruct.Qualifier;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Qualifier
@Retention(RetentionPolicy.RUNTIME)
public @interface CustomStatusTransformer {

}

