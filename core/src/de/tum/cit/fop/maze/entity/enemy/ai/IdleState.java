package de.tum.cit.fop.maze.entity.enemy.ai;

import de.tum.cit.fop.maze.entity.enemy.Enemy;
import de.tum.cit.fop.maze.entity.enemy.EnemyAIState;

/**
 * Idle state - enemy stays in place.
 */
public class IdleState implements EnemyAIState {
    
    private float idleTime;
    private float maxIdleTime;
    
    public IdleState(float maxIdleTime) {
        this.maxIdleTime = maxIdleTime;
    }
    
    @Override
    public void enter(Enemy enemy) {
        idleTime = 0f;
        enemy.setVelocity(0, 0);
    }
    
    @Override
    public void update(Enemy enemy, float deltaTime) {
        idleTime += deltaTime;
    }
    
    @Override
    public void exit(Enemy enemy) {
        // Nothing to clean up
    }
    
    @Override
    public EnemyAIState checkTransitions(Enemy enemy) {
        // Transition to chase if player is visible
        if (enemy.canSeePlayer()) {
            return new ChaseState();
        }
        
        // Transition to patrol after idle time
        if (idleTime >= maxIdleTime) {
            return new PatrolState();
        }
        
        return this;
    }
    
    @Override
    public String getName() {
        return "idle";
    }
}
