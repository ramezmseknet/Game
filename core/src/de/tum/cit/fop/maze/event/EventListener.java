package de.tum.cit.fop.maze.event;

/**
 * Functional interface for event listeners.
 * @param <T> The type of event this listener handles
 */
@FunctionalInterface
public interface EventListener<T extends GameEvent> {
    
    /**
     * Called when an event of the subscribed type is published.
     * @param event The event that was published
     */
    void onEvent(T event);
}
