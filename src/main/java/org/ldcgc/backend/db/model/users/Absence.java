package org.ldcgc.backend.db.model.users;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@Entity
@Table(name = "absences")
public class Absence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private Integer id;

    private LocalDate dateFrom;

    private LocalDate dateTo;

    @ManyToOne
    private Volunteer volunteer;

}
