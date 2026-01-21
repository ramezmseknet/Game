package de.tum.cit.fop.maze.entity.obstacle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import de.tum.cit.fop.maze.entity.AbstractEntity;
import de.tum.cit.fop.maze.entity.Player;
import de.tum.cit.fop.maze.util.Constants;

/**
 * A static trap obstacle that damages the player on contact.
 * Uses sprites from objects.png: row 4 (index 3), columns 5-11 (indices 4-10).
 * Fire animation loops through all frames continuously.
 */
public class Trap extends AbstractEntity implements Obstacle {
    
    private static final int OBJECT_TILE_SIZE = 16;
    private static final int FIRE_FRAME_COUNT = 7; // Columns 5-11 inclusive (indices 4-10)
    private static final float FIRE_FRAME_DURATION = 0.1f; // 10 FPS animation
    
    private int damage;
    private boolean lethal;
    private float cooldown;
    private float cooldownTimer;
    
    // Fire animation
    private float fireAnimTime;
    private boolean animationActive; // For damage flash effect
    private float damageFlashTime;
    
    // Trap sprites (fire animation frames)
    private static Texture objectsTexture;
    private static TextureRegion[] fireFrames;
    private static boolean texturesLoaded = false;
    
    /**
     * Creates a trap at the specified tile position.
     * @param tileX Tile X position
     * @param tileY Tile Y position
     */
    public Trap(int tileX, int tileY) {
        super(tileX, tileY, Constants.TILE_SIZE, Constants.TILE_SIZE);
        this.damage = 1;
        this.lethal = false;
        this.cooldown = 1.0f; // 1 second cooldown between damage
        this.cooldownTimer = 0f;
        this.fireAnimTime = 0f;
        this.animationActive = false;
        this.damageFlashTime = 0f;
        
        // Load fire animation textures
        loadTextures();
        
        // Set initial frame
        if (fireFrames != null && fireFrames.length > 0) {
            this.textureRegion = fireFrames[0];
        }
    }
    
    /**
     * Loads fire animation frames from objects.png.
     * Row 4 (index 3), columns 5-11 (indices 4-10).
     */
    private static void loadTextures() {
        if (texturesLoaded) return;
        
        try {
            objectsTexture = new Texture(Gdx.files.internal("objects.png"));
            fireFrames = new TextureRegion[FIRE_FRAME_COUNT];
            
            int row = 3; // Row 4 is index 3
            for (int i = 0; i < FIRE_FRAME_COUNT; i++) {
                int col = 4 + i; // Columns 5-11 are indices 4-10
                fireFrames[i] = new TextureRegion(
                    objectsTexture,
                    col * OBJECT_TILE_SIZE,
                    row * OBJECT_TILE_SIZE,
                    OBJECT_TILE_SIZE,
                    OBJECT_TILE_SIZE
                );
            }
            
            texturesLoaded = true;
            Gdx.app.log("Trap", "Loaded " + FIRE_FRAME_COUNT + " fire animation frames from objects.png");
        } catch (Exception e) {
            Gdx.app.error("Trap", "Failed to load fire textures: " + e.getMessage());
        }
    }
    
    @Override
    public void update(float deltaTime) {
        if (cooldownTimer > 0) {
            cooldownTimer -= deltaTime;
        }
        
        // Update fire animation - loops continuously
        fireAnimTime += deltaTime;
        if (fireFrames != null && fireFrames.length > 0) {
            int frameIndex = (int) (fireAnimTime / FIRE_FRAME_DURATION) % FIRE_FRAME_COUNT;
            this.textureRegion = fireFrames[frameIndex];
        }
        
        // Handle damage flash effect
        if (animationActive) {
            damageFlashTime += deltaTime;
            if (damageFlashTime >= 0.5f) {
                animationActive = false;
                damageFlashTime = 0f;
            }
        }
        
        super.update(deltaTime);
    }
    
    @Override
    public void render(SpriteBatch batch) {
        if (textureRegion != null && active) {
            // Flash when triggered
            if (animationActive) {
                batch.setColor(1f, 0.5f, 0.5f, 1f);
            }
            
            batch.draw(textureRegion, position.x, position.y, width, height);
            
            batch.setColor(Color.WHITE);
        }
    }
    
    @Override
    public int getDamage() {
        return damage;
    }
    
    @Override
    public void onContact(Player player) {
        if (canDamage() && !player.isInvincible()) {
            player.takeDamage(damage);
            cooldownTimer = cooldown;
            animationActive = true;
            damageFlashTime = 0f;
            // Could trigger TrapTriggeredEvent here
        }
    }
    
    @Override
    public boolean isLethal() {
        return lethal;
    }
    
    @Override
    public boolean canDamage() {
        return cooldownTimer <= 0;
    }
    
    // Setters for customization
    public void setDamage(int damage) {
        this.damage = damage;
    }
    
    public void setLethal(boolean lethal) {
        this.lethal = lethal;
    }
    
    public void setCooldown(float cooldown) {
        this.cooldown = cooldown;
    }
}
