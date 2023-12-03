package org.ldcgc.backend.db.model.location;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@Entity
@Table(name = "locations")
public class Location {

    public Location(String name, Integer level) {
        this.name = name;
        this.description = name;
        this.level = level;
    }

    public Location(String name, Location parent, Integer level) {
        this.name = name;
        this.description = name;
        this.parent = parent;
        this.level = level;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private Integer id;

    private String name;

    private String description;

    private String url;

    private Integer level;

    @ManyToOne
    private Location parent;

    @OneToMany(mappedBy = "parent", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Location> locations = new ArrayList<>();

}
