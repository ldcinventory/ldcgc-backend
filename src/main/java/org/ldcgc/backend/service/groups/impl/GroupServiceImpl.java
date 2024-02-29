package org.ldcgc.backend.service.groups.impl;

import lombok.RequiredArgsConstructor;
import org.ldcgc.backend.db.repository.group.GroupRepository;
import org.ldcgc.backend.exception.RequestException;
import org.ldcgc.backend.payload.dto.group.GroupDto;
import org.ldcgc.backend.payload.mapper.group.GroupMapper;
import org.ldcgc.backend.service.groups.GroupsService;
import org.ldcgc.backend.util.constants.Messages;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupsService {

    private final GroupRepository groupRepository;

    @Override
    public List<GroupDto> getAllGroups() {
        return groupRepository.findAll().stream()
                .map(GroupMapper.MAPPER::toDto)
                .toList();
    }

    @Override
    public GroupDto findGroupByName(String groupName) {
        return groupRepository.getGroupByName(groupName)
            .map(GroupMapper.MAPPER::toDto)
            .orElseThrow(() -> new RequestException(HttpStatus.NOT_FOUND, Messages.Error.GROUP_NOT_FOUND.formatted(groupName)));
    }
}
