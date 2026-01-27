package de.tum.cit.fop.maze.entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Base interface for all game objects in the maze.
 * Provides common contract for updating, rendering, and collision detection.
 */
public interface GameObject {
    
    /**
     * Updates the game object's state.
     * @param deltaTime Time elapsed since last frame in seconds
     */
    void update(float deltaTime);
    
    /**
     * Renders the game object.
     * @param batch The SpriteBatch to draw with
     */
    void render(SpriteBatch batch);
    
    /**
     * Gets the world position of the object.
     * @return Position vector (bottom-left corner)
     */
    Vector2 getPosition();
    
    /**
     * Sets the world position of the object.
     * @param x X coordinate
     * @param y Y coordinate
     */
    void setPosition(float x, float y);
    
    /**
     * Gets the bounding rectangle for collision detection.
     * @return Rectangle bounds in world coordinates
     */
    Rectangle getBounds();
    
    /**
     * Checks if the object is active (should be updated/rendered).
     * @return true if active, false if should be removed
     */
    boolean isActive();
    
    /**
     * Sets the active state of the object.
     * @param active Whether the object should be active
     */
    void setActive(boolean active);
    
    /**
     * Gets the tile X coordinate of this object.
     * @return Grid X position
     */
    int getTileX();
    
    /**
     * Gets the tile Y coordinate of this object.
     * @return Grid Y position
     */
    int getTileY();
}
