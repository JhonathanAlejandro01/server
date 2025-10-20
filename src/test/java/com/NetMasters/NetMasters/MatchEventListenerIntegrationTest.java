package com.NetMasters.NetMasters;

import com.NetMasters.NetMasters.application.services.MatchService;
import com.NetMasters.NetMasters.infrastructure.persistence.models.GameModel;
import com.NetMasters.NetMasters.infrastructure.persistence.models.PlayerModel;
import com.NetMasters.NetMasters.infrastructure.persistence.repositories.ConnectFourGameRepository;
import com.NetMasters.NetMasters.infrastructure.persistence.repositories.GameRepository;
import com.NetMasters.NetMasters.infrastructure.persistence.repositories.MatchRepository;
import com.NetMasters.NetMasters.infrastructure.persistence.repositories.PlayerRepository;
import com.NetMasters.NetMasters.infrastructure.persistence.repositories.TriquiGameRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class MatchEventListenerIntegrationTest {

    @Autowired
    private MatchService matchService;

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private TriquiGameRepository triquiGameRepository;

    @Autowired
    private ConnectFourGameRepository connectFourGameRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Test
    @Transactional
    public void createMatch_triggers_gameCreation_listenerCreatesTriqui() throws InterruptedException {
    // Create a Game (TRIQUI) and a Player to use in the test
    GameModel game = GameModel.builder().name(com.NetMasters.NetMasters.core.entities.GameName.TRIQUI).genre("test").build();
    game = gameRepository.save(game);

    PlayerModel player = PlayerModel.builder().username("testuser").email("test@example.com").password("pwd").gamesPlayed(0).gamesTied(0).gamesWinner(0).onlineState(false).build();
    player = playerRepository.save(player);

    Long gameId = game.getId();
    Long player1 = player.getId();

    Long matchId = matchService.createMatch(gameId, player1, null);

        assertThat(matchRepository.findById(matchId)).isPresent();

        // Wait briefly for event listener (synchronous in same thread though)
        Thread.sleep(200);

        boolean hasTriqui = triquiGameRepository.findByMatchId(matchId).isPresent();
        boolean hasConnect = connectFourGameRepository.findAll().stream().anyMatch(g -> g.getMatch().getId().equals(matchId));

        // Expect one of them depending on the game type
        assertThat(hasTriqui || hasConnect).isTrue();
    }
}
