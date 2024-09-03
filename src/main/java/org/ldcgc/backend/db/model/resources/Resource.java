package org.ldcgc.backend.db.model.resources;

import lombok.Getter;
import lombok.Setter;
import org.ldcgc.backend.db.model.category.Brand;
import org.ldcgc.backend.db.model.category.ResourceType;
import org.ldcgc.backend.db.model.group.Group;
import org.ldcgc.backend.db.model.location.Location;
import org.ldcgc.backend.shared.enums.EUploadStatus;

import java.time.LocalDate;

@Setter
@Getter
public abstract class Resource {

    // common to tools and consumables
    private Integer id;
    private String barcode;
    private ResourceType resourceType;
    private Brand brand;
    private String name;
    private String model;
    private String description;
    private Float price;
    private LocalDate purchaseDate;
    private String[] urlImages;
    private Location location;
    private EUploadStatus uploadStatus;
    private Group group;

}
