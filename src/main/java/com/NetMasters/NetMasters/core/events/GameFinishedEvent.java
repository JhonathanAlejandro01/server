package com.NetMasters.NetMasters.core.events;

import com.NetMasters.NetMasters.core.valueobjects.MatchId;
import com.NetMasters.NetMasters.core.valueobjects.PlayerId;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class GameFinishedEvent extends ApplicationEvent {
    private final MatchId matchId;
    private final PlayerId winnerId;
    private final boolean isDraw;

    public GameFinishedEvent(Object source, MatchId matchId, PlayerId winnerId, boolean isDraw) {
        super(source);
        this.matchId = matchId;
        this.winnerId = winnerId;
        this.isDraw = isDraw;
    }
}