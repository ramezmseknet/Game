package de.tum.cit.fop.maze.entity;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import de.tum.cit.fop.maze.util.Constants;
import de.tum.cit.fop.maze.world.Maze;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * The player entity controlled by user input.
 * Handles movement, lives, collected keys, and power-up states.
 */
public class Player extends AbstractEntity {
    
    private CharacterAnimations characterAnimations;
    
    private Map<Direction, Animation<TextureRegion>> animations;
    private float stateTime;

    private int lives;
    private int maxLives;
    private float speed;
    private float baseSpeed;
    
    private Set<String> collectedKeys;
    private int keysRequired;
    
    private boolean hasShield;
    private float shieldTimer;
    private boolean hasSpeedBoost;
    private float speedBoostTimer;
    private float speedBoostMultiplier;
    
    private boolean isInvincible;
    private float invincibilityTimer;
    private boolean flashRed;
    private float flashTimer;
    
    private Direction facingDirection;
    private boolean isMoving;
    private boolean isRunning;
    private Direction currentMoveDirection;
    
    private int score;
    private int coins;
    private int gems;
    private int totalCoinsCollected;
    private int totalGemsCollected;
    
    private boolean hasFreeze;
    private float freezeTimer;
    private boolean hasMagnet;
    private float magnetTimer;
    private boolean hasDoublePoints;
    private float doublePointsTimer;
    private boolean hasInvisibility;
    private float invisibilityTimer;
    
    private float cornerForgivenessPadding;
    private float cornerSnapDistance;
    private float cornerSnapSpeed;
    
    /**
     * Creates a player at the specified tile position.
     * @param tileX Starting tile X
     * @param tileY Starting tile Y
     */
    public Player(int tileX, int tileY) {
        super(tileX, tileY, Constants.DEFAULT_PLAYER_WIDTH, Constants.DEFAULT_PLAYER_HEIGHT);
        
        this.characterAnimations = null;
        
        this.animations = new EnumMap<>(Direction.class);
        this.stateTime = 0f;
        
        this.lives = Constants.DEFAULT_PLAYER_LIVES;
        this.maxLives = Constants.DEFAULT_PLAYER_LIVES;
        this.baseSpeed = Constants.PLAYER_WALK_SPEED;
        this.speed = baseSpeed;
        
        this.collectedKeys = new HashSet<>();
        this.keysRequired = 0;
        
        this.hasShield = false;
        this.shieldTimer = 0f;
        this.hasSpeedBoost = false;
        this.speedBoostTimer = 0f;
        this.speedBoostMultiplier = Constants.PLAYER_RUN_MULTIPLIER;
        
        this.isInvincible = false;
        this.invincibilityTimer = 0f;
        this.flashRed = false;
        this.flashTimer = 0f;
        
        this.facingDirection = Direction.DOWN;
        this.isMoving = false;
        this.isRunning = false;
        this.currentMoveDirection = Direction.NONE;
        
        this.score = 0;
        this.coins = 0;
        this.gems = 0;
        this.totalCoinsCollected = 0;
        this.totalGemsCollected = 0;
        
        this.hasFreeze = false;
        this.freezeTimer = 0f;
        this.hasMagnet = false;
        this.magnetTimer = 0f;
        this.hasDoublePoints = false;
        this.doublePointsTimer = 0f;
        this.hasInvisibility = false;
        this.invisibilityTimer = 0f;
        
        this.cornerForgivenessPadding = Constants.CORNER_FORGIVENESS_PADDING;
        this.cornerSnapDistance = Constants.CORNER_SNAP_DISTANCE;
        this.cornerSnapSpeed = Constants.CORNER_SNAP_SPEED;
    }
    
    @Override
    public void update(float deltaTime) {
        stateTime += deltaTime;
        
        if (characterAnimations != null) {
            characterAnimations.update(deltaTime, facingDirection, isMoving, isRunning);
        }
        
        if (hasFreeze) {
            freezeTimer -= deltaTime;
            if (freezeTimer <= 0) hasFreeze = false;
        }
        if (hasMagnet) {
            magnetTimer -= deltaTime;
            if (magnetTimer <= 0) hasMagnet = false;
        }
        if (hasDoublePoints) {
            doublePointsTimer -= deltaTime;
            if (doublePointsTimer <= 0) hasDoublePoints = false;
        }
        if (hasInvisibility) {
            invisibilityTimer -= deltaTime;
            if (invisibilityTimer <= 0) hasInvisibility = false;
        }
        
        if (isInvincible) {
            invincibilityTimer -= deltaTime;
            if (invincibilityTimer <= 0) {
                isInvincible = false;
                flashRed = false;
            } else {
                flashTimer += deltaTime;
                if (flashTimer >= 0.1f) {
                    flashRed = !flashRed;
                    flashTimer = 0f;
                }
            }
        }
        
        if (hasShield) {
            shieldTimer -= deltaTime;
            if (shieldTimer <= 0) {
                hasShield = false;
            }
        }
        
        if (hasSpeedBoost) {
            speedBoostTimer -= deltaTime;
            if (speedBoostTimer <= 0) {
                hasSpeedBoost = false;
                updateSpeed();
            }
        }
        
        if (!isMoving && characterAnimations == null) {
            stateTime = 0;
        }
        
        super.update(deltaTime);
    }
    
    @Override
    public void render(SpriteBatch batch) {
        if (!active) return;
        
        if (flashRed) {
            batch.setColor(1f, 0.3f, 0.3f, 1f);
        } else if (hasShield) {
            batch.setColor(0.5f, 0.8f, 1f, 1f);
        } else if (hasSpeedBoost) {
            batch.setColor(1f, 1f, 0.5f, 1f);
        } else if (hasInvisibility) {
            batch.setColor(1f, 1f, 1f, 0.4f);
        }
        
        if (characterAnimations != null) {
            characterAnimations.render(batch, position.x, position.y, width, height);
        } else {
            TextureRegion frame = getCurrentFrame();
            if (frame != null) {
                batch.draw(frame, position.x, position.y, width, height);
            }
        }
        
        batch.setColor(Color.WHITE);
    }
    
    /**
     * Gets the current animation frame based on direction and movement.
     * Uses new animation system if available, otherwise legacy.
     * @return The current texture region to draw
     */
    private TextureRegion getCurrentFrame() {
        if (characterAnimations != null) {
            return characterAnimations.getCurrentFrame();
        }
        
        Animation<TextureRegion> anim = animations.get(facingDirection);
        if (anim != null) {
            return anim.getKeyFrame(stateTime, true);
        }
        return textureRegion;
    }
    
    /**
     * Triggers an attack animation.
     * @param attackType 1 for primary attack, 2 for secondary
     */
    public void attack(int attackType) {
        if (characterAnimations != null) {
            characterAnimations.attack(attackType);
        }
    }
    
    /**
     * Triggers primary attack animation.
     */
    public void attack() {
        attack(1);
    }
    
    /**
     * Checks if player is currently attacking.
     * @return true if attack animation is playing
     */
    public boolean isAttacking() {
        return characterAnimations != null && characterAnimations.isAttacking();
    }
    
    /**
     * Attempts to move the player in a direction.
     * @param direction The direction to move
     * @param maze The maze for collision checking
     * @return true if movement was successful
     */
    public boolean tryMove(Direction direction, Maze maze) {
        return tryMove(direction, maze, false);
    }
    
    /**
     * Attempts to move the player in a direction, considering door state.
     * Sets the movement direction for continuous free movement.
     * @param direction The direction to move
     * @param maze The maze for collision checking (used in updateMovement)
     * @param doorUnlocked Whether the exit door is unlocked
     * @return true if a direction is set
     */
    public boolean tryMove(Direction direction, Maze maze, boolean doorUnlocked) {
        if (direction == Direction.NONE) {
            currentMoveDirection = Direction.NONE;
            isMoving = false;
            return false;
        }
        
        facingDirection = direction;
        currentMoveDirection = direction;
        return true;
    }
    
    /**
     * Updates free movement based on current direction and deltaTime.
     * Player moves continuously as long as a direction key is held.
     * Includes corner forgiveness for smooth corridor entry.
     * @param deltaTime Frame delta time
     * @param maze The maze for collision checking
     * @param doorUnlocked Whether the exit door is unlocked
     */
    public void updateMovement(float deltaTime, Maze maze, boolean doorUnlocked) {
        if (currentMoveDirection == Direction.NONE) {
            isMoving = false;
            return;
        }
        
        float moveDistance = speed * deltaTime;
        float moveX = currentMoveDirection.dx * moveDistance;
        float moveY = currentMoveDirection.dy * moveDistance;
        
        float newX = position.x + moveX;
        float newY = position.y + moveY;
        
        boolean canMove = checkCollisionWithForgiveness(newX, newY, maze, doorUnlocked);
        
        if (canMove) {
            position.x = newX;
            position.y = newY;
            bounds.setPosition(position);
            isMoving = true;
            
            float centerX = newX + width / 2f;
            float centerY = newY + height / 2f;
            tileX = (int) (centerX / Constants.TILE_SIZE);
            tileY = (int) (centerY / Constants.TILE_SIZE);
        } else {
            boolean aligned = tryCornerForgiveness(deltaTime, moveX, moveY, maze, doorUnlocked);
            
            if (!aligned) {
                tryWallSliding(moveX, moveY, maze, doorUnlocked);
            }
        }
    }
    
    /**
     * Checks collision with configurable padding for leniency.
     * Uses a slightly smaller collision box to allow near-misses.
     */
    private boolean checkCollisionWithForgiveness(float newX, float newY, Maze maze, boolean doorUnlocked) {
        float centerX = newX + width / 2f;
        float centerY = newY + height / 2f;
        int newTileX = (int) (centerX / Constants.TILE_SIZE);
        int newTileY = (int) (centerY / Constants.TILE_SIZE);
        
        boolean canMove = maze.isWalkable(newTileX, newTileY, doorUnlocked);
        
        if (canMove) {
            float padding = cornerForgivenessPadding;
            int cornerTileX, cornerTileY;
            
            cornerTileX = (int) ((newX + padding) / Constants.TILE_SIZE);
            cornerTileY = (int) ((newY + height - padding) / Constants.TILE_SIZE);
            canMove = canMove && maze.isWalkable(cornerTileX, cornerTileY, doorUnlocked);
            
            cornerTileX = (int) ((newX + width - padding) / Constants.TILE_SIZE);
            cornerTileY = (int) ((newY + height - padding) / Constants.TILE_SIZE);
            canMove = canMove && maze.isWalkable(cornerTileX, cornerTileY, doorUnlocked);
            
            cornerTileX = (int) ((newX + padding) / Constants.TILE_SIZE);
            cornerTileY = (int) ((newY + padding) / Constants.TILE_SIZE);
            canMove = canMove && maze.isWalkable(cornerTileX, cornerTileY, doorUnlocked);
            
            cornerTileX = (int) ((newX + width - padding) / Constants.TILE_SIZE);
            cornerTileY = (int) ((newY + padding) / Constants.TILE_SIZE);
            canMove = canMove && maze.isWalkable(cornerTileX, cornerTileY, doorUnlocked);
        }
        
        return canMove;
    }
    
    /**
     * Attempts corner forgiveness by detecting corridor entrances and auto-aligning.
     * When player is near a corridor but slightly misaligned, nudge them toward the center.
     * @return true if alignment was applied and movement occurred
     */
    private boolean tryCornerForgiveness(float deltaTime, float moveX, float moveY, Maze maze, boolean doorUnlocked) {
        boolean movingHorizontal = Math.abs(moveX) > Math.abs(moveY);
        
        if (movingHorizontal && moveX != 0) {
            return tryHorizontalCornerForgiveness(deltaTime, moveX, maze, doorUnlocked);
        } else if (moveY != 0) {
            return tryVerticalCornerForgiveness(deltaTime, moveY, maze, doorUnlocked);
        }
        
        return false;
    }
    
    /**
     * Tries to align player when moving horizontally into a corridor.
     * Detects if there's a walkable corridor ahead and nudges player to align.
     */
    private boolean tryHorizontalCornerForgiveness(float deltaTime, float moveX, Maze maze, boolean doorUnlocked) {
        float playerCenterY = position.y + height / 2f;
        int playerTileY = (int) (playerCenterY / Constants.TILE_SIZE);
        
        int targetTileX;
        if (moveX > 0) {
            targetTileX = (int) ((position.x + width + Math.abs(moveX)) / Constants.TILE_SIZE);
        } else {
            targetTileX = (int) ((position.x - Math.abs(moveX)) / Constants.TILE_SIZE);
        }
        
        float tileCenterY = playerTileY * Constants.TILE_SIZE + Constants.TILE_SIZE / 2f;
        float offsetFromCenter = playerCenterY - tileCenterY;
        
        if (maze.isWalkable(targetTileX, playerTileY, doorUnlocked)) {
            if (Math.abs(offsetFromCenter) <= cornerSnapDistance && Math.abs(offsetFromCenter) > 1f) {
                float nudgeAmount = cornerSnapSpeed * deltaTime * Constants.TILE_SIZE;
                float idealY = tileCenterY - height / 2f;
                
                if (position.y < idealY) {
                    position.y = Math.min(position.y + nudgeAmount, idealY);
                } else {
                    position.y = Math.max(position.y - nudgeAmount, idealY);
                }
                
                float newX = position.x + moveX;
                if (checkCollisionWithForgiveness(newX, position.y, maze, doorUnlocked)) {
                    position.x = newX;
                    bounds.setPosition(position);
                    isMoving = true;
                    updateTilePosition();
                    return true;
                }
            }
        }
        
        for (int yOffset = -1; yOffset <= 1; yOffset++) {
            if (yOffset == 0) continue;
            
            int checkTileY = playerTileY + yOffset;
            if (maze.isWalkable(targetTileX, checkTileY, doorUnlocked)) {
                float corridorCenterY = checkTileY * Constants.TILE_SIZE + Constants.TILE_SIZE / 2f;
                float distToCorridorCenter = Math.abs(playerCenterY - corridorCenterY);
                
                if (distToCorridorCenter <= cornerSnapDistance + Constants.TILE_SIZE / 2f) {
                    float nudgeAmount = cornerSnapSpeed * deltaTime * Constants.TILE_SIZE;
                    float idealY = corridorCenterY - height / 2f;
                    
                    float newY;
                    if (position.y < idealY) {
                        newY = Math.min(position.y + nudgeAmount, idealY);
                    } else {
                        newY = Math.max(position.y - nudgeAmount, idealY);
                    }
                    
                    if (checkCollisionWithForgiveness(position.x, newY, maze, doorUnlocked)) {
                        position.y = newY;
                        
                        float newX = position.x + moveX;
                        if (checkCollisionWithForgiveness(newX, position.y, maze, doorUnlocked)) {
                            position.x = newX;
                        }
                        
                        bounds.setPosition(position);
                        isMoving = true;
                        updateTilePosition();
                        return true;
                    }
                }
            }
        }
        
        return false;
    }
    
    /**
     * Tries to align player when moving vertically into a corridor.
     * Detects if there's a walkable corridor ahead and nudges player to align.
     */
    private boolean tryVerticalCornerForgiveness(float deltaTime, float moveY, Maze maze, boolean doorUnlocked) {
        float playerCenterX = position.x + width / 2f;
        int playerTileX = (int) (playerCenterX / Constants.TILE_SIZE);
        
        int targetTileY;
        if (moveY > 0) {
            targetTileY = (int) ((position.y + height + Math.abs(moveY)) / Constants.TILE_SIZE);
        } else {
            targetTileY = (int) ((position.y - Math.abs(moveY)) / Constants.TILE_SIZE);
        }
        
        float tileCenterX = playerTileX * Constants.TILE_SIZE + Constants.TILE_SIZE / 2f;
        float offsetFromCenter = playerCenterX - tileCenterX;
        
        if (maze.isWalkable(playerTileX, targetTileY, doorUnlocked)) {
            if (Math.abs(offsetFromCenter) <= cornerSnapDistance && Math.abs(offsetFromCenter) > 1f) {
                float nudgeAmount = cornerSnapSpeed * deltaTime * Constants.TILE_SIZE;
                float idealX = tileCenterX - width / 2f;
                
                if (position.x < idealX) {
                    position.x = Math.min(position.x + nudgeAmount, idealX);
                } else {
                    position.x = Math.max(position.x - nudgeAmount, idealX);
                }
                
                float newY = position.y + moveY;
                if (checkCollisionWithForgiveness(position.x, newY, maze, doorUnlocked)) {
                    position.y = newY;
                    bounds.setPosition(position);
                    isMoving = true;
                    updateTilePosition();
                    return true;
                }
            }
        }
        
        for (int xOffset = -1; xOffset <= 1; xOffset++) {
            if (xOffset == 0) continue;
            
            int checkTileX = playerTileX + xOffset;
            if (maze.isWalkable(checkTileX, targetTileY, doorUnlocked)) {
                float corridorCenterX = checkTileX * Constants.TILE_SIZE + Constants.TILE_SIZE / 2f;
                float distToCorridorCenter = Math.abs(playerCenterX - corridorCenterX);
                
                if (distToCorridorCenter <= cornerSnapDistance + Constants.TILE_SIZE / 2f) {
                    float nudgeAmount = cornerSnapSpeed * deltaTime * Constants.TILE_SIZE;
                    float idealX = corridorCenterX - width / 2f;
                    
                    float newX;
                    if (position.x < idealX) {
                        newX = Math.min(position.x + nudgeAmount, idealX);
                    } else {
                        newX = Math.max(position.x - nudgeAmount, idealX);
                    }
                    
                    if (checkCollisionWithForgiveness(newX, position.y, maze, doorUnlocked)) {
                        position.x = newX;
                        
                        float newY = position.y + moveY;
                        if (checkCollisionWithForgiveness(position.x, newY, maze, doorUnlocked)) {
                            position.y = newY;
                        }
                        
                        bounds.setPosition(position);
                        isMoving = true;
                        updateTilePosition();
                        return true;
                    }
                }
            }
        }
        
        return false;
    }
    
    /**
     * Updates tile position based on current position.
     */
    @Override
    protected void updateTilePosition() {
        tileX = (int) ((position.x + width / 2f) / Constants.TILE_SIZE);
        tileY = (int) ((position.y + height / 2f) / Constants.TILE_SIZE);
    }
    
    /**
     * Tries to slide along walls when direct movement is blocked.
     */
    private void tryWallSliding(float moveX, float moveY, Maze maze, boolean doorUnlocked) {
        boolean movedX = false;
        boolean movedY = false;
        
        if (moveX != 0) {
            float testX = position.x + moveX;
            if (checkCollisionWithForgiveness(testX, position.y, maze, doorUnlocked)) {
                position.x = testX;
                movedX = true;
            }
        }
        
        if (moveY != 0) {
            float testY = position.y + moveY;
            if (checkCollisionWithForgiveness(position.x, testY, maze, doorUnlocked)) {
                position.y = testY;
                movedY = true;
            }
        }
        
        if (movedX || movedY) {
            bounds.setPosition(position);
            isMoving = true;
            updateTilePosition();
        } else {
            isMoving = false;
        }
    }
    
    /**
     * Checks if corners are valid for X movement.
     */
    private boolean checkCornersX(float testX, float y, Maze maze, boolean doorUnlocked) {
        float margin = cornerForgivenessPadding;
        int cornerTileX, cornerTileY;
        
        cornerTileX = (int) ((testX + margin) / Constants.TILE_SIZE);
        cornerTileY = (int) ((y + height - margin) / Constants.TILE_SIZE);
        if (!maze.isWalkable(cornerTileX, cornerTileY, doorUnlocked)) return false;
        
        cornerTileX = (int) ((testX + width - margin) / Constants.TILE_SIZE);
        if (!maze.isWalkable(cornerTileX, cornerTileY, doorUnlocked)) return false;
        
        cornerTileX = (int) ((testX + margin) / Constants.TILE_SIZE);
        cornerTileY = (int) ((y + margin) / Constants.TILE_SIZE);
        if (!maze.isWalkable(cornerTileX, cornerTileY, doorUnlocked)) return false;
        
        cornerTileX = (int) ((testX + width - margin) / Constants.TILE_SIZE);
        if (!maze.isWalkable(cornerTileX, cornerTileY, doorUnlocked)) return false;
        
        return true;
    }
    
    /**
     * Checks if corners are valid for Y movement.
     */
    private boolean checkCornersY(float x, float testY, Maze maze, boolean doorUnlocked) {
        float margin = cornerForgivenessPadding;
        int cornerTileX, cornerTileY;
        
        cornerTileX = (int) ((x + margin) / Constants.TILE_SIZE);
        cornerTileY = (int) ((testY + height - margin) / Constants.TILE_SIZE);
        if (!maze.isWalkable(cornerTileX, cornerTileY, doorUnlocked)) return false;
        
        cornerTileX = (int) ((x + width - margin) / Constants.TILE_SIZE);
        if (!maze.isWalkable(cornerTileX, cornerTileY, doorUnlocked)) return false;
        
        cornerTileX = (int) ((x + margin) / Constants.TILE_SIZE);
        cornerTileY = (int) ((testY + margin) / Constants.TILE_SIZE);
        if (!maze.isWalkable(cornerTileX, cornerTileY, doorUnlocked)) return false;
        
        cornerTileX = (int) ((x + width - margin) / Constants.TILE_SIZE);
        if (!maze.isWalkable(cornerTileX, cornerTileY, doorUnlocked)) return false;
        
        return true;
    }
    
    /**
     * Gets the current movement direction.
     * @return The direction the player is trying to move
     */
    public Direction getCurrentMoveDirection() {
        return currentMoveDirection;
    }
    
    /**
     * Sets the current movement direction (used by GameWorld input handling).
     * @param direction The direction to move
     */
    public void setCurrentMoveDirection(Direction direction) {
        this.currentMoveDirection = direction;
        if (direction != Direction.NONE) {
            this.facingDirection = direction;
        } else {
            isMoving = false;
        }
    }
    
    /**
     * Applies damage to the player.
     * @param amount Amount of damage (usually 1)
     */
    public void takeDamage(int amount) {
        if (isInvincible || hasShield) {
            if (hasShield) {
                hasShield = false;
                shieldTimer = 0;
            }
            return;
        }
        
        lives -= amount;
        
        if (lives <= 0) {
            lives = 0;
        } else {
            isInvincible = true;
            invincibilityTimer = Constants.INVINCIBILITY_DURATION;
            flashRed = true;
            flashTimer = 0f;
        }
    }
    
    /**
     * Heals the player.
     * @param amount Amount of lives to restore
     */
    public void heal(int amount) {
        lives = Math.min(lives + amount, maxLives);
    }
    
    /**
     * Collects a key.
     * @param keyId Unique identifier for the key
     */
    public void collectKey(String keyId) {
        collectedKeys.add(keyId);
    }
    
    /**
     * Checks if the player has collected all required keys.
     * @return true if all keys collected
     */
    public boolean hasAllKeys() {
        return collectedKeys.size() >= keysRequired;
    }
    
    /**
     * Activates the shield power-up.
     * @param duration Duration in seconds
     */
    public void activateShield(float duration) {
        hasShield = true;
        shieldTimer = duration;
    }
    
    /**
     * Activates the speed boost power-up.
     * @param duration Duration in seconds
     * @param multiplier Speed multiplier
     */
    public void activateSpeedBoost(float duration, float multiplier) {
        hasSpeedBoost = true;
        speedBoostTimer = duration;
        speedBoostMultiplier = multiplier;
        updateSpeed();
    }
    
    /**
     * Updates speed based on current state.
     */
    private void updateSpeed() {
        speed = baseSpeed;
        if (isRunning) {
            speed *= Constants.PLAYER_RUN_MULTIPLIER;
        }
        if (hasSpeedBoost) {
            speed *= speedBoostMultiplier;
        }
    }
    
    /**
     * Sets whether the player is running.
     * @param running true if run key is held
     */
    public void setRunning(boolean running) {
        this.isRunning = running;
        updateSpeed();
    }
    
    /**
     * Adds to the player's score.
     * @param points Points to add
     */
    public void addScore(int points) {
        score += points;
    }
    
    /**
     * Sets an animation for a direction.
     * @param direction The direction
     * @param animation The animation
     */
    public void setAnimation(Direction direction, Animation<TextureRegion> animation) {
        animations.put(direction, animation);
    }
    
    /**
     * Sets the same animation for all directions (simple setup).
     * @param animation The animation to use
     */
    public void setAllDirectionAnimation(Animation<TextureRegion> animation) {
        for (Direction dir : Direction.values()) {
            if (dir != Direction.NONE) {
                animations.put(dir, animation);
            }
        }
    }
    
    /**
     * Sets the comprehensive character animations system.
     * @param animations The CharacterAnimations instance
     */
    public void setCharacterAnimations(CharacterAnimations animations) {
        this.characterAnimations = animations;
    }
    
    /**
     * Gets the character animations system.
     * @return The CharacterAnimations instance, or null if not set
     */
    public CharacterAnimations getCharacterAnimations() {
        return characterAnimations;
    }
    
    // Getters
    public int getLives() { return lives; }
    public int getMaxLives() { return maxLives; }
    public int getKeysCollected() { return collectedKeys.size(); }
    public int getKeysRequired() { return keysRequired; }
    public boolean hasShield() { return hasShield; }
    public boolean hasSpeedBoost() { return hasSpeedBoost; }
    public boolean isInvincible() { return isInvincible; }
    public Direction getFacingDirection() { return facingDirection; }
    public boolean isMoving() { return isMoving; }
    public boolean isRunning() { return isRunning; }
    public int getScore() { return score; }
    public float getSpeed() { return speed; }
    public Set<String> getCollectedKeys() { return new HashSet<>(collectedKeys); }
    public boolean isDead() { return lives <= 0; }
    public float getShieldTimeRemaining() { return shieldTimer; }
    public float getSpeedBoostTimeRemaining() { return speedBoostTimer; }
    
    // Currency getters
    public int getCoins() { return coins; }
    public int getGems() { return gems; }
    public int getTotalCoinsCollected() { return totalCoinsCollected; }
    public int getTotalGemsCollected() { return totalGemsCollected; }
    
    // Additional power-up getters
    public boolean hasFreeze() { return hasFreeze; }
    public boolean hasMagnet() { return hasMagnet; }
    public boolean hasDoublePoints() { return hasDoublePoints; }
    public boolean hasInvisibility() { return hasInvisibility; }
    public float getFreezeTimeRemaining() { return freezeTimer; }
    public float getMagnetTimeRemaining() { return magnetTimer; }
    public float getDoublePointsTimeRemaining() { return doublePointsTimer; }
    public float getInvisibilityTimeRemaining() { return invisibilityTimer; }
    
    // Setters
    public void setLives(int lives) { this.lives = Math.min(lives, maxLives); }
    public void setMaxLives(int maxLives) { this.maxLives = maxLives; }
    public void setKeysRequired(int keysRequired) { this.keysRequired = keysRequired; }
    public void setScore(int score) { this.score = score; }
    public void setMoving(boolean moving) { this.isMoving = moving; }
    public void setInvincible(boolean invincible) { this.isInvincible = invincible; }
    
    public void addCoins(int amount) {
        if (amount > 0) {
            coins += amount;
            totalCoinsCollected += amount;
        }
    }
    
    public void addGems(int amount) {
        if (amount > 0) {
            gems += amount;
            totalGemsCollected += amount;
        }
    }
    
    public boolean spendCoins(int amount) {
        if (amount > 0 && coins >= amount) {
            coins -= amount;
            return true;
        }
        return false;
    }
    
    public boolean spendGems(int amount) {
        if (amount > 0 && gems >= amount) {
            gems -= amount;
            return true;
        }
        return false;
    }
    
    public void activateFreeze(float duration) {
        hasFreeze = true;
        freezeTimer = duration;
    }
    
    public void activateMagnet(float duration) {
        hasMagnet = true;
        magnetTimer = duration;
    }
    
    public void activateDoublePoints(float duration) {
        hasDoublePoints = true;
        doublePointsTimer = duration;
    }
    
    public void activateInvisibility(float duration) {
        hasInvisibility = true;
        invisibilityTimer = duration;
    }
    
    /**
     * Resets player state for a new game.
     */
    public void reset() {
        lives = maxLives;
        collectedKeys.clear();
        hasShield = false;
        hasSpeedBoost = false;
        isInvincible = false;
        score = 0;
        stateTime = 0;
        
        // Reset currency
        coins = 0;
        gems = 0;
        
        // Reset additional power-ups
        hasFreeze = false;
        hasMagnet = false;
        hasDoublePoints = false;
        hasInvisibility = false;
        freezeTimer = 0;
        magnetTimer = 0;
        doublePointsTimer = 0;
        invisibilityTimer = 0;
        
        // Reset animation state
        if (characterAnimations != null) {
            characterAnimations.resetStateTime();
            characterAnimations.setDirection(Direction.DOWN);
        }
    }
    
    // ==================== Corner Forgiveness Settings ====================
    
    /**
     * Gets the collision padding for corner forgiveness.
     * @return Padding in pixels
     */
    public float getCornerForgivenessPadding() {
        return cornerForgivenessPadding;
    }
    
    /**
     * Sets the collision padding for corner forgiveness.
     * Higher values make collision more lenient.
     * @param padding Padding in pixels (recommended: 4-10)
     */
    public void setCornerForgivenessPadding(float padding) {
        this.cornerForgivenessPadding = Math.max(0f, Math.min(padding, Constants.TILE_SIZE / 4f));
    }
    
    /**
     * Gets the snap distance for auto-alignment at corridor entrances.
     * @return Snap distance in pixels
     */
    public float getCornerSnapDistance() {
        return cornerSnapDistance;
    }
    
    /**
     * Sets the snap distance for auto-alignment at corridor entrances.
     * Player will be nudged toward corridor center if within this distance.
     * @param distance Snap distance in pixels (recommended: 8-16)
     */
    public void setCornerSnapDistance(float distance) {
        this.cornerSnapDistance = Math.max(0f, Math.min(distance, Constants.TILE_SIZE / 2f));
    }
    
    /**
     * Gets the snap speed for auto-alignment.
     * @return Snap speed multiplier
     */
    public float getCornerSnapSpeed() {
        return cornerSnapSpeed;
    }
    
    /**
     * Sets the snap speed for auto-alignment.
     * Higher values make the nudge faster.
     * @param speed Snap speed multiplier (recommended: 5-15)
     */
    public void setCornerSnapSpeed(float speed) {
        this.cornerSnapSpeed = Math.max(1f, Math.min(speed, 20f));
    }
    
    /**
     * Disposes of player resources.
     */
    public void dispose() {
        if (characterAnimations != null) {
            characterAnimations.dispose();
            characterAnimations = null;
        }
    }
}
