package de.tum.cit.fop.maze.event;

/**
 * Interface for the game event bus.
 * Allows publishing and subscribing to game events.
 */
public interface EventBus {
    
    /**
     * Subscribes a listener to a specific event type.
     * @param eventType The class of events to listen for
     * @param listener The listener to call when events occur
     * @param <T> The event type
     */
    <T extends GameEvent> void subscribe(Class<T> eventType, EventListener<T> listener);
    
    /**
     * Unsubscribes a listener from a specific event type.
     * @param eventType The class of events to stop listening for
     * @param listener The listener to remove
     * @param <T> The event type
     */
    <T extends GameEvent> void unsubscribe(Class<T> eventType, EventListener<T> listener);
    
    /**
     * Publishes an event to all subscribed listeners.
     * @param event The event to publish
     */
    void publish(GameEvent event);
    
    /**
     * Removes all listeners.
     */
    void clear();
}
