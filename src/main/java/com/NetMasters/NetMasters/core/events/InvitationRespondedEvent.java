package com.NetMasters.NetMasters.core.events;


import com.NetMasters.NetMasters.core.valueobjects.MatchId;
import com.NetMasters.NetMasters.core.valueobjects.PlayerId;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class InvitationRespondedEvent extends ApplicationEvent {
    private final MatchId matchId;
    private final PlayerId playerId;
    private final boolean accepted;

    public InvitationRespondedEvent(Object source, MatchId matchId, PlayerId playerId, boolean accepted) {
        super(source);
        this.matchId = matchId;
        this.playerId = playerId;
        this.accepted = accepted;
    }
}