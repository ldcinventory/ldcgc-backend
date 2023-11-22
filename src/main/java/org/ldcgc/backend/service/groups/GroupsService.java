package org.ldcgc.backend.service.groups;

import org.ldcgc.backend.payload.dto.group.GroupDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface GroupsService {
    List<GroupDto> getAllGroups();
}
