package com.NetMasters.NetMasters.infrastructure.persistence.config;

import com.NetMasters.NetMasters.core.interfaces.EventBus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EventBusConfig {

    @Bean
    public EventBus eventBus() {
        return new SimpleEventBus();
    }
}