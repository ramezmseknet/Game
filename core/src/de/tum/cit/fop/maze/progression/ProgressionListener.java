package de.tum.cit.fop.maze.progression;

/**
 * Listener interface for progression events.
 */
public interface ProgressionListener {
    
    /**
     * Called when XP is gained.
     * @param amount Amount gained
     * @param currentXP Current XP in level
     * @param xpToNextLevel XP needed for next level
     */
    void onXPGained(int amount, int currentXP, int xpToNextLevel);
    
    /**
     * Called when player levels up.
     * @param newLevel The new level
     * @param skillPoints Available skill points
     */
    void onLevelUp(int newLevel, int skillPoints);
    
    /**
     * Called when a skill is unlocked/upgraded.
     * @param skill The skill that was upgraded
     */
    void onSkillUnlocked(Skill skill);
}
