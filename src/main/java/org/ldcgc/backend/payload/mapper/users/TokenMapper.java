package org.ldcgc.backend.payload.mapper.users;

import org.ldcgc.backend.db.model.users.Token;
import org.ldcgc.backend.payload.dto.users.TokenDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TokenMapper {

    TokenMapper MAPPER = Mappers.getMapper(TokenMapper.class);

    @Mapping(target = "isRecoveryToken", source = "recoveryToken")
    @Mapping(target = "isRefreshToken", source = "refreshToken")
    TokenDto toDto(Token token);

}
