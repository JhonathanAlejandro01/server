package com.NetMasters.NetMasters.core.events;

import com.NetMasters.NetMasters.core.valueobjects.MatchId;
import com.NetMasters.NetMasters.core.valueobjects.PlayerId;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class MatchCreatedEvent extends ApplicationEvent {
    private final MatchId matchId;
    private final PlayerId player1Id;
    private final PlayerId player2Id;
    private final String gameType;

    public MatchCreatedEvent(Object source, MatchId matchId, PlayerId player1Id, PlayerId player2Id, String gameType) {
        super(source);
        this.matchId = matchId;
        this.player1Id = player1Id;
        this.player2Id = player2Id;
        this.gameType = gameType;
    }
}