package org.ldcgc.backend.service.group.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ldcgc.backend.payload.dto.group.GroupDto;
import org.ldcgc.backend.service.group.GroupService;
import org.ldcgc.backend.db.repository.group.GroupRepository;
import org.ldcgc.backend.payload.mapper.group.GroupMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.ldcgc.backend.exception.RequestException;

import static org.ldcgc.backend.util.retrieving.Message.ErrorMessage.GROUP_NOT_FOUND;
import static org.ldcgc.backend.util.retrieving.Message.getErrorMessage;


import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {

    private final GroupRepository groupRepository;

    @Override
    public List<GroupDto> getAllGroups() {
        return groupRepository.findAll().stream()
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
