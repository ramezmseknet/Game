package de.tum.cit.fop.maze.world;

/**
 * Manages game difficulty settings and scaling.
 * Used primarily in endless mode for progressive difficulty.
 */
public class DifficultyManager {
    
    /**
     * Predefined difficulty levels.
     */
    public enum DifficultyLevel {
        EASY(0.7f, "Easy"),
        NORMAL(1.0f, "Normal"),
        HARD(1.3f, "Hard"),
        NIGHTMARE(1.6f, "Nightmare");
        
        private final float multiplier;
        private final String displayName;
        
        DifficultyLevel(float multiplier, String displayName) {
            this.multiplier = multiplier;
            this.displayName = displayName;
        }
        
        public float getMultiplier() { return multiplier; }
        public String getDisplayName() { return displayName; }
    }
    
    private DifficultyLevel baseDifficulty;
    private float dynamicMultiplier;
    private float elapsedTime;
    private int wavesCompleted;
    
    private float timeScalingRate;
    private float waveScalingRate;
    private float maxDifficultyMultiplier;
    
    private float enemySpeedMultiplier;
    private float enemyDamageMultiplier;
    private float enemyCountMultiplier;
    private float trapDensityMultiplier;
    private float powerUpFrequencyMultiplier;
    
    /**
     * Creates a difficulty manager with default settings.
     */
    public DifficultyManager() {
        this.baseDifficulty = DifficultyLevel.NORMAL;
        this.dynamicMultiplier = 1.0f;
        this.elapsedTime = 0f;
        this.wavesCompleted = 0;
        
        this.timeScalingRate = 0.01f;    // 1% per second
        this.waveScalingRate = 0.1f;     // 10% per wave
        this.maxDifficultyMultiplier = 3.0f;
        
        recalculateMultipliers();
    }
    
    /**
     * Updates difficulty based on elapsed time (for endless mode).
     * @param deltaTime Frame delta time
     */
    public void update(float deltaTime) {
        elapsedTime += deltaTime;
        
        float timeScaling = 1.0f + (elapsedTime * timeScalingRate);
        
        float waveScaling = 1.0f + (wavesCompleted * waveScalingRate);
        
        dynamicMultiplier = Math.min(
            baseDifficulty.getMultiplier() * timeScaling * waveScaling,
            maxDifficultyMultiplier
        );
        
        recalculateMultipliers();
    }
    
    /**
     * Called when a wave/level is completed.
     */
    public void onWaveCompleted() {
        wavesCompleted++;
        recalculateMultipliers();
    }
    
    /**
     * Recalculates all difficulty multipliers.
     */
    private void recalculateMultipliers() {
        float base = dynamicMultiplier;
        
        enemySpeedMultiplier = 1.0f + (base - 1.0f) * 0.5f;      // 50% of base scaling
        enemyDamageMultiplier = 1.0f + (base - 1.0f) * 0.3f;     // 30% of base scaling
        enemyCountMultiplier = base;                              // Full scaling
        trapDensityMultiplier = 1.0f + (base - 1.0f) * 0.7f;     // 70% of base scaling
        powerUpFrequencyMultiplier = 1.0f / base;                 // Inverse - fewer power-ups at higher difficulty
    }
    
    /**
     * Sets the base difficulty level.
     * @param level The difficulty level
     */
    public void setBaseDifficulty(DifficultyLevel level) {
        this.baseDifficulty = level;
        recalculateMultipliers();
    }
    
    /**
     * Gets the base difficulty level.
     * @return Current base difficulty
     */
    public DifficultyLevel getBaseDifficulty() {
        return baseDifficulty;
    }
    
    /**
     * Gets the current dynamic difficulty multiplier.
     * @return Overall difficulty multiplier
     */
    public float getDynamicMultiplier() {
        return dynamicMultiplier;
    }
    
    /**
     * Gets the enemy speed multiplier.
     * @return Speed multiplier for enemies
     */
    public float getEnemySpeedMultiplier() {
        return enemySpeedMultiplier;
    }
    
    /**
     * Gets the enemy damage multiplier.
     * @return Damage multiplier for enemies
     */
    public float getEnemyDamageMultiplier() {
        return enemyDamageMultiplier;
    }
    
    /**
     * Gets the enemy count multiplier.
     * @return Multiplier for number of enemies
     */
    public float getEnemyCountMultiplier() {
        return enemyCountMultiplier;
    }
    
    /**
     * Gets the trap density multiplier.
     * @return Multiplier for trap frequency
     */
    public float getTrapDensityMultiplier() {
        return trapDensityMultiplier;
    }
    
    /**
     * Gets the power-up frequency multiplier.
     * @return Multiplier for power-up spawns
     */
    public float getPowerUpFrequencyMultiplier() {
        return powerUpFrequencyMultiplier;
    }
    
    /**
     * Gets the number of waves completed.
     * @return Wave count
     */
    public int getWavesCompleted() {
        return wavesCompleted;
    }
    
    /**
     * Gets total elapsed time.
     * @return Time in seconds
     */
    public float getElapsedTime() {
        return elapsedTime;
    }
    
    /**
     * Resets difficulty to starting values.
     */
    public void reset() {
        this.dynamicMultiplier = baseDifficulty.getMultiplier();
        this.elapsedTime = 0f;
        this.wavesCompleted = 0;
        recalculateMultipliers();
    }
    
    /**
     * Gets a descriptive string of current difficulty.
     * @return Difficulty description
     */
    public String getDifficultyDescription() {
        if (dynamicMultiplier < 1.0f) return "Easy";
        if (dynamicMultiplier < 1.3f) return "Normal";
        if (dynamicMultiplier < 1.6f) return "Hard";
        if (dynamicMultiplier < 2.0f) return "Very Hard";
        if (dynamicMultiplier < 2.5f) return "Extreme";
        return "Nightmare";
    }
    
    /**
     * Calculates recommended maze size for current difficulty.
     * @return Maze width/height in tiles
     */
    public int getRecommendedMazeSize() {
        int baseSize = 15;
        int sizeIncrease = wavesCompleted / 3; // Increase every 3 waves
        return Math.min(baseSize + sizeIncrease * 2, 30); // Cap at 30
    }
    
    /**
     * Calculates recommended enemy count for current difficulty.
     * @param mazeSize The maze size
     * @return Number of enemies to spawn
     */
    public int getRecommendedEnemyCount(int mazeSize) {
        int baseCount = 2 + wavesCompleted;
        return (int) (baseCount * enemyCountMultiplier);
    }
}
