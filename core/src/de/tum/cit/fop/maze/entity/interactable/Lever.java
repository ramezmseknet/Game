package de.tum.cit.fop.maze.entity.interactable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import de.tum.cit.fop.maze.entity.AbstractEntity;
import de.tum.cit.fop.maze.entity.Player;
import de.tum.cit.fop.maze.util.Constants;

/**
 * A lever that can be toggled between open and closed states.
 * The player can interact with the lever using the F key to toggle its state.
 * Unlike keys, levers are NOT collected - they remain in place and can be toggled repeatedly.
 * 
 * Sprites from objects.png:
 * - Row 10 (index 9), column 3 (index 2): closed lever
 * - Row 10 (index 9), column 4 (index 3): open lever
 */
public class Lever extends AbstractEntity {
    
    private static final int OBJECT_TILE_SIZE = 16;
    
    private final String leverId;
    private boolean isOpen;
    
    // Textures
    private static Texture objectsTexture;
    private static TextureRegion closedLeverTexture;
    private static TextureRegion openLeverTexture;
    private static boolean texturesLoaded = false;
    
    /**
     * Creates a lever at the specified tile position.
     * @param leverId Unique identifier for this lever
     * @param tileX Tile X position
     * @param tileY Tile Y position
     */
    public Lever(String leverId, int tileX, int tileY) {
        super(tileX, tileY, Constants.TILE_SIZE * 0.5f, Constants.TILE_SIZE * 0.5f);
        this.leverId = leverId;
        this.isOpen = false;  // Lever starts in closed state
        
        // Load textures once
        loadTextures();
    }
    
    /**
     * Loads lever textures from objects.png.
     * Row 10 (index 9), column 3 (index 2) for closed, column 4 (index 3) for open.
     */
    private static void loadTextures() {
        if (texturesLoaded) return;
        
        try {
            objectsTexture = new Texture(Gdx.files.internal("objects.png"));
            int row = 9;  // Row 10 is index 9
            
            // Column 3 (index 2) - closed lever
            closedLeverTexture = new TextureRegion(
                objectsTexture,
                2 * OBJECT_TILE_SIZE,
                row * OBJECT_TILE_SIZE,
                OBJECT_TILE_SIZE,
                OBJECT_TILE_SIZE
            );
            
            // Column 4 (index 3) - open lever
            openLeverTexture = new TextureRegion(
                objectsTexture,
                3 * OBJECT_TILE_SIZE,
                row * OBJECT_TILE_SIZE,
                OBJECT_TILE_SIZE,
                OBJECT_TILE_SIZE
            );
            
            texturesLoaded = true;
            Gdx.app.log("Lever", "Lever textures loaded successfully");
        } catch (Exception e) {
            Gdx.app.error("Lever", "Failed to load lever textures: " + e.getMessage());
        }
    }
    
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
    }
    
    /**
     * Renders the lever with appropriate texture (closed or open).
     * @param batch SpriteBatch to draw with
     */
    @Override
    public void render(SpriteBatch batch) {
        if (!active) return;
        
        TextureRegion texture = isOpen ? openLeverTexture : closedLeverTexture;
        if (texture != null) {
            batch.draw(texture, 
                position.x, 
                position.y, 
                width, 
                height);
        }
    }
    
    /**
     * Toggles the lever state between open and closed.
     * @return true if the state was toggled successfully
     */
    public boolean toggle() {
        isOpen = !isOpen;
        Gdx.app.log("Lever", "Lever " + leverId + " toggled to: " + (isOpen ? "OPEN" : "CLOSED"));
        return true;
    }
    
    /**
     * Checks if the player is close enough to interact with the lever.
     * @param player The player to check
     * @return true if player is within interaction range
     */
    public boolean isPlayerNearby(Player player) {
        float dx = position.x - player.getPosition().x;
        float dy = position.y - player.getPosition().y;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        return distance < Constants.TILE_SIZE * 1.5f; // Within 1.5 tiles
    }
    
    /**
     * Checks if the lever is in the open state.
     * @return true if the lever is open
     */
    public boolean isOpen() {
        return isOpen;
    }
    
    /**
     * Sets the lever state directly.
     * @param open true to set open, false to set closed
     */
    public void setOpen(boolean open) {
        this.isOpen = open;
    }
    
    /**
     * Gets the unique identifier for this lever.
     * @return The lever ID
     */
    public String getLeverId() {
        return leverId;
    }
    
    /**
     * Gets the closed lever texture.
     * @return Closed lever texture region
     */
    public static TextureRegion getClosedLeverTexture() {
        loadTextures();
        return closedLeverTexture;
    }
    
    /**
     * Gets the open lever texture.
     * @return Open lever texture region
     */
    public static TextureRegion getOpenLeverTexture() {
        loadTextures();
        return openLeverTexture;
    }
    
    /**
     * Disposes of shared textures. Call when game exits.
     */
    public static void disposeTextures() {
        if (objectsTexture != null) {
            objectsTexture.dispose();
            objectsTexture = null;
            texturesLoaded = false;
        }
    }
}
