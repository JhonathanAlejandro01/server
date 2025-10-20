package com.NetMasters.NetMasters.infrastructure.persistence.config;

import com.NetMasters.NetMasters.core.interfaces.EventBus;
import com.NetMasters.NetMasters.core.interfaces.EventListener;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class SimpleEventBus implements EventBus, ApplicationEventPublisherAware {

    private ApplicationEventPublisher eventPublisher;
    private final Map<Class<?>, List<EventListener<?>>> listeners = new HashMap<>();

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.eventPublisher = applicationEventPublisher;
    }

    @Override
    public void publish(Object event) {
        if (eventPublisher != null) {
            eventPublisher.publishEvent(event);
        }

        // También notificar a los listeners registrados directamente
        List<EventListener<?>> eventListeners = listeners.get(event.getClass());
        if (eventListeners != null) {
            for (EventListener listener : eventListeners) {
                try {
                    listener.handle(event);
                } catch (Exception e) {
                    // Log error but continue
                    System.err.println("Error handling event: " + e.getMessage());
                }
            }
        }
    }

    @Override
    public <T> void subscribe(Class<T> eventType, EventListener<T> listener) {
        listeners.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>()).add(listener);
    }
}