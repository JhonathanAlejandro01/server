package com.NetMasters.NetMasters.core.events;

import com.NetMasters.NetMasters.core.valueobjects.PlayerId;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class PlayerLoggedInEvent extends ApplicationEvent {
    private final PlayerId playerId;
    private final String username;

    public PlayerLoggedInEvent(Object source, PlayerId playerId, String username) {
        super(source);
        this.playerId = playerId;
        this.username = username;
    }
}