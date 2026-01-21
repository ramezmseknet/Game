package de.tum.cit.fop.maze.entity.enemy;

import de.tum.cit.fop.maze.entity.enemy.ai.ChaseState;
import de.tum.cit.fop.maze.util.Constants;

/**
 * An aggressive enemy that actively chases the player.
 * Has a larger detection range and moves faster than patrol enemies.
 */
public class ChaserEnemy extends Enemy {
    
    /**
     * Creates a chaser enemy at the specified position.
     * @param id Unique identifier
     * @param tileX Starting tile X
     * @param tileY Starting tile Y
     */
    public ChaserEnemy(String id, int tileX, int tileY) {
        super(id, tileX, tileY,
              Constants.DEFAULT_ENEMY_WIDTH,
              Constants.DEFAULT_ENEMY_HEIGHT);
        
        this.speed = Constants.PLAYER_WALK_SPEED * 0.5f; // 50% of player speed
        this.detectionRange = 4 * Constants.TILE_SIZE; // 4 tiles - only detect nearby players
        this.damage = 1;
        
        // Start in chase state (always aggressive)
        setState(new ChaseState());
    }
}
