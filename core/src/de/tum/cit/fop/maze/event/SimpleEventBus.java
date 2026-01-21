package de.tum.cit.fop.maze.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Simple implementation of the EventBus interface.
 * Uses a map of event types to listener lists.
 */
public class SimpleEventBus implements EventBus {
    
    private final Map<Class<? extends GameEvent>, List<EventListener<?>>> listeners;
    
    public SimpleEventBus() {
        this.listeners = new HashMap<>();
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public <T extends GameEvent> void subscribe(Class<T> eventType, EventListener<T> listener) {
        listeners.computeIfAbsent(eventType, k -> new ArrayList<>()).add(listener);
    }
    
    @Override
    public <T extends GameEvent> void unsubscribe(Class<T> eventType, EventListener<T> listener) {
        List<EventListener<?>> eventListeners = listeners.get(eventType);
        if (eventListeners != null) {
            eventListeners.remove(listener);
        }
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public void publish(GameEvent event) {
        List<EventListener<?>> eventListeners = listeners.get(event.getClass());
        if (eventListeners != null) {
            for (EventListener<?> listener : new ArrayList<>(eventListeners)) {
                ((EventListener<GameEvent>) listener).onEvent(event);
            }
        }
    }
    
    @Override
    public void clear() {
        listeners.clear();
    }
}
