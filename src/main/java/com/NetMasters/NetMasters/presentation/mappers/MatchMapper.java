package com.NetMasters.NetMasters.presentation.mappers;

import com.NetMasters.NetMasters.core.entities.Match;
import com.NetMasters.NetMasters.infrastructure.persistence.models.MatchModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {TimestampMapper.class})
public interface MatchMapper {
    MatchMapper INSTANCE = Mappers.getMapper(MatchMapper.class);

    @Mapping(source = "game.id", target = "gameId")
    @Mapping(source = "player1.id", target = "player1Id")
    @Mapping(source = "player2.id", target = "player2Id")
    Match matchModelToMatch(MatchModel matchModel);

    @Mapping(source = "gameId", target = "game.id")
    @Mapping(source = "player1Id", target = "player1.id")
    @Mapping(source = "player2Id", target = "player2.id")
    MatchModel matchToMatchModel(Match match);
}
