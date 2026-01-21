package de.tum.cit.fop.maze.entity.enemy;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import de.tum.cit.fop.maze.ai.Pathfinder;
import de.tum.cit.fop.maze.entity.AbstractEntity;
import de.tum.cit.fop.maze.entity.Direction;
import de.tum.cit.fop.maze.entity.Player;
import de.tum.cit.fop.maze.util.Constants;
import de.tum.cit.fop.maze.world.Maze;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base class for all enemy types.
 * Provides AI state machine and pathfinding integration.
 */
public abstract class Enemy extends AbstractEntity {
    
    protected String id;
    protected int damage;
    protected float speed;
    protected float detectionRange;

    // Health and damage system
    protected int health;
    protected int maxHealth;
    protected boolean isInvincible;
    protected float invincibilityTimer;
    protected static final float INVINCIBILITY_DURATION = 0.5f;
    
    protected EnemyAIState currentState;
    protected Pathfinder pathfinder;
    protected Maze maze;
    protected Player targetPlayer;
    
    protected List<Vector2> currentPath;
    protected int pathIndex;
    protected float pathUpdateTimer;
    protected float pathUpdateInterval;
    
    protected Direction facingDirection;
    protected Animation<TextureRegion> animation;
    protected float stateTime;
    
    // New directional animation system
    protected EnemyAnimations enemyAnimations;
    protected boolean isMoving;
    
    /**
     * Creates an enemy at the specified tile position.
     * @param id Unique identifier
     * @param tileX Starting tile X
     * @param tileY Starting tile Y
     * @param width Entity width
     * @param height Entity height
     */
    public Enemy(String id, int tileX, int tileY, float width, float height) {
        super(tileX, tileY, width, height);
        
        this.id = id;
        this.damage = 1;
        this.speed = Constants.PLAYER_WALK_SPEED * 0.4f; // 40% of player speed
        this.detectionRange = 3 * Constants.TILE_SIZE; // 3 tiles - only detect nearby players

        // Initialize health system
        this.maxHealth = 3;
        this.health = maxHealth;
        this.isInvincible = false;
        this.invincibilityTimer = 0f;
        
        this.currentPath = new ArrayList<>();
        this.pathIndex = 0;
        this.pathUpdateTimer = 0f;
        this.pathUpdateInterval = 0.25f; // Update path every 0.25 seconds
        
        this.facingDirection = Direction.DOWN;
        this.stateTime = 0f;
        this.isMoving = false;
        this.enemyAnimations = null;
    }
    
    @Override
    public void update(float deltaTime) {
        stateTime += deltaTime;

        // Update invincibility timer
        if (isInvincible) {
            invincibilityTimer -= deltaTime;
            if (invincibilityTimer <= 0) {
                isInvincible = false;
                invincibilityTimer = 0f;
            }
        }

        // Update AI state
        if (currentState != null) {
            EnemyAIState nextState = currentState.checkTransitions(this);
            if (nextState != currentState) {
                currentState.exit(this);
                currentState = nextState;
                currentState.enter(this);
            }
            currentState.update(this, deltaTime);
        }

        // Update path timer
        pathUpdateTimer += deltaTime;

        // Update directional animations
        if (enemyAnimations != null) {
            enemyAnimations.update(deltaTime, facingDirection, isMoving);
        }

        super.update(deltaTime);
    }
    
    @Override
    public void render(SpriteBatch batch) {
        if (!active) return;

        // Apply red tint when invincible (just took damage)
        if (isInvincible) {
            batch.setColor(1f, 0.3f, 0.3f, 1f);
        }

        // Use directional animations if available
        if (enemyAnimations != null) {
            enemyAnimations.render(batch, position.x, position.y, width, height);
        } else if (animation != null) {
            TextureRegion frame = animation.getKeyFrame(stateTime, true);
            batch.draw(frame, position.x, position.y, width, height);
        } else {
            super.render(batch);
        }

        // Reset color after rendering
        if (isInvincible) {
            batch.setColor(Color.WHITE);
        }
    }
    
    /**
     * Moves towards the next point in the current path.
     * @param deltaTime Frame delta time
     * @return true if reached the end of path
     */
    public boolean followPath(float deltaTime) {
        if (currentPath.isEmpty() || pathIndex >= currentPath.size()) {
            isMoving = false;
            return true;
        }
        
        // Skip waypoints that we're already standing on
        while (pathIndex < currentPath.size()) {
            Vector2 target = currentPath.get(pathIndex);
            if ((int) target.x == tileX && (int) target.y == tileY) {
                pathIndex++;
            } else {
                break;
            }
        }
        
        if (pathIndex >= currentPath.size()) {
            isMoving = false;
            return true;
        }
        
        Vector2 target = currentPath.get(pathIndex);
        Vector2 targetWorld = new Vector2(
            target.x * Constants.TILE_SIZE + Constants.TILE_SIZE / 2f,
            target.y * Constants.TILE_SIZE + Constants.TILE_SIZE / 2f
        );
        
        Vector2 center = getCenter();
        Vector2 direction = targetWorld.cpy().sub(center);
        float distance = direction.len();
        
        // Use a fixed threshold for reaching waypoints (8 pixels)
        float waypointThreshold = 8f;
        
        if (distance < waypointThreshold) {
            // Reached waypoint, move to next
            setTilePosition((int) target.x, (int) target.y);
            pathIndex++;
            return pathIndex >= currentPath.size();
        }
        
        // Move towards waypoint
        direction.nor().scl(speed * deltaTime);
        position.add(direction);
        bounds.setPosition(position);
        
        // Update facing direction and movement state
        updateFacingDirection(direction);
        isMoving = true;
        
        return false;
    }
    
    /**
     * Updates facing direction based on movement.
     */
    protected void updateFacingDirection(Vector2 movement) {
        if (Math.abs(movement.x) > Math.abs(movement.y)) {
            facingDirection = movement.x > 0 ? Direction.RIGHT : Direction.LEFT;
        } else if (movement.y != 0) {
            facingDirection = movement.y > 0 ? Direction.UP : Direction.DOWN;
        }
    }
    
    /**
     * Sets moving state (for animation).
     */
    public void setMoving(boolean moving) {
        this.isMoving = moving;
    }
    
    /**
     * Calculates a new path to the target position.
     * @param targetTileX Target tile X
     * @param targetTileY Target tile Y
     */
    public void calculatePathTo(int targetTileX, int targetTileY) {
        if (pathfinder != null) {
            currentPath = pathfinder.findPath(tileX, tileY, targetTileX, targetTileY);
            pathIndex = 0;
            // Skip the first waypoint if it's our current position
            if (!currentPath.isEmpty() && 
                (int) currentPath.get(0).x == tileX && 
                (int) currentPath.get(0).y == tileY) {
                pathIndex = 1;
            }
        }
    }
    
    /**
     * Calculates a new path to the player.
     */
    public void calculatePathToPlayer() {
        if (targetPlayer != null) {
            calculatePathTo(targetPlayer.getTileX(), targetPlayer.getTileY());
        }
    }
    
    /**
     * Checks if it's time to recalculate the path.
     * @return true if path should be updated
     */
    public boolean shouldUpdatePath() {
        return pathUpdateTimer >= pathUpdateInterval;
    }
    
    /**
     * Resets the path update timer.
     */
    public void resetPathTimer() {
        pathUpdateTimer = 0f;
    }
    
    /**
     * Gets the distance to the player.
     * @return Distance in pixels, or Float.MAX_VALUE if no player
     */
    public float getDistanceToPlayer() {
        if (targetPlayer == null) return Float.MAX_VALUE;
        return getCenter().dst(targetPlayer.getCenter());
    }
    
    /**
     * Checks if player is within detection range.
     * @return true if player is detectable
     */
    public boolean canSeePlayer() {
        return getDistanceToPlayer() <= detectionRange;
    }
    
    /**
     * Sets the current AI state.
     * @param state The new state
     */
    public void setState(EnemyAIState state) {
        if (currentState != null) {
            currentState.exit(this);
        }
        currentState = state;
        if (currentState != null) {
            currentState.enter(this);
        }
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public int getDamage() { return damage; }
    public void setDamage(int damage) { this.damage = damage; }
    public float getSpeed() { return speed; }
    public void setSpeed(float speed) { this.speed = speed; }
    public float getDetectionRange() { return detectionRange; }
    public void setDetectionRange(float range) { this.detectionRange = range; }
    public EnemyAIState getCurrentState() { return currentState; }
    public Pathfinder getPathfinder() { return pathfinder; }
    public void setPathfinder(Pathfinder pathfinder) { this.pathfinder = pathfinder; }
    public Maze getMaze() { return maze; }
    public void setMaze(Maze maze) { this.maze = maze; }
    public Player getTargetPlayer() { return targetPlayer; }
    public void setTargetPlayer(Player player) { this.targetPlayer = player; }
    public List<Vector2> getCurrentPath() { return currentPath; }
    public Direction getFacingDirection() { return facingDirection; }
    public void setAnimation(Animation<TextureRegion> animation) { this.animation = animation; }
    public void setEnemyAnimations(EnemyAnimations animations) { this.enemyAnimations = animations; }
    public EnemyAnimations getEnemyAnimations() { return enemyAnimations; }
    public String getCurrentStateName() { return currentState != null ? currentState.getName() : "none"; }

    // Health system methods
    public int getHealth() { return health; }
    public int getMaxHealth() { return maxHealth; }
    public void setHealth(int health) { this.health = Math.max(0, Math.min(health, maxHealth)); }
    public void setMaxHealth(int maxHealth) { this.maxHealth = maxHealth; }
    public boolean isInvincible() { return isInvincible; }

    /**
     * Applies damage to the enemy.
     * Returns true if damage was dealt, false if enemy is invincible.
     * @param amount Amount of damage to deal
     * @return true if damage was applied
     */
    public boolean takeDamage(int amount) {
        if (isInvincible || !active) {
            return false;
        }

        health -= amount;
        if (health <= 0) {
            health = 0;
            active = false;
        } else {
            // Trigger invincibility frames
            isInvincible = true;
            invincibilityTimer = INVINCIBILITY_DURATION;
        }
        return true;
    }

    /**
     * Checks if the enemy is dead (health <= 0).
     * @return true if dead
     */
    public boolean isDead() {
        return health <= 0;
    }
}
