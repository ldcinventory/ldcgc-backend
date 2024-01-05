package org.ldcgc.backend.db.model.users;

import jakarta.persistence.*;
import lombok.*;
import org.ldcgc.backend.db.mapper.AvailabilityConverter;
import org.ldcgc.backend.util.common.EWeekday;

import java.util.HashSet;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@Entity
@Table(name = "availabilities")
public class Availability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private Integer id;

    // this converter gets the natural string from DB, which is formatted as an array ['L','M','X',...]
    // and instantiate it in backend as a Set of EWeekday enum
    @Convert(converter = AvailabilityConverter.class)
    @Column(columnDefinition = "text")
    private HashSet<EWeekday> availabilityDays;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "volunteer_id")
    private Volunteer volunteer;

}
