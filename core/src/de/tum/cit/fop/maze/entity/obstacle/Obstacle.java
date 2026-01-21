package de.tum.cit.fop.maze.entity.obstacle;

import de.tum.cit.fop.maze.entity.GameObject;
import de.tum.cit.fop.maze.entity.Player;

/**
 * Interface for obstacles that can damage the player.
 * Traps, hazards, and dynamic obstacles implement this.
 */
public interface Obstacle extends GameObject {
    
    /**
     * Gets the damage this obstacle deals to the player.
     * @return Number of lives to subtract (usually 1)
     */
    int getDamage();
    
    /**
     * Called when the player contacts this obstacle.
     * Handles damage application and visual/audio effects.
     * @param player The player that touched the obstacle
     */
    void onContact(Player player);
    
    /**
     * Checks if this obstacle kills instantly.
     * @return true if contact means instant death
     */
    boolean isLethal();
    
    /**
     * Checks if this obstacle can currently deal damage.
     * Some obstacles may have cooldowns or inactive states.
     * @return true if the obstacle can damage the player
     */
    boolean canDamage();
}
