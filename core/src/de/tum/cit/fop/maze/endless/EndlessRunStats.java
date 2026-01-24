package de.tum.cit.fop.maze.endless;

/**
 * Tracks statistics for an endless mode run.
 * Includes score, time, waves completed, and other metrics.
 */
public class EndlessRunStats {

    private int score;
    private int wavesCompleted;
    private float totalTime;
    private int enemiesDefeated;
    private int heartsCollected;
    private int totalDamageTaken;
    private int coinsCollected;

    // Per-wave tracking
    private float currentWaveTime;
    private int currentWaveScore;

    // Combo system
    private int comboMultiplier;
    private float comboTimer;
    private static final float COMBO_TIMEOUT = 3f;
    private static final int MAX_COMBO = 10;

    public EndlessRunStats() {
        reset();
    }

    /**
     * Resets all stats for a new run.
     */
    public void reset() {
        score = 0;
        wavesCompleted = 0;
        totalTime = 0f;
        enemiesDefeated = 0;
        heartsCollected = 0;
        totalDamageTaken = 0;
        coinsCollected = 0;
        currentWaveTime = 0f;
        currentWaveScore = 0;
        comboMultiplier = 1;
        comboTimer = 0f;
    }

    /**
     * Updates time-based stats.
     * @param deltaTime Frame delta time
     */
    public void update(float deltaTime) {
        totalTime += deltaTime;
        currentWaveTime += deltaTime;

        // Update combo timer
        if (comboTimer > 0) {
            comboTimer -= deltaTime;
            if (comboTimer <= 0) {
                comboMultiplier = 1;
            }
        }
    }

    /**
     * Adds score with combo multiplier.
     * @param baseScore Base score amount
     */
    public void addScore(int baseScore) {
        int earnedScore = baseScore * comboMultiplier;
        score += earnedScore;
        currentWaveScore += earnedScore;

        // Refresh combo timer
        comboTimer = COMBO_TIMEOUT;
        comboMultiplier = Math.min(comboMultiplier + 1, MAX_COMBO);
    }

    /**
     * Records wave completion.
     * @param bonusScore Bonus score for completing the wave
     */
    public void onWaveCompleted(int bonusScore) {
        wavesCompleted++;

        // Time bonus: faster completion = more points
        float timeBonus = Math.max(0, 60f - currentWaveTime) * 10;
        score += bonusScore + (int) timeBonus;

        // Reset wave-specific tracking
        currentWaveTime = 0f;
        currentWaveScore = 0;
    }

    /**
     * Records enemy defeat.
     * @param scoreValue Score value of the enemy
     */
    public void onEnemyDefeated(int scoreValue) {
        enemiesDefeated++;
        addScore(scoreValue);
    }

    /**
     * Records heart collection.
     */
    public void onHeartCollected() {
        heartsCollected++;
        addScore(50);
    }

    /**
     * Records coin collection.
     * @param value Coin value
     */
    public void onCoinCollected(int value) {
        coinsCollected += value;
        addScore(value);
    }

    /**
     * Records damage taken (breaks combo).
     * @param damage Damage amount
     */
    public void onDamageTaken(int damage) {
        totalDamageTaken += damage;
        // Break combo on damage
        comboMultiplier = 1;
        comboTimer = 0f;
    }

    // Getters
    public int getScore() { return score; }
    public int getWavesCompleted() { return wavesCompleted; }
    public float getTotalTime() { return totalTime; }
    public int getEnemiesDefeated() { return enemiesDefeated; }
    public int getHeartsCollected() { return heartsCollected; }
    public int getTotalDamageTaken() { return totalDamageTaken; }
    public int getCoinsCollected() { return coinsCollected; }
    public float getCurrentWaveTime() { return currentWaveTime; }
    public int getCurrentWaveScore() { return currentWaveScore; }
    public int getComboMultiplier() { return comboMultiplier; }
    public float getComboTimer() { return comboTimer; }

    /**
     * Gets formatted total time string.
     * @return Time as MM:SS
     */
    public String getFormattedTime() {
        int minutes = (int) (totalTime / 60);
        int seconds = (int) (totalTime % 60);
        return String.format("%02d:%02d", minutes, seconds);
    }

    /**
     * Gets formatted wave time string.
     * @return Time as M:SS
     */
    public String getFormattedWaveTime() {
        int minutes = (int) (currentWaveTime / 60);
        int seconds = (int) (currentWaveTime % 60);
        return String.format("%d:%02d", minutes, seconds);
    }
}
