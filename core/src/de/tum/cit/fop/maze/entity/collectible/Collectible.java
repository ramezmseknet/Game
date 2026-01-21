package de.tum.cit.fop.maze.entity.collectible;

import de.tum.cit.fop.maze.entity.GameObject;
import de.tum.cit.fop.maze.entity.Player;

/**
 * Interface for collectible items in the maze.
 * Keys, hearts, and power-ups implement this.
 */
public interface Collectible extends GameObject {
    
    /**
     * Called when the player collects this item.
     * Applies the collectible's effect to the player.
     * @param player The player collecting the item
     */
    void onCollect(Player player);
    
    /**
     * Checks if this item has already been collected.
     * @return true if collected
     */
    boolean isCollected();
    
    /**
     * Gets the type of this collectible.
     * @return The collectible type enum value
     */
    CollectibleType getType();
}
