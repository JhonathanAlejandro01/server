package com.NetMasters.NetMasters.presentation.mappers;

import com.NetMasters.NetMasters.core.entities.TriquiMove;
import com.NetMasters.NetMasters.infrastructure.persistence.models.TriquiMoveModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {TimestampMapper.class})
public interface TriquiMoveMapper {
    TriquiMoveMapper INSTANCE = Mappers.getMapper(TriquiMoveMapper.class);

    @Mapping(source = "triquiGame.id", target = "triquiGameId")
    @Mapping(source = "player.id", target = "playerId")
    TriquiMove triquiMoveModelToTriquiMove(TriquiMoveModel triquiMoveModel);

    @Mapping(source = "triquiGameId", target = "triquiGame.id")
    @Mapping(source = "playerId", target = "player.id")
    TriquiMoveModel triquiMoveToTriquiMoveModel(TriquiMove triquiMove);
}
