package de.tum.cit.fop.maze.console;

import de.tum.cit.fop.maze.entity.Player;

/**
 * Interface for game context that can be controlled by the dev console.
 * Provides methods for debug commands and cheats.
 */
public interface ConsoleContext {

    /**
     * Gets the current player.
     * @return The player instance
     */
    Player getPlayer();

    /**
     * Toggles god mode (invincibility).
     * @return The new god mode state
     */
    boolean toggleGodMode();

    /**
     * Toggles noclip mode (move through walls).
     * @return The new noclip state
     */
    boolean toggleNoclip();

    /**
     * Sets the game speed multiplier.
     * @param multiplier Speed multiplier (1.0 = normal)
     */
    void setSpeedMultiplier(float multiplier);

    /**
     * Teleports the player to a specific tile position.
     * @param tileX Tile X coordinate
     * @param tileY Tile Y coordinate
     */
    void teleportPlayer(int tileX, int tileY);

    /**
     * Spawns an entity at a location.
     * @param type Entity type name
     * @param tileX Tile X coordinate
     * @param tileY Tile Y coordinate
     */
    void spawnEntity(String type, int tileX, int tileY);

    /**
     * Kills all enemies in the level.
     * @return Number of enemies killed
     */
    int killAllEnemies();

    /**
     * Loads a specific level.
     * @param levelPath Path to the level file
     */
    void loadLevel(String levelPath);

    /**
     * Completes the current level.
     */
    void completeLevel();
}
