package org.ldcgc.backend.service.location.impl;

import lombok.RequiredArgsConstructor;
import org.ldcgc.backend.db.repository.location.LocationRepository;
import org.ldcgc.backend.exception.RequestException;
import org.ldcgc.backend.payload.dto.location.LocationDto;
import org.ldcgc.backend.payload.mapper.location.LocationMapper;
import org.ldcgc.backend.service.location.LocationService;
import org.ldcgc.backend.util.constants.Messages;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;

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
    public LocationDto findLocationByName(String locationName) {
        return locationRepository.getLocationByName(locationName)
            .map(LocationMapper.MAPPER::toDto)
            .orElseThrow(() -> new RequestException(HttpStatus.NOT_FOUND, Messages.Error.LOCATION_NOT_FOUND.formatted(locationName)));

    }

}
