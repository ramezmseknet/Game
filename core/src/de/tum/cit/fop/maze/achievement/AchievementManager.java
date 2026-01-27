package de.tum.cit.fop.maze.achievement;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import de.tum.cit.fop.maze.event.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages game achievements, tracking progress and unlocking.
 * Achievement definitions are loaded from an external JSON file.
 * Integrates with the event system to track progress automatically.
 */
public class AchievementManager implements EventListener<GameEvent> {
    @SuppressWarnings("unused")

    private static AchievementManager instance;

    private Array<Achievement> achievements;
    private Map<String, Achievement> achievementMap;
    private AchievementProgress progress;

    // Listeners for achievement unlocks
    private Array<AchievementUnlockListener> unlockListeners;

    // Event bus reference
    @SuppressWarnings("unused")
    private EventBus eventBus;

    /**
     * Private constructor for singleton.
     */
    private AchievementManager() {
        achievements = new Array<>();
        achievementMap = new HashMap<>();
        unlockListeners = new Array<>();
        loadAchievements();
        loadProgress();
    }

    /**
     * Gets the singleton instance.
     * 
     * @return The AchievementManager instance
     */
    public static AchievementManager getInstance() {
        if (instance == null) {
            instance = new AchievementManager();
        }
        return instance;
    }

    /**
     * Registers with an event bus to receive game events.
     * 
     * @param eventBus The event bus to subscribe to
     */
    public void registerEventBus(EventBus eventBus) {
        this.eventBus = eventBus;

        // Subscribe to relevant events
        eventBus.subscribe(KeyCollectedEvent.class, this::onKeyCollected);
        eventBus.subscribe(PlayerDamagedEvent.class, this::onPlayerDamaged);
        eventBus.subscribe(LevelCompletedEvent.class, this::onLevelCompleted);
    }

    /**
     * Loads achievement definitions from external file.
     */
    private void loadAchievements() {
        try {
            FileHandle file = Gdx.files.internal("config/achievements.json");
            if (file.exists()) {
                Json json = new Json();
                AchievementDefinitions defs = json.fromJson(AchievementDefinitions.class, file);
                for (Achievement achievement : defs.achievements) {
                    achievements.add(achievement);
                    achievementMap.put(achievement.id, achievement);
                }
                Gdx.app.log("AchievementManager", "Loaded " + achievements.size + " achievements");
            } else {
                // Create default achievements if file doesn't exist
                createDefaultAchievements();
            }
        } catch (Exception e) {
            Gdx.app.error("AchievementManager", "Failed to load achievements: " + e.getMessage());
            createDefaultAchievements();
        }
    }

    /**
     * Creates default achievements.
     */
    private void createDefaultAchievements() {
        achievements.add(new Achievement("first_key", "Key Collector",
                "Collect your first key", "key_icon", AchievementType.COLLECT_KEYS, 1));
        achievements.add(new Achievement("key_master", "Key Master",
                "Collect 50 keys total", "key_icon", AchievementType.COLLECT_KEYS, 50));
        achievements.add(new Achievement("first_level", "Maze Runner",
                "Complete your first level", "level_icon", AchievementType.COMPLETE_LEVELS, 1));
        achievements.add(new Achievement("level_master", "Level Master",
                "Complete all campaign levels", "level_icon", AchievementType.COMPLETE_LEVELS, 5));
        achievements.add(new Achievement("survivor", "Survivor",
                "Complete a level without taking damage", "shield_icon", AchievementType.NO_DAMAGE_LEVEL, 1));
        achievements.add(new Achievement("speedster", "Speedster",
                "Complete a level in under 60 seconds", "speed_icon", AchievementType.SPEED_RUN, 60));
        achievements.add(new Achievement("endless_10", "Endless Beginner",
                "Survive 10 waves in endless mode", "endless_icon", AchievementType.ENDLESS_WAVES, 10));
        achievements.add(new Achievement("endless_50", "Endless Champion",
                "Survive 50 waves in endless mode", "endless_icon", AchievementType.ENDLESS_WAVES, 50));
        achievements.add(new Achievement("score_1000", "Score Hunter",
                "Reach a score of 1000", "score_icon", AchievementType.REACH_SCORE, 1000));
        achievements.add(new Achievement("score_10000", "Score Master",
                "Reach a score of 10000", "score_icon", AchievementType.REACH_SCORE, 10000));

        for (Achievement a : achievements) {
            achievementMap.put(a.id, a);
        }

        // Save default achievements
        saveDefaultAchievements();
    }

    /**
     * Saves default achievements to file.
     */
    private void saveDefaultAchievements() {
        try {
            FileHandle file = Gdx.files.local("config/achievements.json");
            AchievementDefinitions defs = new AchievementDefinitions();
            defs.achievements = achievements;

            Json json = new Json();
            json.setOutputType(JsonWriter.OutputType.json);
            file.writeString(json.prettyPrint(defs), false);
        } catch (Exception e) {
            Gdx.app.error("AchievementManager", "Failed to save default achievements: " + e.getMessage());
        }
    }

    /**
     * Loads player's achievement progress.
     */
    private void loadProgress() {
        try {
            FileHandle file = Gdx.files.local("saves/achievements_progress.json");
            if (file.exists()) {
                Json json = new Json();
                progress = json.fromJson(AchievementProgress.class, file);
            } else {
                progress = new AchievementProgress();
            }
        } catch (Exception e) {
            Gdx.app.error("AchievementManager", "Failed to load progress: " + e.getMessage());
            progress = new AchievementProgress();
        }
    }

    /**
     * Saves player's achievement progress.
     */
    public void saveProgress() {
        try {
            FileHandle file = Gdx.files.local("saves/achievements_progress.json");
            Json json = new Json();
            json.setOutputType(JsonWriter.OutputType.json);
            file.writeString(json.prettyPrint(progress), false);
        } catch (Exception e) {
            Gdx.app.error("AchievementManager", "Failed to save progress: " + e.getMessage());
        }
    }

    /**
     * Updates progress for an achievement type.
     * 
     * @param type   The achievement type
     * @param amount The amount to add to progress
     */
    public void updateProgress(AchievementType type, int amount) {
        for (Achievement achievement : achievements) {
            if (achievement.type == type && !isUnlocked(achievement.id)) {
                int currentProgress = progress.getProgress(achievement.id);
                int newProgress = currentProgress + amount;
                progress.setProgress(achievement.id, newProgress);

                // Check if unlocked
                if (newProgress >= achievement.requirement) {
                    unlock(achievement);
                }
            }
        }
    }

    /**
     * Sets absolute progress for an achievement type.
     * 
     * @param type  The achievement type
     * @param value The new progress value
     */
    public void setProgress(AchievementType type, int value) {
        for (Achievement achievement : achievements) {
            if (achievement.type == type && !isUnlocked(achievement.id)) {
                progress.setProgress(achievement.id, value);

                // Check if unlocked
                if (value >= achievement.requirement) {
                    unlock(achievement);
                }
            }
        }
    }

    /**
     * Unlocks an achievement.
     * 
     * @param achievement The achievement to unlock
     */
    private void unlock(Achievement achievement) {
        if (progress.isUnlocked(achievement.id))
            return;

        progress.unlock(achievement.id);
        saveProgress();

        Gdx.app.log("AchievementManager", "Achievement unlocked: " + achievement.name);

        // Play achievement sound
        de.tum.cit.fop.maze.audio.AudioManager.getInstance().playSound("achievement");

        // Notify listeners
        for (AchievementUnlockListener listener : unlockListeners) {
            listener.onAchievementUnlocked(achievement);
        }
    }

    /**
     * Checks if an achievement is unlocked.
     * 
     * @param achievementId The achievement ID
     * @return true if unlocked
     */
    public boolean isUnlocked(String achievementId) {
        return progress.isUnlocked(achievementId);
    }

    /**
     * Gets progress for an achievement.
     * 
     * @param achievementId The achievement ID
     * @return Current progress value
     */
    public int getProgress(String achievementId) {
        return progress.getProgress(achievementId);
    }

    /**
     * Gets all achievements.
     * 
     * @return Array of all achievements
     */
    public Array<Achievement> getAchievements() {
        return achievements;
    }

    /**
     * Gets unlocked achievements.
     * 
     * @return Array of unlocked achievements
     */
    public Array<Achievement> getUnlockedAchievements() {
        Array<Achievement> unlocked = new Array<>();
        for (Achievement a : achievements) {
            if (isUnlocked(a.id)) {
                unlocked.add(a);
            }
        }
        return unlocked;
    }

    /**
     * Adds an unlock listener.
     * 
     * @param listener The listener to add
     */
    public void addUnlockListener(AchievementUnlockListener listener) {
        unlockListeners.add(listener);
    }

    /**
     * Removes an unlock listener.
     * 
     * @param listener The listener to remove
     */
    public void removeUnlockListener(AchievementUnlockListener listener) {
        unlockListeners.removeValue(listener, true);
    }

    // Event handlers
    @Override
    public void onEvent(GameEvent event) {
        // Generic handler - specific handlers below
    }

    private void onKeyCollected(KeyCollectedEvent event) {
        updateProgress(AchievementType.COLLECT_KEYS, 1);
    }

    private void onPlayerDamaged(PlayerDamagedEvent event) {
        // Track for no-damage achievements
        progress.setLevelDamageTaken(true);
    }

    private void onLevelCompleted(LevelCompletedEvent event) {
        updateProgress(AchievementType.COMPLETE_LEVELS, 1);

        // Check for no-damage achievement
        if (!progress.isLevelDamageTaken()) {
            updateProgress(AchievementType.NO_DAMAGE_LEVEL, 1);
        }

        // Check for speed run achievement
        if (event.getCompletionTime() < 60f) {
            updateProgress(AchievementType.SPEED_RUN, 1);
        }

        // Reset level damage tracker
        progress.setLevelDamageTaken(false);
    }

    /**
     * Called when endless mode wave is completed.
     * 
     * @param waveNumber The completed wave number
     */
    public void onEndlessWaveCompleted(int waveNumber) {
        setProgress(AchievementType.ENDLESS_WAVES, waveNumber);
    }

    /**
     * Called when score changes.
     * 
     * @param score The new score
     */
    public void onScoreChanged(int score) {
        setProgress(AchievementType.REACH_SCORE, score);
    }

    /**
     * Resets all progress (for testing).
     */
    public void resetProgress() {
        progress = new AchievementProgress();
        saveProgress();
    }
}
