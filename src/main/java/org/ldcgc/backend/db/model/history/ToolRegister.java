package org.ldcgc.backend.db.model.history;

import jakarta.persistence.*;
import lombok.*;
import org.ldcgc.backend.db.model.resources.Tool;
import org.ldcgc.backend.db.model.users.Volunteer;

import java.time.LocalDateTime;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@Entity
@Table(name = "tool-register")
public class ToolRegister {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private Integer id;

    private LocalDateTime inRegistration;

    private LocalDateTime outRegistration;

    @OneToOne
    @JoinColumn(name = "tool_id", referencedColumnName = "id")
    private Tool tool;

    @OneToOne
    @JoinColumn(name = "volunteer_id", referencedColumnName = "id")
    private Volunteer volunteer;

}
