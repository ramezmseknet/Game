package de.tum.cit.fop.maze.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

import java.util.EnumMap;
import java.util.Map;

/**
 * Comprehensive character animation system extracted from sprite sheet.
 * Handles movement and combat animations for all 4 directions.
 * 
 * Sprite sheet layout (16x32 per frame, 256x256 total):
 * - Rows 1-4: Movement animations (no weapon)
 * - Rows 5-8: Combat animations (with weapon)
 * 
 * Row 1: Down idle + walk, Up idle + walk
 * Row 2: Left idle + walk, Right idle + walk
 * Row 3: Down run/extra, Up run/extra
 * Row 4: Left run/extra, Right run/extra
 * Row 5-8: Combat versions of above
 */
public class CharacterAnimations {
    
    public static final int FRAME_WIDTH = 16;
    public static final int FRAME_HEIGHT = 32;
    public static final int SHEET_COLUMNS = 16;
    public static final int SHEET_ROWS = 8;
    
    private static final float WALK_FRAME_DURATION = 0.12f;
    private static final float RUN_FRAME_DURATION = 0.08f;
    private static final float ATTACK_FRAME_DURATION = 0.1f;
    private static final float IDLE_FRAME_DURATION = 0.3f;
    
    /**
     * Animation types available.
     */
    public enum AnimationType {
        IDLE,
        WALK,
        RUN,
        ATTACK_1,
        ATTACK_2
    }
    
    private Texture spriteSheet;
    private TextureRegion[][] allFrames;
    
    private Map<Direction, Map<AnimationType, Animation<TextureRegion>>> movementAnimations;
    private Map<Direction, Map<AnimationType, Animation<TextureRegion>>> combatAnimations;
    
    private Map<Direction, TextureRegion> idleFrames;
    private Map<Direction, TextureRegion> combatIdleFrames;
    
    private Direction currentDirection;
    private AnimationType currentAnimation;
    private boolean inCombatMode;
    private boolean isAttacking;
    private float stateTime;
    private float attackTime;
    
    private boolean flipLeftFromRight;
    
    public CharacterAnimations(String spriteSheetPath) {
        this.spriteSheet = new Texture(Gdx.files.internal(spriteSheetPath));
        this.currentDirection = Direction.DOWN;
        this.currentAnimation = AnimationType.IDLE;
        this.inCombatMode = false;
        this.isAttacking = false;
        this.stateTime = 0f;
        this.attackTime = 0f;
        this.flipLeftFromRight = false;
        
        initializeAnimationMaps();
        extractFramesFromSheet();
        buildAnimations();
    }
    
    private void initializeAnimationMaps() {
        movementAnimations = new EnumMap<>(Direction.class);
        combatAnimations = new EnumMap<>(Direction.class);
        idleFrames = new EnumMap<>(Direction.class);
        combatIdleFrames = new EnumMap<>(Direction.class);
        
        for (Direction dir : new Direction[]{Direction.DOWN, Direction.UP, Direction.LEFT, Direction.RIGHT}) {
            movementAnimations.put(dir, new EnumMap<>(AnimationType.class));
            combatAnimations.put(dir, new EnumMap<>(AnimationType.class));
        }
    }
    
    /**
     * Extracts all frames from the sprite sheet into a 2D array.
     */
    private void extractFramesFromSheet() {
        allFrames = new TextureRegion[SHEET_ROWS][SHEET_COLUMNS];
        
        for (int row = 0; row < SHEET_ROWS; row++) {
            for (int col = 0; col < SHEET_COLUMNS; col++) {
                allFrames[row][col] = new TextureRegion(
                    spriteSheet,
                    col * FRAME_WIDTH,
                    row * FRAME_HEIGHT,
                    FRAME_WIDTH,
                    FRAME_HEIGHT
                );
            }
        }
    }
    
    /**
     * Builds all animations from the extracted frames.
     * 
     * Layout mapping (based on user's sprite sheet):
     * 
     * ROW 0 (Movement): First 4 frames = DOWN
     * ROW 1 (Movement): First 4 frames = RIGHT
     * ROW 2 (Movement): First 4 frames = UP (back)
     * ROW 3 (Movement): First 4 frames = LEFT
     * 
     * ROWS 4-7 (Combat): Same layout but with weapon
     */
    private void buildAnimations() {

        buildDirectionalAnimations(Direction.DOWN, 0, 0, false);
        
        buildDirectionalAnimations(Direction.RIGHT, 1, 0, false);
        
        buildDirectionalAnimations(Direction.UP, 2, 0, false);
        
        buildDirectionalAnimations(Direction.LEFT, 3, 0, false);
        
        buildCombatAnimations(Direction.DOWN, 4, 0);
        
        buildCombatAnimations(Direction.RIGHT, 5, 0);
        
        buildCombatAnimations(Direction.UP, 6, 0);
        
        buildCombatAnimations(Direction.LEFT, 7, 0);
    }
    
    /**
     * Builds idle and walk animations for a direction.
     * Uses first 4 frames: frame 0 = idle, frames 0-3 = walk loop
     */
    private void buildDirectionalAnimations(Direction dir, int row, int startCol, boolean flip) {
        TextureRegion idleFrame = allFrames[row][startCol];
        if (flip) {
            idleFrame = new TextureRegion(idleFrame);
            idleFrame.flip(true, false);
        }
        idleFrames.put(dir, idleFrame);
        
        Array<TextureRegion> idleArray = new Array<>();
        idleArray.add(idleFrame);
        Animation<TextureRegion> idleAnim = new Animation<>(IDLE_FRAME_DURATION, idleArray);
        movementAnimations.get(dir).put(AnimationType.IDLE, idleAnim);
        
        Array<TextureRegion> walkArray = new Array<>();
        for (int i = 0; i < 4; i++) {
            TextureRegion frame = allFrames[row][startCol + i];
            if (flip) {
                frame = new TextureRegion(frame);
                frame.flip(true, false);
            }
            walkArray.add(frame);
        }
        Animation<TextureRegion> walkAnim = new Animation<>(WALK_FRAME_DURATION, walkArray, Animation.PlayMode.LOOP);
        movementAnimations.get(dir).put(AnimationType.WALK, walkAnim);
        
        Animation<TextureRegion> runAnim = new Animation<>(RUN_FRAME_DURATION, walkArray, Animation.PlayMode.LOOP);
        movementAnimations.get(dir).put(AnimationType.RUN, runAnim);
    }
    
    /**
     * Builds combat idle and attack animations for a direction.
     * Uses first 4 frames: frame 0 = idle, frames 0-3 = attack
     */
    private void buildCombatAnimations(Direction dir, int row, int startCol) {
        combatIdleFrames.put(dir, allFrames[row][startCol]);
        
        Array<TextureRegion> idleArray = new Array<>();
        idleArray.add(allFrames[row][startCol]);
        Animation<TextureRegion> idleAnim = new Animation<>(IDLE_FRAME_DURATION, idleArray);
        combatAnimations.get(dir).put(AnimationType.IDLE, idleAnim);
        
        Array<TextureRegion> attackArray = new Array<>();
        for (int i = 0; i < 4; i++) {
            attackArray.add(allFrames[row][startCol + i]);
        }
        Animation<TextureRegion> attackAnim = new Animation<>(ATTACK_FRAME_DURATION, attackArray, Animation.PlayMode.NORMAL);
        combatAnimations.get(dir).put(AnimationType.ATTACK_1, attackAnim);
        
        Animation<TextureRegion> attack2Anim = new Animation<>(ATTACK_FRAME_DURATION, attackArray, Animation.PlayMode.NORMAL);
        combatAnimations.get(dir).put(AnimationType.ATTACK_2, attack2Anim);
    }
    
    /**
     * Updates animation state.
     * @param deltaTime Frame delta
     * @param direction Current facing direction
     * @param isMoving Whether the character is moving
     * @param isRunning Whether the character is running
     */
    public void update(float deltaTime, Direction direction, boolean isMoving, boolean isRunning) {
        stateTime += deltaTime;
        
        if (direction != Direction.NONE) {
            currentDirection = direction;
        }
        
        if (isAttacking) {
            attackTime += deltaTime;
            Animation<TextureRegion> attackAnim = getAttackAnimation();
            if (attackAnim != null && attackAnim.isAnimationFinished(attackTime)) {
                isAttacking = false;
                attackTime = 0f;
            }
        }
        
        if (!isAttacking) {
            if (isMoving) {
                currentAnimation = isRunning ? AnimationType.RUN : AnimationType.WALK;
            } else {
                currentAnimation = AnimationType.IDLE;
            }
        }
    }
    
    /**
     * Triggers an attack animation.
     * @param attackType 1 for ATTACK_1, 2 for ATTACK_2
     */
    public void attack(int attackType) {
        if (!isAttacking) {
            isAttacking = true;
            attackTime = 0f;
            inCombatMode = true;
            currentAnimation = (attackType == 2) ? AnimationType.ATTACK_2 : AnimationType.ATTACK_1;
        }
    }
    
    /**
     * Gets the current frame to render.
     */
    public TextureRegion getCurrentFrame() {
        if (isAttacking) {
            Animation<TextureRegion> attackAnim = getAttackAnimation();
            if (attackAnim != null) {
                return attackAnim.getKeyFrame(attackTime);
            }
        }
        
        Map<Direction, Map<AnimationType, Animation<TextureRegion>>> animMap = 
            inCombatMode ? combatAnimations : movementAnimations;
        
        Map<AnimationType, Animation<TextureRegion>> dirAnims = animMap.get(currentDirection);
        if (dirAnims == null) {
            return idleFrames.get(Direction.DOWN);
        }
        
        Animation<TextureRegion> anim = dirAnims.get(currentAnimation);
        if (anim == null) {
            anim = dirAnims.get(AnimationType.IDLE);
        }
        
        if (anim != null) {
            return anim.getKeyFrame(stateTime);
        }
        
        return idleFrames.get(currentDirection);
    }
    
    private Animation<TextureRegion> getAttackAnimation() {
        Map<AnimationType, Animation<TextureRegion>> dirAnims = combatAnimations.get(currentDirection);
        if (dirAnims != null) {
            return dirAnims.get(currentAnimation);
        }
        return null;
    }
    
    /**
     * Renders the current animation frame.
     */
    public void render(SpriteBatch batch, float x, float y) {
        render(batch, x, y, FRAME_WIDTH, FRAME_HEIGHT);
    }
    
    /**
     * Renders the current animation frame with custom size.
     */
    public void render(SpriteBatch batch, float x, float y, float width, float height) {
        TextureRegion frame = getCurrentFrame();
        if (frame != null) {
            batch.draw(frame, x, y, width, height);
        }
    }
    
    /**
     * Gets idle frame for a specific direction.
     */
    public TextureRegion getIdleFrame(Direction direction) {
        return idleFrames.get(direction);
    }
    
    /**
     * Gets combat idle frame for a specific direction.
     */
    public TextureRegion getCombatIdleFrame(Direction direction) {
        return combatIdleFrames.get(direction);
    }
    
    /**
     * Gets walk animation for a specific direction.
     */
    public Animation<TextureRegion> getWalkAnimation(Direction direction) {
        return movementAnimations.get(direction).get(AnimationType.WALK);
    }
    
    /**
     * Gets run animation for a specific direction.
     */
    public Animation<TextureRegion> getRunAnimation(Direction direction) {
        return movementAnimations.get(direction).get(AnimationType.RUN);
    }
    
    /**
     * Gets attack animation for a specific direction.
     */
    public Animation<TextureRegion> getAttackAnimation(Direction direction, int attackNum) {
        AnimationType type = (attackNum == 2) ? AnimationType.ATTACK_2 : AnimationType.ATTACK_1;
        return combatAnimations.get(direction).get(type);
    }
    
    // State getters
    public Direction getCurrentDirection() { return currentDirection; }
    public AnimationType getCurrentAnimationType() { return currentAnimation; }
    public boolean isAttacking() { return isAttacking; }
    public boolean isInCombatMode() { return inCombatMode; }
    public float getStateTime() { return stateTime; }
    
    // State setters
    public void setDirection(Direction direction) { 
        if (direction != Direction.NONE) {
            this.currentDirection = direction; 
        }
    }
    public void setCombatMode(boolean combat) { this.inCombatMode = combat; }
    public void resetStateTime() { this.stateTime = 0f; }
    
    /**
     * Disposes of the sprite sheet texture.
     */
    public void dispose() {
        if (spriteSheet != null) {
            spriteSheet.dispose();
        }
    }
}
