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
import jakarta.persistence.Transient;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.ldcgc.backend.db.mapper.StatusConverter;
import org.ldcgc.backend.db.mapper.StockTypeConverter;
import org.ldcgc.backend.db.mapper.StringArrayConverter;
import org.ldcgc.backend.db.mapper.TimeUnitConverter;
import org.ldcgc.backend.db.model.category.Brand;
import org.ldcgc.backend.db.model.category.ResourceType;
import org.ldcgc.backend.db.model.group.Group;
import org.ldcgc.backend.db.model.history.Maintenance;
import org.ldcgc.backend.db.model.location.Location;
import org.ldcgc.backend.shared.enums.EStockType;
import org.ldcgc.backend.shared.enums.ETimeUnit;
import org.ldcgc.backend.shared.enums.EToolStatus;
import org.ldcgc.backend.shared.enums.EUploadStatus;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@Entity
@Transactional
@Table(name = "tools")
public class Tool extends Resource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private Integer id;

    @Column(unique = true)
    //@GeneratedValue //TODO: make generator class
    private String barcode;

    @ManyToOne
    @JoinColumn(name = "resource_type_id", referencedColumnName = "id")
    private ResourceType resourceType;

    @ManyToOne
    @JoinColumn(name = "brand_id", referencedColumnName = "id")
    private Brand brand;

    @Column(nullable = false)
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

    @ManyToOne
    @JoinColumn(name = "lastMaintenanceDetails_id", referencedColumnName = "id")
    private Maintenance lastMaintenanceDetails;

    @Convert(converter = StatusConverter.class)
    @Column(columnDefinition = "int")
    private EToolStatus status;

    @ManyToOne
    @JoinColumn(name = "location_id", referencedColumnName = "id")
    private Location location;

    @ManyToOne
    @JoinColumn(name = "group_id", referencedColumnName = "id")
    private Group group;

    @Transient
    private EUploadStatus uploadStatus;

    private boolean enabled = true;

}
