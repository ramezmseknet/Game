package de.tum.cit.fop.maze.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import de.tum.cit.fop.maze.util.Constants;

/**
 * Exit door entity that displays different sprites based on lock state.
 * Uses sprites from things.png: column 1, rows 1-4.
 * - Row 1 (index 0): Closed/locked door
 * - Row 4 (index 3): Open/unlocked door
 * 
 * Extends AbstractEntity directly for exit functionality.
 */
public class ExitDoor extends AbstractEntity {
    
    private static final int THING_TILE_SIZE = 16;
    
    private static Texture thingsTexture;
    private static TextureRegion closedDoorTexture;
    private static TextureRegion openDoorTexture;
    private static boolean texturesLoaded = false;
    
    /** Whether the exit door is currently unlocked */
    private boolean isUnlocked;
    
    /**
     * Creates an exit door at the specified tile position.
     * @param tileX Tile X position
     * @param tileY Tile Y position
     */
    public ExitDoor(int tileX, int tileY) {
        super(tileX, tileY, Constants.TILE_SIZE, Constants.TILE_SIZE);
        this.isUnlocked = false;
        
        loadTextures();
        updateTexture();
    }
    
    /**
     * Loads door textures from things.png.
     * Column 1 (index 0), rows 1-4 (indices 0-3).
     */
    private static void loadTextures() {
        if (texturesLoaded) return;
        
        try {
            thingsTexture = new Texture(Gdx.files.internal("things.png"));
            int col = 0;
            
            closedDoorTexture = new TextureRegion(
                thingsTexture,
                col * THING_TILE_SIZE,
                0 * THING_TILE_SIZE,
                THING_TILE_SIZE,
                THING_TILE_SIZE
            );
            
            openDoorTexture = new TextureRegion(
                thingsTexture,
                col * THING_TILE_SIZE,
                3 * THING_TILE_SIZE,
                THING_TILE_SIZE,
                THING_TILE_SIZE
            );
            
            texturesLoaded = true;
            Gdx.app.log("ExitDoor", "Loaded door textures from things.png");
        } catch (Exception e) {
            Gdx.app.error("ExitDoor", "Failed to load door textures: " + e.getMessage());
        }
    }
    
    /**
     * Updates the door texture based on unlock state.
     */
    private void updateTexture() {
        if (isUnlocked && openDoorTexture != null) {
            this.textureRegion = openDoorTexture;
        } else if (closedDoorTexture != null) {
            this.textureRegion = closedDoorTexture;
        }
    }
    
    /**
     * Sets the door unlock state. Call this when the player collects all keys.
     * @param unlocked true if the door should be unlocked/open
     */
    public void setUnlocked(boolean unlocked) {
        if (this.isUnlocked != unlocked) {
            this.isUnlocked = unlocked;
            updateTexture();
            Gdx.app.log("ExitDoor", "Door " + (unlocked ? "unlocked" : "locked"));
        }
    }
    
    /**
     * @return true if the door is unlocked
     */
    public boolean isUnlocked() {
        return isUnlocked;
    }
    
    /**
     * Checks if this exit blocks movement.
     * @return true if exit is locked, false if unlocked
     */
    public boolean blocksMovement() {
        return !isUnlocked;
    }
    
    /**
     * Checks if an entity can pass through this exit.
     * @return true if exit is unlocked
     */
    public boolean isWalkable() {
        return isUnlocked;
    }
    
    /**
     * Checks if this is an exit point.
     * @return Always true as this is an exit
     */
    public boolean isExitPoint() {
        return true;
    }
    
    @Override
    public void render(SpriteBatch batch) {
        if (!active || textureRegion == null) return;
        
        batch.draw(textureRegion, position.x, position.y, width, height);
    }
    
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
    }
}
