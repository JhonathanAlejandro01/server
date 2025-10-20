package com.NetMasters.NetMasters.presentation.mappers;

import com.NetMasters.NetMasters.core.entities.TriquiGame;
import com.NetMasters.NetMasters.infrastructure.persistence.models.TriquiGameModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface TriquiGameMapper {
    TriquiGameMapper INSTANCE = Mappers.getMapper(TriquiGameMapper.class);

    @Mapping(source = "match.id", target = "matchId")
    @Mapping(source = "currentTurn.id", target = "currentTurn")
    @Mapping(source = "winner.id", target = "winner")
    TriquiGame triquiGameModelToTriquiGame(TriquiGameModel triquiGameModel);

    @Mapping(source = "matchId", target = "match.id")
    @Mapping(source = "currentTurn", target = "currentTurn.id")
    @Mapping(source = "winner", target = "winner.id")
    TriquiGameModel triquiGameToTriquiGameModel(TriquiGame triquiGame);
}
