package de.tum.cit.fop.maze.achievement;

/**
 * Types of achievements for tracking and categorization.
 */
public enum AchievementType {
    
    /** Collect a certain number of keys */
    COLLECT_KEYS("Keys Collected"),
    
    /** Complete a certain number of levels */
    COMPLETE_LEVELS("Levels Completed"),
    
    /** Complete a level without taking damage */
    NO_DAMAGE_LEVEL("Flawless Victory"),
    
    /** Complete a level within time limit */
    SPEED_RUN("Speed Run"),
    
    /** Survive waves in endless mode */
    ENDLESS_WAVES("Endless Survival"),
    
    /** Reach a certain score */
    REACH_SCORE("Score Achievement"),
    
    /** Kill/avoid enemies */
    ENEMIES_ENCOUNTERED("Enemy Encounters"),
    
    /** Collect power-ups */
    POWERUPS_COLLECTED("Power-ups Collected"),
    
    /** Play for a certain total time */
    TOTAL_PLAYTIME("Playtime"),
    
    /** Die a certain number of times */
    DEATHS("Deaths"),
    
    /** Discover secrets */
    SECRETS_FOUND("Secrets"),
    
    /** Custom achievements */
    CUSTOM("Custom");
    
    private final String displayName;
    
    AchievementType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
