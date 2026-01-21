package de.tum.cit.fop.maze.entity.collectible;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import de.tum.cit.fop.maze.entity.AbstractEntity;
import de.tum.cit.fop.maze.entity.Player;
import de.tum.cit.fop.maze.util.Constants;

/**
 * A heart collectible that restores one life.
 * Sprites from objects.png row 3 (index 2), items 1-4 (indices 0-3).
 */
public class Heart extends AbstractEntity implements Collectible {
    
    private static final int OBJECT_TILE_SIZE = 16;
    
    private boolean collected;
    private float pulseScale;
    private float pulseTime;
    private float animTime;
    
    // Textures
    private static Texture objectsTexture;
    private static TextureRegion[] heartFrames;
    private static boolean texturesLoaded = false;
    
    /**
     * Creates a heart at the specified tile position.
     * @param tileX Tile X position
     * @param tileY Tile Y position
     */
    public Heart(int tileX, int tileY) {
        super(tileX, tileY, Constants.TILE_SIZE * 0.6f, Constants.TILE_SIZE * 0.6f);
        this.collected = false;
        this.pulseScale = 1f;
        this.pulseTime = 0f;
        this.animTime = 0f;
        
        // Load textures once
        loadTextures();
    }
    
    /**
     * Loads heart textures from objects.png.
     * Row 10 (index 9), columns 5-9 (indices 4-8) for maze hearts.
     */
    private static void loadTextures() {
        if (texturesLoaded) return;
        
        try {
            objectsTexture = new Texture(Gdx.files.internal("objects.png"));
            int row = 9;  // Row 10 is index 9
            
            // Columns 5-9 (indices 4-8) - heart animation frames
            heartFrames = new TextureRegion[5];
            for (int i = 0; i < 5; i++) {
                heartFrames[i] = new TextureRegion(
                    objectsTexture,
                    (4 + i) * OBJECT_TILE_SIZE,  // columns 5-9 are indices 4-8
                    row * OBJECT_TILE_SIZE,
                    OBJECT_TILE_SIZE,
                    OBJECT_TILE_SIZE
                );
            }
            
            texturesLoaded = true;
        } catch (Exception e) {
            Gdx.app.error("Heart", "Failed to load heart textures: " + e.getMessage());
        }
    }
    
    @Override
    public void update(float deltaTime) {
        if (!collected) {
            // Pulsing animation
            pulseTime += deltaTime * 4f;
            pulseScale = 1f + (float) Math.sin(pulseTime) * 0.1f;
            
            // Frame animation
            animTime += deltaTime;
        }
        super.update(deltaTime);
    }
    
    /**
     * Renders the heart with texture.
     * @param batch SpriteBatch to draw with
     */
    public void render(SpriteBatch batch) {
        if (collected) return;
        
        if (heartFrames != null && heartFrames.length > 0) {
            // Animate through frames
            int frameIndex = (int) ((animTime * 4f) % heartFrames.length);
            TextureRegion texture = heartFrames[frameIndex];
            
            float scaledWidth = width * pulseScale;
            float scaledHeight = height * pulseScale;
            float offsetX = (width - scaledWidth) / 2f;
            float offsetY = (height - scaledHeight) / 2f;
            
            batch.draw(texture, 
                position.x + offsetX, 
                position.y + offsetY, 
                scaledWidth, 
                scaledHeight);
        }
    }
    
    @Override
    public void onCollect(Player player) {
        if (!collected) {
            collected = true;
            active = false;
            player.heal(1);
            player.addScore(50);
            // Could trigger HeartCollectedEvent here
        }
    }
    
    @Override
    public boolean isCollected() {
        return collected;
    }
    
    @Override
    public CollectibleType getType() {
        return CollectibleType.HEART;
    }
    
    /**
     * Gets the scale for pulsing animation.
     * @return Scale multiplier
     */
    public float getPulseScale() {
        return pulseScale;
    }
    
    /**
     * Gets a heart texture frame.
     * @param index Frame index (0-3)
     * @return Heart texture region
     */
    public static TextureRegion getHeartFrame(int index) {
        loadTextures();
        if (heartFrames != null && index >= 0 && index < heartFrames.length) {
            return heartFrames[index];
        }
        return null;
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
