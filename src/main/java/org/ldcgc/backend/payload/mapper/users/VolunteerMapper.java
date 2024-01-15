package org.ldcgc.backend.payload.mapper.users;

import org.ldcgc.backend.db.model.users.Volunteer;
import org.ldcgc.backend.payload.dto.users.VolunteerDto;
import org.ldcgc.backend.util.common.EWeekday;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(nullValuePropertyMappingStrategy = IGNORE, uses = { AbsenceMapper.class })
public interface VolunteerMapper {

    VolunteerMapper MAPPER = Mappers.getMapper(VolunteerMapper.class);

    Volunteer toEntity(VolunteerDto volunteerRequest);

    @Mapping(target= "availability", qualifiedByName = "mapAvailability")
    @Mapping(target = "absences", ignore = true)
    VolunteerDto toDto(Volunteer volunteer);

    void update(@MappingTarget Volunteer volunteer, VolunteerDto volunteerDto);

    @Named("mapAvailability")
    static List<EWeekday> mapAvailability(Set<EWeekday> entityAvailability) {
        if(CollectionUtils.isEmpty(entityAvailability))
            return Collections.emptyList();

        return entityAvailability.stream().toList();
    }

}
