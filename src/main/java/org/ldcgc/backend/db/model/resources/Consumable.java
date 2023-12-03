package org.ldcgc.backend.db.model.resources;

import jakarta.persistence.*;
import lombok.*;
import org.ldcgc.backend.db.model.category.Category;
import org.ldcgc.backend.db.model.group.Group;
import org.ldcgc.backend.db.model.location.Location;

import java.time.LocalDate;

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

    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "brand_id", referencedColumnName = "id")
    private Category brand;

    private String name;

    private String model;

    private String description;

    private Float price;

    private LocalDate purchaseDate;

    private String urlImages;

    @Column(nullable = false)
    private Integer stock;

    private Integer minStock;

    @ManyToOne
    @JoinColumn(name = "stock_id", referencedColumnName = "id")
    private Category stockType;

    @ManyToOne
    @JoinColumn(name = "location_id", referencedColumnName = "id")
    private Location location;

    @ManyToOne
    @JoinColumn(name = "group_id", referencedColumnName = "id")
    private Group group;

}
