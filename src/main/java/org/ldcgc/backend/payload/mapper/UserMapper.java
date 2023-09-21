package org.ldcgc.backend.payload.mapper;

import org.ldcgc.backend.db.model.users.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import static org.ldcgc.backend.payload.dto.users.User.DTO;
import static org.ldcgc.backend.util.creation.Generator.getEncryptedPassword;

@Mapper
public interface UserMapper {

    UserMapper MAPPER = Mappers.getMapper(UserMapper.class);

    User toEntity(DTO userRequest);

    DTO toDTO(User user);

    @Mapping(source = "password", target = "password", qualifiedByName = "mapPasswordToEncryptedPassword")
    void update(DTO userFrom, @MappingTarget User userTo);

    @Named("mapPasswordToEncryptedPassword")
    static String mapPasswordToEncryptedPassword(String password) {
        return getEncryptedPassword(password);
    }

}
