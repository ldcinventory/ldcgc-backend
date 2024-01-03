package org.ldcgc.backend.service.location;

import org.ldcgc.backend.exception.RequestException;
import org.ldcgc.backend.payload.dto.location.LocationDto;
import org.ldcgc.backend.util.retrieving.Messages;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface LocationService {
    List<LocationDto> getAllLocations();

    static LocationDto findLocationInListByName(String locationName, List<LocationDto> locations){
        return locations.stream()
                .filter(location -> location.getName().equalsIgnoreCase(locationName))
                .findFirst()
                .orElseThrow(() -> new RequestException(HttpStatus.NOT_FOUND, Messages.Error.LOCATION_NOT_FOUND
                        .formatted(locationName, locations.stream().map(LocationDto::getName).toList().toString())));
    }
}
