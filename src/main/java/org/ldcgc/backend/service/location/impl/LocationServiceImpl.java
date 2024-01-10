package org.ldcgc.backend.service.location.impl;

import lombok.RequiredArgsConstructor;
import org.ldcgc.backend.db.model.location.Location;
import org.ldcgc.backend.db.repository.location.LocationRepository;
import org.ldcgc.backend.payload.dto.location.LocationDto;
import org.ldcgc.backend.payload.mapper.location.LocationMapper;
import org.ldcgc.backend.service.location.LocationService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {
    private final LocationRepository repository;

    @Override
    public List<LocationDto> getAllLocations() {
        return repository.findAll().stream()
                .map(LocationMapper.MAPPER::toDto)
                .toList();
    }

    @Override
    public Location findLocationInListByName(String locationLvl2, List<LocationDto> locations) {
        return null;
    }

}
