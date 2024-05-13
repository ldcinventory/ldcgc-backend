package org.ldcgc.backend.service.location;

import org.ldcgc.backend.payload.dto.location.LocationDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface LocationService {

    List<LocationDto> getAllLocations();

    ResponseEntity<?> getLocations(Integer groupId);

    ResponseEntity<?> getLocation(Integer locationId, boolean detailed);

    ResponseEntity<?> createLocation(LocationDto locationDto);

    ResponseEntity<?> updateLocation(Integer locationId, LocationDto locationDto);

    ResponseEntity<?> deleteLocation(Integer locationId);

}
