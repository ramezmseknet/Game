package de.tum.cit.fop.maze.entity.enemy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import de.tum.cit.fop.maze.entity.Direction;

import java.util.EnumMap;
import java.util.Map;

/**
 * Enemy animation system extracted from mobs.png sprite sheet.
 * 
 * Layout (16x16 per frame):
 * - Row 5 (index 4), cols 1-3: going DOWN
 * - Row 6 (index 5), cols 1-3: going LEFT
 * - Row 7 (index 6), cols 1-3: going RIGHT
 * - Row 8 (index 7), cols 1-3: going UP
 */
public class EnemyAnimations {
    
    public static final int FRAME_WIDTH = 16;
    public static final int FRAME_HEIGHT = 16;
    
    private static final float WALK_FRAME_DURATION = 0.15f;
    
    private Texture spriteSheet;
    private Map<Direction, Animation<TextureRegion>> animations;
    private Map<Direction, TextureRegion> idleFrames;
    
    private Direction currentDirection;
    private float stateTime;
    private boolean isMoving;
    
    public EnemyAnimations(String spriteSheetPath) {
        this.spriteSheet = new Texture(Gdx.files.internal(spriteSheetPath));
        this.animations = new EnumMap<>(Direction.class);
        this.idleFrames = new EnumMap<>(Direction.class);
        this.currentDirection = Direction.DOWN;
        this.stateTime = 0f;
        this.isMoving = false;
        
        buildAnimations();
    }
    
    private void buildAnimations() {
        // Row indices (0-based): row 5 = index 4, row 6 = index 5, etc.
        // Columns 1-3 (0-based: cols 0-2)
        
        // Down: Row 5 (y = 4 * 16 = 64), cols 0-2
        buildDirectionAnimation(Direction.DOWN, 4, 0);
        
        // Left: Row 6 (y = 5 * 16 = 80), cols 0-2
        buildDirectionAnimation(Direction.LEFT, 5, 0);
        
        // Right: Row 7 (y = 6 * 16 = 96), cols 0-2
        buildDirectionAnimation(Direction.RIGHT, 6, 0);
        
        // Up: Row 8 (y = 7 * 16 = 112), cols 0-2
        buildDirectionAnimation(Direction.UP, 7, 0);
    }
    
    private void buildDirectionAnimation(Direction dir, int row, int startCol) {
        Array<TextureRegion> frames = new Array<>();
        
        // Extract 3 frames for this direction
        for (int i = 0; i < 3; i++) {
            int x = (startCol + i) * FRAME_WIDTH;
            int y = row * FRAME_HEIGHT;
            TextureRegion frame = new TextureRegion(spriteSheet, x, y, FRAME_WIDTH, FRAME_HEIGHT);
            frames.add(frame);
        }
        
        // Store idle frame (first frame)
        idleFrames.put(dir, frames.get(0));
        
        // Create looping animation
        Animation<TextureRegion> anim = new Animation<>(WALK_FRAME_DURATION, frames, Animation.PlayMode.LOOP);
        animations.put(dir, anim);
    }
    
    /**
     * Updates animation state.
     */
    public void update(float deltaTime, Direction direction, boolean moving) {
        stateTime += deltaTime;
        
        if (direction != null && direction != Direction.NONE) {
            currentDirection = direction;
        }
        
        this.isMoving = moving;
    }
    
    /**
     * Gets the current frame to render.
     */
    public TextureRegion getCurrentFrame() {
        if (!isMoving) {
            return idleFrames.get(currentDirection);
        }
        
        Animation<TextureRegion> anim = animations.get(currentDirection);
        if (anim != null) {
            return anim.getKeyFrame(stateTime);
        }
        
        return idleFrames.get(Direction.DOWN);
    }
    
    /**
     * Renders the current animation frame.
     */
    public void render(SpriteBatch batch, float x, float y, float width, float height) {
        TextureRegion frame = getCurrentFrame();
        if (frame != null) {
            batch.draw(frame, x, y, width, height);
        }
    }
    
    /**
     * Gets animation for a specific direction.
     */
    public Animation<TextureRegion> getAnimation(Direction direction) {
        return animations.get(direction);
    }
    
    /**
     * Gets idle frame for a specific direction.
     */
    public TextureRegion getIdleFrame(Direction direction) {
        return idleFrames.get(direction);
    }
    
    public Direction getCurrentDirection() {
        return currentDirection;
    }
    
    public void setDirection(Direction direction) {
        if (direction != null && direction != Direction.NONE) {
            this.currentDirection = direction;
        }
    }
    
    public void resetStateTime() {
        this.stateTime = 0f;
    }
    
    public void dispose() {
        if (spriteSheet != null) {
            spriteSheet.dispose();
        }
    }
}
