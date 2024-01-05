package org.ldcgc.backend.db.model.users;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.ldcgc.backend.db.model.category.Category;
import org.ldcgc.backend.db.model.group.Group;
import org.ldcgc.backend.util.common.ERole;

import java.time.LocalDateTime;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private Integer id;

    private String email;

    private String password;

    @NotNull @NonNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ERole role;

    private LocalDateTime acceptedEULA;

    private LocalDateTime acceptedEULAManager;

    @OneToOne
    @JoinColumn(name = "volunteer_id", referencedColumnName = "id")
    private Volunteer volunteer;

    @ManyToOne
    @JoinColumn(name = "responsibility_id", referencedColumnName = "id")
    private Category responsibility;

    @ManyToOne
    @JoinColumn(name = "group_id", referencedColumnName = "id")
    private Group group;

}
