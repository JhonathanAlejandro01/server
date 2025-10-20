package com.NetMasters.NetMasters.core.events;

import com.NetMasters.NetMasters.core.valueobjects.MatchId;
import com.NetMasters.NetMasters.core.valueobjects.PlayerId;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class InvitationSentEvent extends ApplicationEvent {
    private final MatchId matchId;
    private final PlayerId invitedPlayerId;

    public InvitationSentEvent(Object source, MatchId matchId, PlayerId invitedPlayerId) {
        super(source);
        this.matchId = matchId;
        this.invitedPlayerId = invitedPlayerId;
    }
}