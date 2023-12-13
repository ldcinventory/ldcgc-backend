package org.ldcgc.backend.db.model.users;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ldcgc.backend.db.mapper.AvailabilityConverter;
import org.ldcgc.backend.util.common.EWeekday;

import java.util.List;
import java.util.Set;

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
    // and instantiate it in backend as a List of EWeekday enum
    @Convert(converter = AvailabilityConverter.class)
    @Column(columnDefinition = "text")
    private Set<EWeekday> availabilityDays;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "volunteer_id")
    private Volunteer volunteer;

}
