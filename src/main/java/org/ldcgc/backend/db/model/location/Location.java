package org.ldcgc.backend.db.model.location;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@Entity
@Table(name = "locations")
public class Location {

    public Location(String name, Integer level, Integer groupId) {
        this.name = name;
        this.description = name;
        this.level = level;
        this.groupId = groupId;
    }

    public Location(String name, Location parent, Integer level, Integer groupId) {
        this.name = name;
        this.description = name;
        this.parent = parent;
        this.level = level;
        this.groupId = groupId;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private Integer id;

    private String name;

    private String description;

    private String url;

    private Integer level;

    @ManyToOne(fetch = FetchType.LAZY)
    private Location parent;

    @OneToMany(mappedBy = "parent", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Location> locations = new ArrayList<>();

    private Integer groupId;

}
