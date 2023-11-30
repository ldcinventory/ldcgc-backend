package org.ldcgc.backend.service.location.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ldcgc.backend.db.repository.location.LocationRepository;
import org.ldcgc.backend.payload.dto.location.LocationDto;
import org.ldcgc.backend.payload.mapper.location.LocationMapper;
import org.ldcgc.backend.service.location.LocationService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.ldcgc.backend.exception.RequestException;

import static org.ldcgc.backend.util.retrieving.Message.ErrorMessage.LOCATION_NOT_FOUND;
import static org.ldcgc.backend.util.retrieving.Message.getErrorMessage;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepository;
    @Override
    public List<LocationDto> getAllLocations() {
        return locationRepository.findAll().stream()
                .map(LocationMapper.MAPPER::toDto)
                .toList();
    }

    @Override
    public LocationDto findLocationInListByName(String locationName, List<LocationDto> locations) {
        return locations.stream()
                .filter(location -> location.getName().equalsIgnoreCase(locationName))
                .findFirst()
                .orElseThrow(() -> new RequestException(HttpStatus.NOT_FOUND, getErrorMessage(LOCATION_NOT_FOUND)
                        .formatted(locationName, locations.stream().map(LocationDto::getName).toList().toString())));
    }
}
