package org.ldcgc.backend.payload.mapper.users;

import org.ldcgc.backend.db.model.users.User;
import org.ldcgc.backend.payload.dto.users.UserCredentialsDto;
import org.ldcgc.backend.payload.dto.users.UserDto;
import org.ldcgc.backend.payload.mapper.category.CategoryMapper;
import org.ldcgc.backend.payload.mapper.location.LocationMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import static org.ldcgc.backend.util.creation.Generator.getEncryptedPassword;

@Mapper(uses = { LocationMapper.class, CategoryMapper.class, VolunteerMapper.class })
public interface UserMapper {

    UserMapper MAPPER = Mappers.getMapper(UserMapper.class);

    User toEntity(UserDto userRequest);

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "group.location.locations", ignore = true)
    UserDto toDTO(User user);

    UserCredentialsDto toCredentialsDTO(User user);

    @Mapping(source = "password", target = "password", qualifiedByName = "mapPasswordToEncryptedPassword")
    void update(UserDto userFrom, @MappingTarget User userTo);

    @Named("mapPasswordToEncryptedPassword")
    static String mapPasswordToEncryptedPassword(String password) {
        return getEncryptedPassword(password);
    }

}
