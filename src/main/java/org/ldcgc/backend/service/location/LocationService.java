package org.ldcgc.backend.service.location;

import org.ldcgc.backend.payload.dto.location.LocationDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface LocationService {
    List<LocationDto> getAllLocations();

    LocationDto findLocationInListByName(String locationLvl2, List<LocationDto> locations);
}
