package org.ldcgc.backend.db.model.resources;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ldcgc.backend.db.mapper.StatusConverter;
import org.ldcgc.backend.db.mapper.StockTypeConverter;
import org.ldcgc.backend.db.mapper.StringArrayConverter;
import org.ldcgc.backend.db.mapper.TimeUnitConverter;
import org.ldcgc.backend.db.model.category.Category;
import org.ldcgc.backend.db.model.group.Group;
import org.ldcgc.backend.db.model.history.Maintenance;
import org.ldcgc.backend.db.model.location.Location;
import org.ldcgc.backend.util.common.EStatus;
import org.ldcgc.backend.util.common.EStockType;
import org.ldcgc.backend.util.common.ETimeUnit;

import java.time.LocalDate;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@Entity
@Table(name = "tools")
public class Tool {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private Integer id;

    // TODO validation to not allow null nor duplications once a barcode is registered
    //  (i.e. after batch a tool could have a null barcode)
    @Column(unique = true)
    //@GeneratedValue //TODO: make generator class
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

    private Float weight;

    @Convert(converter = StockTypeConverter.class)
    private EStockType stockWeightType;

    private Float price;

    private LocalDate purchaseDate;

    @Convert(converter = StringArrayConverter.class)
    @Column(columnDefinition = "text")
    private String[] urlImages;

    private Integer maintenancePeriod;

    @Convert(converter = TimeUnitConverter.class)
    @Column(columnDefinition = "int")
    private ETimeUnit maintenanceTime;

    private LocalDate lastMaintenance;

    private LocalDate nextMaintenance;

    @OneToOne
    @JoinColumn(name = "lastMaintenanceDetails_id", referencedColumnName = "id")
    private Maintenance lastMaintenanceDetails;

    @Convert(converter = StatusConverter.class)
    @Column(columnDefinition = "int")
    private EStatus status;

    @ManyToOne
    @JoinColumn(name = "location_id", referencedColumnName = "id")
    private Location location;

    @ManyToOne
    @JoinColumn(name = "group_id", referencedColumnName = "id")
    private Group group;

}
