package de.tum.cit.fop.maze.achievement;

/**
 * Represents a game achievement that can be unlocked.
 */
public class Achievement {
    
    /** Unique identifier */
    public String id;
    
    /** Display name */
    public String name;
    
    /** Description of how to unlock */
    public String description;
    
    /** Icon identifier for display */
    public String icon;
    
    /** Type of achievement for tracking */
    public AchievementType type;
    
    /** Requirement value to unlock */
    public int requirement;
    
    /** Whether this is a hidden achievement */
    public boolean hidden;
    
    /** Points awarded for unlocking */
    public int points;
    
    /**
     * Default constructor for JSON.
     */
    public Achievement() {
        this.hidden = false;
        this.points = 10;
    }
    
    /**
     * Creates an achievement.
     * @param id Unique identifier
     * @param name Display name
     * @param description Description
     * @param icon Icon identifier
     * @param type Achievement type
     * @param requirement Unlock requirement
     */
    public Achievement(String id, String name, String description, 
                       String icon, AchievementType type, int requirement) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.type = type;
        this.requirement = requirement;
        this.hidden = false;
        this.points = 10;
    }
    
    /**
     * Gets a progress string for display.
     * @param currentProgress Current progress value
     * @return Formatted progress string
     */
    public String getProgressString(int currentProgress) {
        return currentProgress + " / " + requirement;
    }
    
    /**
     * Gets progress as a percentage.
     * @param currentProgress Current progress value
     * @return Progress percentage (0-100)
     */
    public float getProgressPercent(int currentProgress) {
        return Math.min(100f, (currentProgress / (float) requirement) * 100f);
    }
    
    @Override
    public String toString() {
        return name + " (" + id + ")";
    }
}
