package com.NetMasters.NetMasters.core.events;

import com.NetMasters.NetMasters.core.valueobjects.Coordinates;
import com.NetMasters.NetMasters.core.valueobjects.MatchId;
import com.NetMasters.NetMasters.core.valueobjects.PlayerId;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class MoveMadeEvent extends ApplicationEvent {
    private final MatchId matchId;
    private final PlayerId playerId;
    private final Coordinates coordinates;
    private final String symbol;

    public MoveMadeEvent(Object source, MatchId matchId, PlayerId playerId, Coordinates coordinates, String symbol) {
        super(source);
        this.matchId = matchId;
        this.playerId = playerId;
        this.coordinates = coordinates;
        this.symbol = symbol;
    }
}