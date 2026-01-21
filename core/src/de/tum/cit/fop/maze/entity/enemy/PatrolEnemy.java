package de.tum.cit.fop.maze.entity.enemy;

import de.tum.cit.fop.maze.entity.enemy.ai.PatrolState;
import de.tum.cit.fop.maze.util.Constants;

/**
 * A patrolling enemy that wanders the maze.
 * Will chase the player if they get too close.
 */
public class PatrolEnemy extends Enemy {
    
    /**
     * Creates a patrol enemy at the specified position.
     * @param id Unique identifier
     * @param tileX Starting tile X
     * @param tileY Starting tile Y
     */
    public PatrolEnemy(String id, int tileX, int tileY) {
        super(id, tileX, tileY,
              Constants.DEFAULT_ENEMY_WIDTH,
              Constants.DEFAULT_ENEMY_HEIGHT);
        
        this.speed = Constants.PLAYER_WALK_SPEED * 0.35f; // 35% of player speed
        this.detectionRange = 3 * Constants.TILE_SIZE; // 3 tiles - only detect nearby players
        
        // Start in patrol state
        setState(new PatrolState());
    }
}
