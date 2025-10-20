package com.NetMasters.NetMasters.core.events;

import com.NetMasters.NetMasters.core.valueobjects.PlayerId;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class RankingUpdatedEvent extends ApplicationEvent {
    private final PlayerId playerId;
    private final String gameType;
    private final int newScore;
    private final int previousScore;

    public RankingUpdatedEvent(Object source, PlayerId playerId, String gameType, int newScore, int previousScore) {
        super(source);
        this.playerId = playerId;
        this.gameType = gameType;
        this.newScore = newScore;
        this.previousScore = previousScore;
    }
}