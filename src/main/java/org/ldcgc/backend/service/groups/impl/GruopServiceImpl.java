package org.ldcgc.backend.service.groups.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ldcgc.backend.db.repository.group.GroupRepository;
import org.ldcgc.backend.payload.dto.group.GroupDto;
import org.ldcgc.backend.payload.mapper.group.GroupMapper;
import org.ldcgc.backend.service.groups.GroupsService;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class GruopServiceImpl implements GroupsService {

    GroupRepository repository;
    @Override
    public List<GroupDto> getAllGroups() {
        return repository.findAll().stream()
                .map(GroupMapper.MAPPER::toDto)
                .toList();
    }
}
