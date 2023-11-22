package org.ldcgc.backend.service.location.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ldcgc.backend.db.repository.location.LocationRepository;
import org.ldcgc.backend.payload.dto.location.LocationDto;
import org.ldcgc.backend.payload.mapper.location.LocationMapper;
import org.ldcgc.backend.service.location.LocationService;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {
    LocationRepository repository;

    @Override
    public List<LocationDto> getAllLocations() {
        return repository.findAll().stream()
                .map(LocationMapper.MAPPER::toDto)
                .toList();
    }
}
