package com.NetMasters.NetMasters;

import com.NetMasters.NetMasters.application.services.ConnectFourService;
import com.NetMasters.NetMasters.application.services.MatchService;
import com.NetMasters.NetMasters.core.entities.MatchStatus;
import com.NetMasters.NetMasters.infrastructure.persistence.models.ConnectFourGameModel;
import com.NetMasters.NetMasters.infrastructure.persistence.models.GameModel;
import com.NetMasters.NetMasters.infrastructure.persistence.models.PlayerModel;
import com.NetMasters.NetMasters.infrastructure.persistence.repositories.ConnectFourGameRepository;
import com.NetMasters.NetMasters.infrastructure.persistence.repositories.GameRepository;
import com.NetMasters.NetMasters.infrastructure.persistence.repositories.MatchRepository;
import com.NetMasters.NetMasters.infrastructure.persistence.repositories.PlayerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class ConnectFourPlaythroughE2ETest {

    @Autowired
    private MatchService matchService;

    @Autowired
    private ConnectFourService connectFourService;

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private ConnectFourGameRepository connectFourGameRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Test
    @Transactional
    public void fullGame_playthrough_player1Wins() throws InterruptedException {
        // Create game type CONNECT_FOUR
    GameModel game = GameModel.builder().name(com.NetMasters.NetMasters.core.entities.GameName.CONNECT4).genre("test").build();
        game = gameRepository.save(game);

        // Create two players
        PlayerModel p1 = PlayerModel.builder().username("p1").email("p1@example.com").password("pwd").gamesPlayed(0).gamesTied(0).gamesWinner(0).onlineState(false).build();
        PlayerModel p2 = PlayerModel.builder().username("p2").email("p2@example.com").password("pwd").gamesPlayed(0).gamesTied(0).gamesWinner(0).onlineState(false).build();
        p1 = playerRepository.save(p1);
        p2 = playerRepository.save(p2);

        // Create match (this should trigger the listener to create the Connect Four game)
        Long matchId = matchService.createMatch(game.getId(), p1.getId(), p2.getId());
        assertThat(matchRepository.findById(matchId)).isPresent();

        // Wait briefly for listener to create the game
        Thread.sleep(200);

        ConnectFourGameModel gm = connectFourGameRepository.findByMatchId(matchId).orElseThrow(() -> new IllegalStateException("Connect4 game not created"));
        Long gameId = gm.getId();

        // To make player1 win, we'll place four tokens vertically in column 0 by alternating turns.
        // Sequence: p1 col0, p2 col1, p1 col0, p2 col1, p1 col0, p2 col1, p1 col0 -> p1 should win

        assertThat(connectFourService.makeMove(gameId, p1.getId(), 0)).isTrue();
        assertThat(connectFourService.makeMove(gameId, p2.getId(), 1)).isTrue();
        assertThat(connectFourService.makeMove(gameId, p1.getId(), 0)).isTrue();
        assertThat(connectFourService.makeMove(gameId, p2.getId(), 1)).isTrue();
        assertThat(connectFourService.makeMove(gameId, p1.getId(), 0)).isTrue();
        assertThat(connectFourService.makeMove(gameId, p2.getId(), 1)).isTrue();

        // Last move that should produce victory
        boolean result = connectFourService.makeMove(gameId, p1.getId(), 0);
        assertThat(result).isTrue();

        // Reload game and match
        gm = connectFourGameRepository.findById(gameId).orElseThrow();
        assertThat(gm.getWinner()).isNotNull();
        assertThat(gm.getWinner().getId()).isEqualTo(p1.getId());

        assertThat(matchRepository.findById(matchId).get().getStatus()).isEqualTo(MatchStatus.FINISHED);
    }
}
