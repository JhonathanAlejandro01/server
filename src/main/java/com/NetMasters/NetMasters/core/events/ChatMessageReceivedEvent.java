package com.NetMasters.NetMasters.core.events;

import com.NetMasters.NetMasters.core.valueobjects.GameId;
import com.NetMasters.NetMasters.core.valueobjects.PlayerId;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class ChatMessageReceivedEvent extends ApplicationEvent {
    private final GameId gameId;
    private final PlayerId senderId;
    private final String message;

    public ChatMessageReceivedEvent(Object source, GameId gameId, PlayerId senderId, String message) {
        super(source);
        this.gameId = gameId;
        this.senderId = senderId;
        this.message = message;
    }
}
