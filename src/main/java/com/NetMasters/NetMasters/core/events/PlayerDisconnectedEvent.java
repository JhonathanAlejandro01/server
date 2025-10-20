package com.NetMasters.NetMasters.core.events;

import com.NetMasters.NetMasters.core.valueobjects.PlayerId;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class PlayerDisconnectedEvent extends ApplicationEvent {
    private final PlayerId playerId;

    public PlayerDisconnectedEvent(Object source, PlayerId playerId) {
        super(source);
        this.playerId = playerId;
    }
}
