package de.tum.cit.fop.maze.achievement;

/**
 * Listener interface for achievement unlock events.
 */
public interface AchievementUnlockListener {
    
    /**
     * Called when an achievement is unlocked.
     * @param achievement The unlocked achievement
     */
    void onAchievementUnlocked(Achievement achievement);
}
