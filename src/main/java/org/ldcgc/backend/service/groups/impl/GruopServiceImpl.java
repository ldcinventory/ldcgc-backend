package org.ldcgc.backend.service.groups.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ldcgc.backend.db.repository.group.GroupRepository;
import org.ldcgc.backend.exception.RequestException;
import org.ldcgc.backend.payload.dto.group.GroupDto;
import org.ldcgc.backend.payload.mapper.group.GroupMapper;
import org.ldcgc.backend.service.groups.GroupsService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.ldcgc.backend.util.retrieving.Message.ErrorMessage.GROUP_NOT_FOUND;
import static org.ldcgc.backend.util.retrieving.Message.getErrorMessage;

@Slf4j
@Component
@RequiredArgsConstructor
public class GruopServiceImpl implements GroupsService {

    private final GroupRepository repository;
    @Override
    public List<GroupDto> getAllGroups() {
        return repository.findAll().stream()
                .map(GroupMapper.MAPPER::toDto)
                .toList();
    }

    @Override
    public GroupDto findGroupInListByName(String groupName, List<GroupDto> groups) {
        return groups.stream()
                .filter(group -> group.getName().equalsIgnoreCase(groupName))
                .findFirst()
                .orElseThrow(() -> new RequestException(HttpStatus.NOT_FOUND, getErrorMessage(GROUP_NOT_FOUND)
                        .formatted(groupName, groups.stream().map(GroupDto::getName).toList().toString())));
    }
}
