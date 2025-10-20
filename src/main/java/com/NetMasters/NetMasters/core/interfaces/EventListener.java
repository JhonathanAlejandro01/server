package com.NetMasters.NetMasters.core.interfaces;

@FunctionalInterface
public interface EventListener<T> {
    void handle(T event);
}