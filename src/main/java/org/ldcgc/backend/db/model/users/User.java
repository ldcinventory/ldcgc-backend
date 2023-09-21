package org.ldcgc.backend.db.model.users;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.ldcgc.backend.category.ERole;
import org.ldcgc.backend.db.model.category.SubCategory;
import org.ldcgc.backend.db.model.group.Group;

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

    @OneToOne
    @JoinColumn(name = "id")
    private Volunteer volunteer;

    @OneToOne
    @JoinColumn(name = "id")
    private SubCategory responsibility;

    @OneToOne
    @JoinColumn(name = "id")
    private Group group;

}
