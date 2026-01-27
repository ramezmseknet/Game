package de.tum.cit.fop.maze.achievement;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Tracks player's progress towards achievements.
 * Serializable for saving/loading.
 */
public class AchievementProgress {
    
    /** Set of unlocked achievement IDs */
    public Set<String> unlockedIds = new HashSet<>();
    
    /** Progress values for each achievement */
    public Map<String, Integer> progressMap = new HashMap<>();
    
    /** Timestamps of when achievements were unlocked */
    public Map<String, Long> unlockTimes = new HashMap<>();
    
    /** Transient: whether damage was taken in current level */
    private transient boolean levelDamageTaken = false;
    
    public AchievementProgress() {}
    
    /**
     * Checks if an achievement is unlocked.
     * @param achievementId The achievement ID
     * @return true if unlocked
     */
    public boolean isUnlocked(String achievementId) {
        return unlockedIds.contains(achievementId);
    }
    
    /**
     * Unlocks an achievement.
     * @param achievementId The achievement ID
     */
    public void unlock(String achievementId) {
        unlockedIds.add(achievementId);
        unlockTimes.put(achievementId, System.currentTimeMillis());
    }
    
    /**
     * Gets progress for an achievement.
     * @param achievementId The achievement ID
     * @return Current progress value
     */
    public int getProgress(String achievementId) {
        return progressMap.getOrDefault(achievementId, 0);
    }
    
    /**
     * Sets progress for an achievement.
     * @param achievementId The achievement ID
     * @param value The progress value
     */
    public void setProgress(String achievementId, int value) {
        progressMap.put(achievementId, value);
    }
    
    /**
     * Gets the unlock timestamp for an achievement.
     * @param achievementId The achievement ID
     * @return Unlock timestamp, or 0 if not unlocked
     */
    public long getUnlockTime(String achievementId) {
        return unlockTimes.getOrDefault(achievementId, 0L);
    }
    
    /**
     * Gets total achievements unlocked.
     * @return Number of unlocked achievements
     */
    public int getUnlockedCount() {
        return unlockedIds.size();
    }
    
    /**
     * Checks if damage was taken in current level.
     * @return true if damaged
     */
    public boolean isLevelDamageTaken() {
        return levelDamageTaken;
    }
    
    /**
     * Sets whether damage was taken in current level.
     * @param taken true if damaged
     */
    public void setLevelDamageTaken(boolean taken) {
        this.levelDamageTaken = taken;
    }
}
