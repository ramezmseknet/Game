package de.tum.cit.fop.maze.event;

/**
 * Base class for all game events.
 * Events are dispatched through the EventBus.
 */
public abstract class GameEvent {
    
    private final long timestamp;
    private boolean consumed;
    
    protected GameEvent() {
        this.timestamp = System.currentTimeMillis();
        this.consumed = false;
    }
    
    /**
     * Gets the time this event was created.
     * @return Timestamp in milliseconds
     */
    public long getTimestamp() {
        return timestamp;
    }
    
    /**
     * Checks if this event has been consumed.
     * @return true if consumed
     */
    public boolean isConsumed() {
        return consumed;
    }
    
    /**
     * Marks this event as consumed.
     * Consumed events may be ignored by subsequent listeners.
     */
    public void consume() {
        this.consumed = true;
    }
}
