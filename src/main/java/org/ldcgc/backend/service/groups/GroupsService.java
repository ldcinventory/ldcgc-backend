package org.ldcgc.backend.service.groups;

import org.ldcgc.backend.exception.RequestException;
import org.ldcgc.backend.payload.dto.group.GroupDto;
import org.ldcgc.backend.util.retrieving.Messages;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface GroupsService {
    List<GroupDto> getAllGroups();

    static GroupDto findGroupInListByName(String groupName, List<GroupDto> groups){
        return groups.stream()
                .filter(group -> group.getName().equalsIgnoreCase(groupName))
                .findFirst()
                .orElseThrow(() -> new RequestException(HttpStatus.NOT_FOUND, Messages.Error.GROUP_NOT_FOUND
                        .formatted(groupName, groups.stream().map(GroupDto::getName).toList().toString())));
    }
}
