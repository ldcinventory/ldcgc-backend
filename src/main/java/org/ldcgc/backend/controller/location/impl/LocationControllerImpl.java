package org.ldcgc.backend.controller.location.impl;

import lombok.RequiredArgsConstructor;
import org.ldcgc.backend.controller.location.LocationController;
import org.ldcgc.backend.payload.dto.location.LocationDto;
import org.ldcgc.backend.service.location.LocationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LocationControllerImpl implements LocationController {

    private final LocationService locationService;

    public ResponseEntity<?> getLocations(Integer groupId) {
        return locationService.getLocations(groupId);
    }

    public ResponseEntity<?> getLocation(Integer locationId, boolean detailed) {
        return locationService.getLocation(locationId, detailed);
    }

    public ResponseEntity<?> createLocation(LocationDto locationDto) {
        return locationService.createLocation(locationDto);
    }

    public ResponseEntity<?> updateLocation(Integer locationId, LocationDto locationDto) {
        return locationService.updateLocation(locationId, locationDto);
    }

    public ResponseEntity<?> deleteLocation(Integer locationId) {
        return locationService.deleteLocation(locationId);
    }
}
