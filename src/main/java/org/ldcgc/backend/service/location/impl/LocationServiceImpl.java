package org.ldcgc.backend.service.location.impl;

import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.ldcgc.backend.db.model.location.Location;
import org.ldcgc.backend.db.repository.group.GroupRepository;
import org.ldcgc.backend.db.repository.location.LocationRepository;
import org.ldcgc.backend.exception.RequestException;
import org.ldcgc.backend.payload.dto.location.LocationDto;
import org.ldcgc.backend.payload.mapper.location.LocationMapper;
import org.ldcgc.backend.security.jwt.JwtUtils;
import org.ldcgc.backend.service.location.LocationService;
import org.ldcgc.backend.util.constants.Messages;
import org.ldcgc.backend.util.creation.Constructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.apache.commons.lang3.StringUtils.containsIgnoreCase;

@Component
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepository;
    private final GroupRepository groupRepository;
    private final JwtUtils jwtUtils;

    public List<LocationDto> getAllLocations() {
        return locationRepository.findAll().stream()
            .map(LocationMapper.MAPPER::toDto)
            .toList();
    }

    public ResponseEntity<?> getLocations(String location, String warehouse, String placement) {
        List<LocationDto> locations = new ArrayList<>(locationRepository.findAllLevel0().stream()
            .map(LocationMapper.MAPPER::toDtoDetailed)
            .toList());

        if(StringUtils.isAllBlank(location, warehouse, placement))

            return Constructor.buildResponseMessageObject(
                HttpStatus.OK,
                String.format(Messages.Info.LOCATION_FOUND,
                    locationRepository.countByLevel(0),
                    locationRepository.countByLevel(1),
                    locationRepository.countByLevel(2)),
                locations);

        int numOfLocations = 0;
        AtomicInteger numOfWarehouses = new AtomicInteger(0);
        AtomicInteger numOfPlacements = new AtomicInteger(0);
        if(StringUtils.isNotBlank(location) && CollectionUtils.isNotEmpty(locations)) {
            locations.removeIf(l -> !containsIgnoreCase(l.getName(), location));
            numOfLocations = locations.size();

            if(CollectionUtils.isNotEmpty(locations)) {
                if (StringUtils.isNotBlank(warehouse))
                    locations.parallelStream().forEach(l -> l.getLocations()
                            .removeIf(w -> w == null || !containsIgnoreCase(w.getName(), warehouse)));

                locations.parallelStream()
                    .forEach(l -> numOfWarehouses.getAndAdd(l.getLocations().size()));

                if (StringUtils.isNotBlank(placement))
                    locations.parallelStream()
                        .forEach(l -> l.getLocations().parallelStream()
                            .forEach(w -> w.getLocations()
                                .removeIf(p -> p == null || !containsIgnoreCase(p.getName(), placement))));

                locations.parallelStream()
                    .forEach(l -> l.getLocations().parallelStream()
                        .forEach(w -> numOfPlacements.getAndAdd(w.getLocations().size())));

            }
        }

        return Constructor.buildResponseMessageObject(
            HttpStatus.OK,
            String.format(Messages.Info.LOCATION_FOUND, numOfLocations, numOfWarehouses.get(), numOfPlacements.get()),
            locations);
    }

    public ResponseEntity<?> getLocation(Integer locationId, boolean detailed) {
        Location location = locationRepository.findById(locationId).orElseThrow(
            () -> new RequestException(HttpStatus.NOT_FOUND, String.format(Messages.Error.LOCATION_NOT_FOUND, locationId)));

        return Constructor.buildResponseObject(HttpStatus.OK, detailed
            ? LocationMapper.MAPPER.toDtoDetailed(location)
            : LocationMapper.MAPPER.toDto(location));
    }

    public ResponseEntity<?> createLocation(LocationDto locationDto) {
        validateGroup(locationDto.getGroupId());
        validateLocationNotExists(locationDto.getName());

        Location location = locationRepository.saveAndFlush(LocationMapper.MAPPER.toMo(locationDto));

        return Constructor.buildResponseObject(HttpStatus.OK, LocationMapper.MAPPER.toDto(location));
    }

    public ResponseEntity<?> updateLocation(Integer locationId, LocationDto locationDto) {
        validateGroup(locationDto.getGroupId());

        Location location = locationRepository.findById(locationId).orElseThrow(
            () -> new RequestException(HttpStatus.NOT_FOUND, String.format(Messages.Error.LOCATION_NOT_FOUND, locationId)));

        LocationMapper.MAPPER.updateLocation(locationDto, location);
        location = locationRepository.saveAndFlush(location);

        return Constructor.buildResponseMessageObject(
            HttpStatus.OK,
            String.format(Messages.Info.LOCATION_UPDATED, locationId),
            LocationMapper.MAPPER.toDto(location));
    }

    public ResponseEntity<?> deleteLocation(Integer locationId) {
        Location location = locationRepository.findById(locationId).orElseThrow(
            () -> new RequestException(HttpStatus.NOT_FOUND, String.format(Messages.Error.LOCATION_NOT_FOUND, locationId)));

        validateLocationNotInGroup(location);

        locationRepository.delete(location);

        return Constructor.buildResponseMessage(
            HttpStatus.OK,
            String.format(Messages.Info.LOCATION_DELETED, locationId));
    }

    private void validateGroup(Integer groupId) {
        if(!groupRepository.existsById(groupId))
            throw new RequestException(HttpStatus.NOT_FOUND, String.format(Messages.Error.GROUP_NOT_FOUND, groupId));
    }

    private void validateLocationNotExists(String name) {
        if(locationRepository.existsByName(name))
            throw new RequestException(HttpStatus.BAD_REQUEST, String.format(Messages.Error.LOCATION_EXISTS, name));
    }

    private void validateLocationNotInGroup(Location location) {
        if(groupRepository.existsByLocation_Id(location.getId()))
            throw new RequestException(HttpStatus.BAD_REQUEST, Messages.Error.LOCATION_MAIN_GROUP);
    }

}
