package de.tum.cit.fop.maze.progression;

/**
 * Represents a single skill in the skill tree.
 */
public class Skill {
    
    private String id;
    private String name;
    private String description;
    private SkillCategory category;
    private int maxLevel;
    private int currentLevel;
    private float value; // The effect value per level
    
    /**
     * Creates a skill.
     * @param id Unique identifier
     * @param name Display name
     * @param description Description of the skill
     * @param category Skill category
     * @param maxLevel Maximum upgrade level
     * @param value Effect value per level
     */
    public Skill(String id, String name, String description, 
                 SkillCategory category, int maxLevel, float value) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
        this.maxLevel = maxLevel;
        this.currentLevel = 0;
        this.value = value;
    }
    
    /**
     * Upgrades the skill by one level.
     * @return true if upgraded successfully
     */
    public boolean upgrade() {
        if (currentLevel < maxLevel) {
            currentLevel++;
            return true;
        }
        return false;
    }
    
    /**
     * Checks if the skill can be upgraded.
     * @return true if not at max level
     */
    public boolean canUpgrade() {
        return currentLevel < maxLevel;
    }
    
    /**
     * Resets the skill to level 0.
     */
    public void reset() {
        currentLevel = 0;
    }
    
    /**
     * Gets the total effect value at current level.
     * @return Total value (value * currentLevel)
     */
    public float getTotalValue() {
        return value * currentLevel;
    }
    
    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public SkillCategory getCategory() { return category; }
    public int getMaxLevel() { return maxLevel; }
    public int getCurrentLevel() { return currentLevel; }
    public float getValue() { return value; }
    
    /**
     * Gets progress display string.
     * @return Level/MaxLevel string
     */
    public String getLevelString() {
        return currentLevel + "/" + maxLevel;
    }
    
    /**
     * Checks if skill is unlocked (level > 0).
     * @return true if unlocked
     */
    public boolean isUnlocked() {
        return currentLevel > 0;
    }
    
    /**
     * Checks if skill is maxed.
     * @return true if at max level
     */
    public boolean isMaxed() {
        return currentLevel >= maxLevel;
    }
    
    @Override
    public String toString() {
        return name + " (" + getLevelString() + ")";
    }
}
