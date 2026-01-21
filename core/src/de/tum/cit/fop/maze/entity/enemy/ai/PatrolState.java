package de.tum.cit.fop.maze.entity.enemy.ai;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import de.tum.cit.fop.maze.entity.enemy.Enemy;
import de.tum.cit.fop.maze.entity.enemy.EnemyAIState;
import de.tum.cit.fop.maze.world.Maze;

import java.util.ArrayList;
import java.util.List;

/**
 * Patrol state - enemy wanders around the maze.
 */
public class PatrolState implements EnemyAIState {
    
    private Vector2 patrolTarget;
    private float patrolTime;
    private float maxPatrolTime;
    private int stuckCounter;
    
    public PatrolState() {
        this.maxPatrolTime = 10f;
    }
    
    @Override
    public void enter(Enemy enemy) {
        patrolTime = 0f;
        stuckCounter = 0;
        pickNewPatrolTarget(enemy);
    }
    
    @Override
    public void update(Enemy enemy, float deltaTime) {
        patrolTime += deltaTime;
        
        // Follow path
        if (enemy.getCurrentPath().isEmpty() || enemy.followPath(deltaTime)) {
            // Reached target or no path, pick new target
            stuckCounter++;
            if (stuckCounter > 3) {
                // Been stuck too long, go idle
                return;
            }
            pickNewPatrolTarget(enemy);
        }
    }
    
    @Override
    public void exit(Enemy enemy) {
        patrolTarget = null;
    }
    
    @Override
    public EnemyAIState checkTransitions(Enemy enemy) {
        // Transition to chase if player is visible
        if (enemy.canSeePlayer()) {
            return new ChaseState();
        }
        
        // Transition to idle after patrol time or if stuck
        if (patrolTime >= maxPatrolTime || stuckCounter > 3) {
            return new IdleState(MathUtils.random(2f, 5f));
        }
        
        return this;
    }
    
    @Override
    public String getName() {
        return "patrol";
    }
    
    /**
     * Picks a new random patrol target.
     */
    private void pickNewPatrolTarget(Enemy enemy) {
        Maze maze = enemy.getMaze();
        if (maze == null) return;
        
        // Find walkable tiles nearby
        List<Vector2> candidates = new ArrayList<>();
        int range = 5;
        
        for (int dx = -range; dx <= range; dx++) {
            for (int dy = -range; dy <= range; dy++) {
                int tx = enemy.getTileX() + dx;
                int ty = enemy.getTileY() + dy;
                
                if (maze.isWalkableForEnemy(tx, ty)) {
                    candidates.add(new Vector2(tx, ty));
                }
            }
        }
        
        if (!candidates.isEmpty()) {
            patrolTarget = candidates.get(MathUtils.random(candidates.size() - 1));
            enemy.calculatePathTo((int) patrolTarget.x, (int) patrolTarget.y);
        }
    }
}
