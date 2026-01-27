package de.tum.cit.fop.maze.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

import java.util.EnumMap;
import java.util.Map;

/**
 * Manages animation sets for different entity types.
 * Loads and caches animations from sprite sheets.
 */
public class AnimationManager {
    
    private static AnimationManager instance;
    
    private Map<EntityAnimationType, Animation<TextureRegion>> animations;
    private Map<EntityAnimationType, Map<Direction, Animation<TextureRegion>>> directionalAnimations;
    
    private Array<Texture> loadedTextures;
    
    public enum EntityAnimationType {
        PLAYER_WALK,
        PLAYER_IDLE,
        ENEMY_GHOST,
        ENEMY_ZOMBIE,
        ENEMY_SLIME,
        ENEMY_BAT,
        KEY_SPIN,
        HEART_PULSE,
        TRAP_ACTIVATE,
        POWERUP_GLOW
    }
    
    private AnimationManager() {
        animations = new EnumMap<>(EntityAnimationType.class);
        directionalAnimations = new EnumMap<>(EntityAnimationType.class);
        loadedTextures = new Array<>();
    }
    
    public static AnimationManager getInstance() {
        if (instance == null) {
            instance = new AnimationManager();
        }
        return instance;
    }
    
    /**
     * Loads a simple animation from a sprite sheet.
     * @param type Animation type identifier
     * @param texturePath Path to the texture file
     * @param frameWidth Width of each frame
     * @param frameHeight Height of each frame
     * @param frameCount Number of frames
     * @param frameDuration Duration of each frame
     * @param row Row in the sprite sheet (0-indexed)
     */
    public void loadAnimation(EntityAnimationType type, String texturePath, 
            int frameWidth, int frameHeight, int frameCount, float frameDuration, int row) {
        try {
            Texture texture = new Texture(Gdx.files.internal(texturePath));
            loadedTextures.add(texture);
            
            Array<TextureRegion> frames = new Array<>();
            for (int i = 0; i < frameCount; i++) {
                frames.add(new TextureRegion(texture, i * frameWidth, row * frameHeight, frameWidth, frameHeight));
            }
            
            Animation<TextureRegion> anim = new Animation<>(frameDuration, frames, Animation.PlayMode.LOOP);
            animations.put(type, anim);
            
            Gdx.app.log("AnimationManager", "Loaded animation: " + type);
        } catch (Exception e) {
            Gdx.app.error("AnimationManager", "Failed to load animation " + type + ": " + e.getMessage());
        }
    }
    
    /**
     * Loads directional animations (4 directions from 4 rows).
     * @param type Animation type identifier
     * @param texturePath Path to the texture file
     * @param frameWidth Width of each frame
     * @param frameHeight Height of each frame
     * @param frameCount Number of frames per direction
     * @param frameDuration Duration of each frame
     */
    public void loadDirectionalAnimation(EntityAnimationType type, String texturePath,
            int frameWidth, int frameHeight, int frameCount, float frameDuration) {
        try {
            Texture texture = new Texture(Gdx.files.internal(texturePath));
            loadedTextures.add(texture);
            
            Map<Direction, Animation<TextureRegion>> dirMap = new EnumMap<>(Direction.class);
            
            Direction[] directions = {Direction.DOWN, Direction.LEFT, Direction.RIGHT, Direction.UP};
            
            for (int row = 0; row < directions.length; row++) {
                Array<TextureRegion> frames = new Array<>();
                for (int col = 0; col < frameCount; col++) {
                    frames.add(new TextureRegion(texture, col * frameWidth, row * frameHeight, frameWidth, frameHeight));
                }
                Animation<TextureRegion> anim = new Animation<>(frameDuration, frames, Animation.PlayMode.LOOP);
                dirMap.put(directions[row], anim);
            }
            
            directionalAnimations.put(type, dirMap);
            
            Gdx.app.log("AnimationManager", "Loaded directional animation: " + type);
        } catch (Exception e) {
            Gdx.app.error("AnimationManager", "Failed to load directional animation " + type + ": " + e.getMessage());
        }
    }
    
    /**
     * Creates a color-tinted version of an existing animation.
     */
    public Animation<TextureRegion> createTintedAnimation(EntityAnimationType baseType, 
            com.badlogic.gdx.graphics.Color tint) {
        Animation<TextureRegion> base = animations.get(baseType);
        if (base == null) return null;
        
        return base;
    }
    
    /**
     * Gets a simple animation.
     */
    public Animation<TextureRegion> getAnimation(EntityAnimationType type) {
        return animations.get(type);
    }
    
    /**
     * Gets a directional animation.
     */
    public Animation<TextureRegion> getAnimation(EntityAnimationType type, Direction direction) {
        Map<Direction, Animation<TextureRegion>> dirMap = directionalAnimations.get(type);
        if (dirMap == null) return null;
        return dirMap.get(direction);
    }
    
    /**
     * Gets the full directional animation map.
     */
    public Map<Direction, Animation<TextureRegion>> getDirectionalAnimations(EntityAnimationType type) {
        return directionalAnimations.get(type);
    }
    
    /**
     * Checks if an animation is loaded.
     */
    public boolean hasAnimation(EntityAnimationType type) {
        return animations.containsKey(type) || directionalAnimations.containsKey(type);
    }
    
    /**
     * Creates a placeholder animation from a solid color.
     */
    public Animation<TextureRegion> createPlaceholder(int width, int height, com.badlogic.gdx.graphics.Color color) {
        com.badlogic.gdx.graphics.Pixmap pixmap = new com.badlogic.gdx.graphics.Pixmap(width, height, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        loadedTextures.add(texture);
        
        TextureRegion region = new TextureRegion(texture);
        Array<TextureRegion> frames = new Array<>();
        frames.add(region);
        
        return new Animation<>(1f, frames);
    }
    
    /**
     * Disposes all loaded textures.
     */
    public void dispose() {
        for (Texture texture : loadedTextures) {
            texture.dispose();
        }
        loadedTextures.clear();
        animations.clear();
        directionalAnimations.clear();
        instance = null;
    }
}
