package org.ldcgc.backend.service.location.impl;

import lombok.RequiredArgsConstructor;
import org.ldcgc.backend.db.model.location.Location;
import org.ldcgc.backend.db.repository.group.GroupRepository;
import org.ldcgc.backend.db.repository.location.LocationRepository;
import org.ldcgc.backend.exception.RequestException;
import org.ldcgc.backend.payload.dto.location.LocationDto;
import org.ldcgc.backend.payload.mapper.location.LocationMapper;
import org.ldcgc.backend.service.location.LocationService;
import org.ldcgc.backend.util.constants.Messages;
import org.ldcgc.backend.util.creation.Constructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepository;
    private final GroupRepository groupRepository;

    public List<LocationDto> getAllLocations() {
        return locationRepository.findAll().stream()
            .map(LocationMapper.MAPPER::toDto)
            .toList();
    }

    public ResponseEntity<?> getLocations(Integer groupId) {
        List<LocationDto> locations = groupId == null
            ? getAllLocations()
            : locationRepository.findAllByGroupId(groupId).stream().map(LocationMapper.MAPPER::toDto).toList();

        return Constructor.buildResponseMessageObject(
            HttpStatus.OK,
            String.format(Messages.Info.LOCATION_FOUND, locations.size()),
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
