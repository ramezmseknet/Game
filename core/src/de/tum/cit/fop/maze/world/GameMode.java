package de.tum.cit.fop.maze.world;

/**
 * Enumeration of available game modes.
 * Each mode has different rules and objectives.
 */
public enum GameMode {
    
    /**
     * Campaign mode - play through predefined levels in order.
     * Progress is saved and levels unlock sequentially.
     */
    CAMPAIGN("Campaign", "Play through story levels"),
    
    /**
     * Endless/Survival mode - procedurally generated mazes.
     * Difficulty increases over time. Compete for high scores.
     */
    ENDLESS("Endless", "Survive as long as possible"),
    
    /**
     * Time Attack mode - complete levels as fast as possible.
     * Focused on speedrunning with leaderboards.
     */
    TIME_ATTACK("Time Attack", "Race against the clock"),
    
    /**
     * Practice mode - no lives lost, for learning levels.
     */
    PRACTICE("Practice", "Learn without consequences");
    
    private final String displayName;
    private final String description;
    
    GameMode(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
    
    /**
     * Gets the human-readable name.
     * @return Display name
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Gets the mode description.
     * @return Description text
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Checks if this mode tracks high scores.
     * @return true if leaderboard-eligible
     */
    public boolean hasLeaderboard() {
        return this == ENDLESS || this == TIME_ATTACK;
    }
    
    /**
     * Checks if this mode has limited lives.
     * @return true if player can die
     */
    public boolean hasLives() {
        return this != PRACTICE;
    }
    
    /**
     * Checks if this mode uses procedural generation.
     * @return true if levels are generated
     */
    public boolean isProcedural() {
        return this == ENDLESS;
    }
    
    /**
     * Checks if progress should be saved.
     * @return true if save/load is enabled
     */
    public boolean canSave() {
        return this == CAMPAIGN;
    }
}
