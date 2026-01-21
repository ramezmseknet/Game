package de.tum.cit.fop.maze.persistence;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;

import java.util.HashMap;
import java.util.Map;

/**
 * Tracks comprehensive game statistics across all play sessions.
 * Persists data to JSON file.
 */
public class StatisticsTracker {
    
    private static final String STATS_FILE = "statistics.json";
    
    // Lifetime statistics
    private long totalPlayTime; // in seconds
    private int totalGamesPlayed;
    private int totalGamesWon;
    private int totalGamesLost;
    private int totalDeaths;
    private int totalLevelsCompleted;
    
    // Collection statistics
    private int totalKeysCollected;
    private int totalCoinsCollected;
    private int totalGemsCollected;
    private int totalPowerUpsCollected;
    private int totalHeartsCollected;
    
    // Combat statistics
    private int totalEnemiesEncountered;
    private int totalDamageTaken;
    private int totalBossesDefeated;
    
    // Movement statistics
    private long totalTilesMoved;
    private long totalDistanceTraveled; // in pixels
    
    // Score statistics
    private int totalScoreEarned;
    private int highestScore;
    private int highestLevel;
    
    // Time statistics
    private float fastestLevelTime; // for any level
    private Map<Integer, Float> fastestLevelTimes; // per level
    private float longestSurvivalTime;
    
    // Achievement tracking
    private int achievementsUnlocked;
    private int totalAchievements;
    
    // Current session statistics
    private transient long sessionStartTime;
    private transient SessionStats currentSession;
    
    private transient Json json;
    
    /**
     * Statistics for current session only.
     */
    public static class SessionStats {
        public int gamesPlayed;
        public int gamesWon;
        public int levelsCompleted;
        public int keysCollected;
        public int coinsCollected;
        public int gemsCollected;
        public int deaths;
        public int scoreEarned;
        public long playTimeSeconds;
        
        public void reset() {
            gamesPlayed = 0;
            gamesWon = 0;
            levelsCompleted = 0;
            keysCollected = 0;
            coinsCollected = 0;
            gemsCollected = 0;
            deaths = 0;
            scoreEarned = 0;
            playTimeSeconds = 0;
        }
    }
    
    public StatisticsTracker() {
        this.fastestLevelTimes = new HashMap<>();
        this.currentSession = new SessionStats();
        this.json = new Json();
        this.json.setOutputType(JsonWriter.OutputType.json);
        
        // Set defaults
        this.fastestLevelTime = Float.MAX_VALUE;
        this.highestScore = 0;
        this.highestLevel = 0;
        this.totalAchievements = 50; // Set based on actual achievement count
        
        load();
        startSession();
    }
    
    /**
     * Starts a new play session.
     */
    public void startSession() {
        sessionStartTime = System.currentTimeMillis();
        currentSession.reset();
    }
    
    /**
     * Ends the current session and saves statistics.
     */
    public void endSession() {
        long sessionDuration = (System.currentTimeMillis() - sessionStartTime) / 1000;
        currentSession.playTimeSeconds = sessionDuration;
        totalPlayTime += sessionDuration;
        save();
    }
    
    // ============== Event Recording Methods ==============
    
    public void recordGameStarted() {
        totalGamesPlayed++;
        currentSession.gamesPlayed++;
    }
    
    public void recordGameWon(int finalScore, int level) {
        totalGamesWon++;
        currentSession.gamesWon++;
        recordScore(finalScore);
        
        if (level > highestLevel) {
            highestLevel = level;
        }
    }
    
    public void recordGameLost() {
        totalGamesLost++;
    }
    
    public void recordDeath() {
        totalDeaths++;
        currentSession.deaths++;
    }
    
    public void recordLevelCompleted(int level, float time) {
        totalLevelsCompleted++;
        currentSession.levelsCompleted++;
        
        // Track fastest time for this level
        Float previousBest = fastestLevelTimes.get(level);
        if (previousBest == null || time < previousBest) {
            fastestLevelTimes.put(level, time);
        }
        
        // Track overall fastest level
        if (time < fastestLevelTime) {
            fastestLevelTime = time;
        }
    }
    
    public void recordKeyCollected() {
        totalKeysCollected++;
        currentSession.keysCollected++;
    }
    
    public void recordCoinCollected(int amount) {
        totalCoinsCollected += amount;
        currentSession.coinsCollected += amount;
    }
    
    public void recordGemCollected(int amount) {
        totalGemsCollected += amount;
        currentSession.gemsCollected += amount;
    }
    
    public void recordPowerUpCollected() {
        totalPowerUpsCollected++;
    }
    
    public void recordHeartCollected() {
        totalHeartsCollected++;
    }
    
    public void recordEnemyEncounter() {
        totalEnemiesEncountered++;
    }
    
    public void recordDamageTaken(int amount) {
        totalDamageTaken += amount;
    }
    
    public void recordBossDefeated() {
        totalBossesDefeated++;
    }
    
    public void recordMovement(float distance) {
        totalDistanceTraveled += (long) distance;
        totalTilesMoved++;
    }
    
    public void recordScore(int score) {
        totalScoreEarned += score;
        currentSession.scoreEarned += score;
        
        if (score > highestScore) {
            highestScore = score;
        }
    }
    
    public void recordSurvivalTime(float time) {
        if (time > longestSurvivalTime) {
            longestSurvivalTime = time;
        }
    }
    
    public void recordAchievementUnlocked() {
        achievementsUnlocked++;
    }
    
    // ============== Calculated Statistics ==============
    
    public float getWinRate() {
        if (totalGamesPlayed == 0) return 0f;
        return (float) totalGamesWon / totalGamesPlayed * 100f;
    }
    
    public float getAverageScore() {
        if (totalGamesPlayed == 0) return 0f;
        return (float) totalScoreEarned / totalGamesPlayed;
    }
    
    public float getAverageDeathsPerGame() {
        if (totalGamesPlayed == 0) return 0f;
        return (float) totalDeaths / totalGamesPlayed;
    }
    
    public float getAchievementProgress() {
        if (totalAchievements == 0) return 0f;
        return (float) achievementsUnlocked / totalAchievements * 100f;
    }
    
    public String getFormattedPlayTime() {
        long hours = totalPlayTime / 3600;
        long minutes = (totalPlayTime % 3600) / 60;
        long seconds = totalPlayTime % 60;
        
        if (hours > 0) {
            return String.format("%dh %dm %ds", hours, minutes, seconds);
        } else if (minutes > 0) {
            return String.format("%dm %ds", minutes, seconds);
        } else {
            return String.format("%ds", seconds);
        }
    }
    
    // ============== Getters ==============
    
    public long getTotalPlayTime() { return totalPlayTime; }
    public int getTotalGamesPlayed() { return totalGamesPlayed; }
    public int getTotalGamesWon() { return totalGamesWon; }
    public int getTotalGamesLost() { return totalGamesLost; }
    public int getTotalDeaths() { return totalDeaths; }
    public int getTotalLevelsCompleted() { return totalLevelsCompleted; }
    public int getTotalKeysCollected() { return totalKeysCollected; }
    public int getTotalCoinsCollected() { return totalCoinsCollected; }
    public int getTotalGemsCollected() { return totalGemsCollected; }
    public int getTotalPowerUpsCollected() { return totalPowerUpsCollected; }
    public int getTotalHeartsCollected() { return totalHeartsCollected; }
    public int getTotalEnemiesEncountered() { return totalEnemiesEncountered; }
    public int getTotalDamageTaken() { return totalDamageTaken; }
    public int getTotalBossesDefeated() { return totalBossesDefeated; }
    public long getTotalTilesMoved() { return totalTilesMoved; }
    public long getTotalDistanceTraveled() { return totalDistanceTraveled; }
    public int getTotalScoreEarned() { return totalScoreEarned; }
    public int getHighestScore() { return highestScore; }
    public int getHighestLevel() { return highestLevel; }
    public float getFastestLevelTime() { return fastestLevelTime == Float.MAX_VALUE ? 0f : fastestLevelTime; }
    public float getLongestSurvivalTime() { return longestSurvivalTime; }
    public int getAchievementsUnlocked() { return achievementsUnlocked; }
    public SessionStats getCurrentSession() { return currentSession; }
    
    public Float getFastestTimeForLevel(int level) {
        return fastestLevelTimes.get(level);
    }
    
    // ============== Persistence ==============
    
    /**
     * Saves statistics to file.
     */
    public void save() {
        try {
            FileHandle file = Gdx.files.local(STATS_FILE);
            String jsonStr = json.prettyPrint(this);
            file.writeString(jsonStr, false);
        } catch (Exception e) {
            Gdx.app.error("StatisticsTracker", "Failed to save statistics", e);
        }
    }
    
    /**
     * Loads statistics from file.
     */
    public void load() {
        try {
            FileHandle file = Gdx.files.local(STATS_FILE);
            if (file.exists()) {
                // Read as JsonValue to avoid recursive instantiation
                String jsonStr = file.readString();
                com.badlogic.gdx.utils.JsonReader reader = new com.badlogic.gdx.utils.JsonReader();
                com.badlogic.gdx.utils.JsonValue root = reader.parse(jsonStr);
                
                // Manually load primitive values
                if (root.has("totalPlayTime")) totalPlayTime = root.getLong("totalPlayTime");
                if (root.has("totalGamesPlayed")) totalGamesPlayed = root.getInt("totalGamesPlayed");
                if (root.has("totalGamesWon")) totalGamesWon = root.getInt("totalGamesWon");
                if (root.has("totalGamesLost")) totalGamesLost = root.getInt("totalGamesLost");
                if (root.has("totalDeaths")) totalDeaths = root.getInt("totalDeaths");
                if (root.has("totalLevelsCompleted")) totalLevelsCompleted = root.getInt("totalLevelsCompleted");
                if (root.has("totalKeysCollected")) totalKeysCollected = root.getInt("totalKeysCollected");
                if (root.has("totalCoinsCollected")) totalCoinsCollected = root.getInt("totalCoinsCollected");
                if (root.has("totalGemsCollected")) totalGemsCollected = root.getInt("totalGemsCollected");
                if (root.has("totalPowerUpsCollected")) totalPowerUpsCollected = root.getInt("totalPowerUpsCollected");
                if (root.has("totalHeartsCollected")) totalHeartsCollected = root.getInt("totalHeartsCollected");
                if (root.has("totalEnemiesEncountered")) totalEnemiesEncountered = root.getInt("totalEnemiesEncountered");
                if (root.has("totalDamageTaken")) totalDamageTaken = root.getInt("totalDamageTaken");
                if (root.has("totalBossesDefeated")) totalBossesDefeated = root.getInt("totalBossesDefeated");
                if (root.has("totalTilesMoved")) totalTilesMoved = root.getLong("totalTilesMoved");
                if (root.has("totalDistanceTraveled")) totalDistanceTraveled = root.getLong("totalDistanceTraveled");
                if (root.has("fastestLevelTime")) fastestLevelTime = root.getFloat("fastestLevelTime");
                if (root.has("highestScore")) highestScore = root.getInt("highestScore");
                if (root.has("highestLevel")) highestLevel = root.getInt("highestLevel");
                if (root.has("achievementsUnlocked")) achievementsUnlocked = root.getInt("achievementsUnlocked");
                
                // Load fastest level times map
                if (root.has("fastestLevelTimes")) {
                    com.badlogic.gdx.utils.JsonValue timesVal = root.get("fastestLevelTimes");
                    for (com.badlogic.gdx.utils.JsonValue entry = timesVal.child; entry != null; entry = entry.next) {
                        try {
                            int levelNum = Integer.parseInt(entry.name);
                            fastestLevelTimes.put(levelNum, entry.asFloat());
                        } catch (NumberFormatException ignored) {}
                    }
                }
            }
        } catch (Exception e) {
            Gdx.app.error("StatisticsTracker", "Failed to load statistics", e);
        }
    }
    
    /**
     * Copies statistics from another tracker.
     */
    private void copyFrom(StatisticsTracker other) {
        this.totalPlayTime = other.totalPlayTime;
        this.totalGamesPlayed = other.totalGamesPlayed;
        this.totalGamesWon = other.totalGamesWon;
        this.totalGamesLost = other.totalGamesLost;
        this.totalDeaths = other.totalDeaths;
        this.totalLevelsCompleted = other.totalLevelsCompleted;
        this.totalKeysCollected = other.totalKeysCollected;
        this.totalCoinsCollected = other.totalCoinsCollected;
        this.totalGemsCollected = other.totalGemsCollected;
        this.totalPowerUpsCollected = other.totalPowerUpsCollected;
        this.totalHeartsCollected = other.totalHeartsCollected;
        this.totalEnemiesEncountered = other.totalEnemiesEncountered;
        this.totalDamageTaken = other.totalDamageTaken;
        this.totalBossesDefeated = other.totalBossesDefeated;
        this.totalTilesMoved = other.totalTilesMoved;
        this.totalDistanceTraveled = other.totalDistanceTraveled;
        this.totalScoreEarned = other.totalScoreEarned;
        this.highestScore = other.highestScore;
        this.highestLevel = other.highestLevel;
        this.fastestLevelTime = other.fastestLevelTime;
        this.longestSurvivalTime = other.longestSurvivalTime;
        this.achievementsUnlocked = other.achievementsUnlocked;
        
        if (other.fastestLevelTimes != null) {
            this.fastestLevelTimes.putAll(other.fastestLevelTimes);
        }
    }
    
    /**
     * Resets all statistics (use with caution!).
     */
    public void resetAll() {
        totalPlayTime = 0;
        totalGamesPlayed = 0;
        totalGamesWon = 0;
        totalGamesLost = 0;
        totalDeaths = 0;
        totalLevelsCompleted = 0;
        totalKeysCollected = 0;
        totalCoinsCollected = 0;
        totalGemsCollected = 0;
        totalPowerUpsCollected = 0;
        totalHeartsCollected = 0;
        totalEnemiesEncountered = 0;
        totalDamageTaken = 0;
        totalBossesDefeated = 0;
        totalTilesMoved = 0;
        totalDistanceTraveled = 0;
        totalScoreEarned = 0;
        highestScore = 0;
        highestLevel = 0;
        fastestLevelTime = Float.MAX_VALUE;
        longestSurvivalTime = 0;
        achievementsUnlocked = 0;
        fastestLevelTimes.clear();
        currentSession.reset();
        save();
    }
}
