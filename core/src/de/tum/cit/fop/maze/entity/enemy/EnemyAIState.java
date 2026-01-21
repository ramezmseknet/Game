package de.tum.cit.fop.maze.entity.enemy;

/**
 * Interface for enemy AI state.
 * Each state handles specific behavior (idle, patrol, chase, etc.)
 */
public interface EnemyAIState {

    /**
     * Called when entering this state.
     * 
     * @param enemy The enemy entering the state
     */
    void enter(Enemy enemy);

    /**
     * Updates the state logic.
     * 
     * @param enemy     The enemy to update
     * @param deltaTime Frame delta time
     */
    void update(Enemy enemy, float deltaTime);

    /**
     * Called when exiting this state.
     * 
     * @param enemy The enemy exiting the state
     */
    void exit(Enemy enemy);

    /**
     * Checks if a state transition should occur.
     * 
     * @param enemy The enemy to check
     * @return The next state, or this state if no transition
     */
    EnemyAIState checkTransitions(Enemy enemy);

    /**
     * Gets the name of this state.
     * 
     * @return State name for debugging/saving
     */
    String getName();
}
