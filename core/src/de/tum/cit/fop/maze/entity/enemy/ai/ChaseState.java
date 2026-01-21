package de.tum.cit.fop.maze.entity.enemy.ai;

import com.badlogic.gdx.math.MathUtils;
import de.tum.cit.fop.maze.entity.Player;
import de.tum.cit.fop.maze.entity.enemy.Enemy;
import de.tum.cit.fop.maze.entity.enemy.EnemyAIState;

/**
 * Chase state - enemy pursues the player using pathfinding.
 */
public class ChaseState implements EnemyAIState {
    
    private float chaseTime;
    private float maxChaseTime;
    private float lostPlayerTimer;
    private float lostPlayerThreshold;
    private int lastPlayerTileX;
    private int lastPlayerTileY;
    
    public ChaseState() {
        this.maxChaseTime = 30f;
        this.lostPlayerThreshold = 0.5f; // Stop chasing quickly when player escapes
        this.lastPlayerTileX = -1;
        this.lastPlayerTileY = -1;
    }
    
    @Override
    public void enter(Enemy enemy) {
        chaseTime = 0f;
        lostPlayerTimer = 0f;
        lastPlayerTileX = -1;
        lastPlayerTileY = -1;
        enemy.calculatePathToPlayer();
        if (enemy.getTargetPlayer() != null) {
            lastPlayerTileX = enemy.getTargetPlayer().getTileX();
            lastPlayerTileY = enemy.getTargetPlayer().getTileY();
        }
        enemy.resetPathTimer();
    }
    
    @Override
    public void update(Enemy enemy, float deltaTime) {
        chaseTime += deltaTime;
        
        Player player = enemy.getTargetPlayer();
        if (player == null) return;
        
        // Only recalculate path if player moved to a different tile or timer expired
        boolean playerMoved = player.getTileX() != lastPlayerTileX || player.getTileY() != lastPlayerTileY;
        if (enemy.shouldUpdatePath() && playerMoved) {
            enemy.calculatePathToPlayer();
            lastPlayerTileX = player.getTileX();
            lastPlayerTileY = player.getTileY();
            enemy.resetPathTimer();
        } else if (enemy.shouldUpdatePath()) {
            enemy.resetPathTimer();
        }
        
        // Follow path
        enemy.followPath(deltaTime);
        
        // Track if we've lost sight of player
        if (!enemy.canSeePlayer()) {
            lostPlayerTimer += deltaTime;
        } else {
            lostPlayerTimer = 0f;
        }
    }
    
    @Override
    public void exit(Enemy enemy) {
        // Nothing to clean up
    }
    
    @Override
    public EnemyAIState checkTransitions(Enemy enemy) {
        // Lost the player
        if (lostPlayerTimer >= lostPlayerThreshold) {
            return new PatrolState();
        }
        
        // Chased too long, take a break
        if (chaseTime >= maxChaseTime) {
            return new IdleState(MathUtils.random(1f, 3f));
        }
        
        return this;
    }
    
    @Override
    public String getName() {
        return "chase";
    }
}
