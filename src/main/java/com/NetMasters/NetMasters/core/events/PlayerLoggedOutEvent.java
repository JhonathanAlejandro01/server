package com.NetMasters.NetMasters.core.events;

import com.NetMasters.NetMasters.core.valueobjects.PlayerId;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class PlayerLoggedOutEvent extends ApplicationEvent {
    private final PlayerId playerId;
    private final String username;

    public PlayerLoggedOutEvent(Object source, PlayerId playerId, String username) {
        super(source);
        this.playerId = playerId;
        this.username = username;
    }
}