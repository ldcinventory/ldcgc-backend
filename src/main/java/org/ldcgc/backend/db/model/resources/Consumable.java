package org.ldcgc.backend.db.model.resources;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ldcgc.backend.db.model.category.SubCategory;
import org.ldcgc.backend.db.model.group.Group;
import org.ldcgc.backend.db.model.location.LocationLvl2;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@Entity
@Table(name = "consumables")
public class Consumable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private Integer id;

    // TODO validation to not allow null once a barcode is registered
    //  (i.e. after batch a tool could have a null barcode)
    private String barcode;

    @OneToOne
    @JoinColumn(name = "id")
    private SubCategory category;

    @OneToOne
    @JoinColumn(name = "id")
    private SubCategory brand;

    private String name;

    private String model;

    private String description;

    private String urlImages;

    @Column(nullable = false)
    private Integer stock;

    private Integer minStock;

    @OneToOne
    @JoinColumn(name = "id")
    private SubCategory stockType;

    @OneToOne
    @JoinColumn(name = "id")
    private LocationLvl2 location;

    @OneToOne
    @JoinColumn(name = "id")
    private Group group;

}
