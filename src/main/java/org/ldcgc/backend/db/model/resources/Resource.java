package org.ldcgc.backend.db.model.resources;

import lombok.Getter;
import org.ldcgc.backend.db.model.category.Brand;

@Getter
public abstract class Resource {

    private String barcode;
    private Brand brand;
    private String name;
    private String model;

}
