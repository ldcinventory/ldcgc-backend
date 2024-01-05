package org.ldcgc.backend.db.model.group;

import jakarta.persistence.*;
import lombok.*;
import org.ldcgc.backend.db.model.location.Location;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@Entity
@Table(name = "groups")
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private Integer id;

    private String name;

    private String description;

    private String phoneNumber;

    private String urlImage;

    @ManyToOne
    @JoinColumn(name = "location_id", referencedColumnName = "id")
    private Location location;


}
