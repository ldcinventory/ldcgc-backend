package org.ldcgc.backend.db.model.users;

import jakarta.persistence.*;
import lombok.*;
import org.ldcgc.backend.db.model.group.Group;

import java.util.List;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@Entity
@Table(name = "volunteers")
public class Volunteer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private Integer id;

    private String name;

    private String lastName;

    // also, this id will be their barcode
    private String builderAssistantId;

    private boolean isActive;

    @OneToOne(mappedBy = "volunteer", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private Availability availability;

    @OneToMany(mappedBy = "volunteer", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Absence> absences;

    @ManyToOne
    @JoinColumn(name = "group_id", referencedColumnName = "id")
    private Group group;

}
