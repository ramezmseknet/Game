package de.tum.cit.fop.maze.event;

/**
 * Event fired when the player collects a heart.
 */
public class HeartCollectedEvent extends GameEvent {
    
    private final int healthRestored;
    private final int currentHealth;
    
    public HeartCollectedEvent(int healthRestored, int currentHealth) {
        super();
        this.healthRestored = healthRestored;
        this.currentHealth = currentHealth;
    }
    
    public int getHealthRestored() { return healthRestored; }
    public int getCurrentHealth() { return currentHealth; }
}
