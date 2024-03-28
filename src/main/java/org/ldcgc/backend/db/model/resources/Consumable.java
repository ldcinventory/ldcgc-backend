package org.ldcgc.backend.db.model.resources;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ldcgc.backend.db.mapper.StockTypeConverter;
import org.ldcgc.backend.db.mapper.StringArrayConverter;
import org.ldcgc.backend.db.model.category.Brand;
import org.ldcgc.backend.db.model.category.ResourceType;
import org.ldcgc.backend.db.model.group.Group;
import org.ldcgc.backend.db.model.location.Location;
import org.ldcgc.backend.util.common.EStockType;

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

    // TODO validation to not allow null nor duplications once a barcode is registered
    //  (i.e. after batch a tool could have a null barcode)
    @Column(unique = true)
    private String barcode;

    @ManyToOne
    @JoinColumn(name = "resource_type_id", referencedColumnName = "id")
    private ResourceType resourceType;

    @ManyToOne
    @JoinColumn(name = "brand_id", referencedColumnName = "id")
    private Brand brand;

    private String name;

    private String model;

    private String description;

    private Float price;

    private LocalDate purchaseDate;

    @Convert(converter = StringArrayConverter.class)
    @Column(columnDefinition = "text")
    private String[] urlImages;

    @Column(nullable = false)
    private Float quantityEachItem;

    @Column(nullable = false)
    private Float stock;

    private Float minStock;

    @Convert(converter = StockTypeConverter.class)
    @Column(columnDefinition = "int")
    private EStockType stockType;

    @ManyToOne
    @JoinColumn(name = "location_id", referencedColumnName = "id")
    private Location location;

    @ManyToOne
    @JoinColumn(name = "group_id", referencedColumnName = "id")
    private Group group;

}
