package com.NetMasters.NetMasters.core.events;

import com.NetMasters.NetMasters.core.valueobjects.MatchId;
import com.NetMasters.NetMasters.core.valueobjects.PlayerId;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class GameStartedEvent extends ApplicationEvent {
    private final MatchId matchId;
    private final PlayerId player1Id;
    private final PlayerId player2Id;

    public GameStartedEvent(Object source, MatchId matchId, PlayerId player1Id, PlayerId player2Id) {
        super(source);
        this.matchId = matchId;
        this.player1Id = player1Id;
        this.player2Id = player2Id;
    }
}
// Similar para MoveMadeEvent, PlayerDisconnectedEvent, etc.