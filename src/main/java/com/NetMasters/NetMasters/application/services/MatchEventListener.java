package com.NetMasters.NetMasters.application.services;

import com.NetMasters.NetMasters.core.events.MatchCreatedEvent;
import com.NetMasters.NetMasters.core.valueobjects.MatchId;
import com.NetMasters.NetMasters.infrastructure.persistence.models.MatchModel;
import com.NetMasters.NetMasters.infrastructure.persistence.repositories.MatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class MatchEventListener {

    private static final Logger logger = LoggerFactory.getLogger(MatchEventListener.class);

    private final TriquiService triquiService;
    private final ConnectFourService connectFourService;
    private final MatchRepository matchRepository;

    @Autowired
    public MatchEventListener(TriquiService triquiService,
                              ConnectFourService connectFourService,
                              MatchRepository matchRepository) {
        this.triquiService = triquiService;
        this.connectFourService = connectFourService;
        this.matchRepository = matchRepository;
    }

    @EventListener
    @Transactional
    public void onMatchCreated(MatchCreatedEvent event) {
        // Extract id
        MatchId matchIdVo = event.getMatchId();
        if (matchIdVo == null) return;
        Long matchId = matchIdVo.getValue();

        String gameType = event.getGameType();
        if (gameType == null) return;

        try {
            switch (gameType.toUpperCase()) {
                case "TRIQUI":
                case "GAME_NAME_TRIQ":
                case "GAME_TRIQ":
                    triquiService.createTriquiGame(matchId);
                    break;
                case "CONNECT4":
                case "CONNECT_FOUR":
                    connectFourService.createConnectFourGame(matchId);
                    break;
                default:
                    // Unknown game: nothing to do
                    break;
            }
            // Optionally set match to IN_PROGRESS if repository is available
            MatchModel mm = matchRepository.findById(matchId).orElse(null);
            if (mm != null) {
                // Some games may expect the match to be IN_PROGRESS once game created
                // We set it only if currently PENDING
                if (mm.getStatus().name().equalsIgnoreCase("PENDING")) {
                    mm.setStatus(com.NetMasters.NetMasters.core.entities.MatchStatus.IN_PROGRESS);
                    matchRepository.save(mm);
                }
            }
        } catch (Exception e) {
            // Log exception but do not rethrow to avoid breaking event dispatch
            logger.error("Error handling MatchCreatedEvent for matchId={}: {}", matchId, e.getMessage(), e);
        }
    }
}
