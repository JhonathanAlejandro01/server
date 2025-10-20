package com.NetMasters.NetMasters;

import com.NetMasters.NetMasters.core.events.GameStartedEvent;
import com.NetMasters.NetMasters.core.valueobjects.MatchId;
import com.NetMasters.NetMasters.core.valueobjects.PlayerId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@SpringBootTest
public class GameStartedWebSocketListenerTest {

    @Autowired
    private ApplicationEventPublisher publisher;

    @MockBean
    private SimpMessagingTemplate messagingTemplate;

    @Test
    public void whenGameStartedEventPublished_thenMessageSent() {
        // Publish event
        GameStartedEvent event = new GameStartedEvent(this, new MatchId(123L), new PlayerId(1L), new PlayerId(2L));
        publisher.publishEvent(event);

    // Verify that template.convertAndSend was called with the expected destination
    org.mockito.ArgumentCaptor<String> destCaptor = org.mockito.ArgumentCaptor.forClass(String.class);
    org.mockito.ArgumentCaptor<Object> payloadCaptor = org.mockito.ArgumentCaptor.forClass(Object.class);
    verify(messagingTemplate, timeout(500)).convertAndSend(destCaptor.capture(), payloadCaptor.capture());
    org.assertj.core.api.Assertions.assertThat(destCaptor.getValue()).isEqualTo("/topic/match/123/started");
    }
}
