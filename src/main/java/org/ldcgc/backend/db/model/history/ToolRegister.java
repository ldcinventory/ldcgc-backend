package org.ldcgc.backend.db.model.history;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
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

    private LocalDateTime registerFrom;

    private LocalDateTime registerTo;

    @ManyToOne
    @JoinColumn(name = "tool_id", referencedColumnName = "id")
    private Tool tool;

    @ManyToOne
    @JoinColumn(name = "volunteer_id", referencedColumnName = "id")
    private Volunteer volunteer;

}
