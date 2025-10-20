package com.NetMasters.NetMasters.presentation.mappers;

import com.NetMasters.NetMasters.core.entities.Player;
import com.NetMasters.NetMasters.infrastructure.persistence.models.PlayerModel;
import com.NetMasters.NetMasters.presentation.dto.PlayerDTO;
import com.NetMasters.NetMasters.presentation.dto.CreatePlayerDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import java.util.List;

@Mapper(componentModel = "spring", uses = {TimestampMapper.class})
public interface PlayerMapper {
    PlayerMapper INSTANCE = Mappers.getMapper(PlayerMapper.class);

    PlayerDTO playerToPlayerDTO(Player player);
    
    List<PlayerDTO> playersToPlayerDTOs(List<Player> players);

    Player playerDTOToPlayer(PlayerDTO playerDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Player createPlayerDTOToPlayer(CreatePlayerDTO createPlayerDTO);

    PlayerModel playerToPlayerModel(Player player);

    Player playerModelToPlayer(PlayerModel playerModel);
    
    List<Player> playerModelsToPlayers(List<PlayerModel> playerModels);
}
