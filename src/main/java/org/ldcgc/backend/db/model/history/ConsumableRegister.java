package org.ldcgc.backend.db.model.history;

import jakarta.persistence.*;
import lombok.*;
import org.ldcgc.backend.db.model.resources.Consumable;
import org.ldcgc.backend.db.model.users.Volunteer;

import java.time.LocalDateTime;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@Entity
@Table(name = "consumable-register")
public class ConsumableRegister {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private Integer id;

    private LocalDateTime inRegistration;

    private LocalDateTime outRegistration;

    private Integer stockLeft;

    @OneToOne
    @JoinColumn(name = "consumable_id", referencedColumnName = "id")
    private Consumable consumable;

    @OneToOne
    @JoinColumn(name = "volunteer_id", referencedColumnName = "id")
    private Volunteer volunteer;

}
