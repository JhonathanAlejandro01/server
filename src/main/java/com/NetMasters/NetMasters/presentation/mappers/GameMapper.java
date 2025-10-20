package com.NetMasters.NetMasters.presentation.mappers;

import com.NetMasters.NetMasters.core.entities.Game;
import com.NetMasters.NetMasters.infrastructure.persistence.models.GameModel;
import com.NetMasters.NetMasters.presentation.dto.GameDTO;
import com.NetMasters.NetMasters.presentation.dto.CreateGameDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import java.util.List;

@Mapper(componentModel = "spring")
public interface GameMapper {
    GameMapper INSTANCE = Mappers.getMapper(GameMapper.class);

    GameDTO gameToGameDTO(Game game);

    List<GameDTO> gamesToGameDTOs(List<Game> games);

    Game gameDTOToGame(GameDTO gameDTO);

    @Mapping(target = "id", ignore = true)
    Game createGameDTOToGame(CreateGameDTO createGameDTO);

    GameModel gameToGameModel(Game game);

    Game gameModelToGame(GameModel gameModel);

    List<Game> gameModelsToGames(List<GameModel> gameModels);
}
