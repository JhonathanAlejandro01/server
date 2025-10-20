package com.NetMasters.NetMasters.core.interfaces;

public interface EventBus {
    /**
     * Publica un evento en el bus.
     * @param event El evento a publicar.
     */
    void publish(Object event);

    /**
     * Suscribe un listener a un tipo de evento.
     * @param eventType Clase del evento.
     * @param listener Listener a suscribir.
     */
    <T> void subscribe(Class<T> eventType, EventListener<T> listener);
}