package org.ldcgc.backend.service.group;

import org.ldcgc.backend.payload.dto.group.GroupDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface GroupService {
    List<GroupDto> getAllGroups();

    GroupDto findGroupInListByName(String groupName, List<GroupDto> groups);
}
