package org.ldcgc.backend.db.model.history;

import jakarta.persistence.*;
import lombok.*;
import org.ldcgc.backend.db.mapper.StatusConverter;
import org.ldcgc.backend.db.model.resources.Tool;
import org.ldcgc.backend.db.model.users.Volunteer;
import org.ldcgc.backend.util.common.EStatus;

import java.time.LocalDate;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@Entity
@Table(name = "maintenance")
public class Maintenance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private Integer id;

    private LocalDate inRegistration;

    private LocalDate outRegistration;

    private String details;

    private String urlImages;

    @ManyToOne
    @JoinColumn(name = "tool_id", referencedColumnName = "id")
    private Tool tool;

    @ManyToOne
    @JoinColumn(name = "volunteer_id", referencedColumnName = "id")
    private Volunteer volunteer;

    @Convert(converter = StatusConverter.class)
    @Column(columnDefinition = "int")
    private EStatus inStatus;

    @Convert(converter = StatusConverter.class)
    @Column(columnDefinition = "int")
    private EStatus outStatus;

}
