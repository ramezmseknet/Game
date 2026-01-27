package de.tum.cit.fop.maze.world;

import java.util.ArrayList;
import java.util.List;

/**
 * Defines challenge modifiers that alter gameplay rules.
 */
public class ChallengeModifier {
    
    /**
     * Predefined challenge types.
     */
    public enum ModifierType {
        DOUBLE_ENEMIES("Double Enemies", "Twice as many enemies spawn", 1.5f),
        FAST_ENEMIES("Fast Enemies", "Enemies move 50% faster", 1.3f),
        ONE_HIT("One Hit Wonder", "You die in one hit", 2.0f),
        NO_POWERUPS("No Power-Ups", "Power-ups don't spawn", 1.2f),
        DARKNESS("Darkness", "Limited visibility", 1.4f),
        
        HALF_TIME("Half Time", "Half the normal time limit", 1.3f),
        COUNTDOWN("Countdown", "Time constantly ticking down", 1.2f),
        
        ALL_KEYS("Key Collector", "Must collect all keys (no skipping)", 1.1f),
        COIN_HUNTER("Coin Hunter", "Must collect 80% of coins", 1.2f),
        
        NO_RUNNING("No Running", "Sprint is disabled", 1.1f),
        REVERSED("Reversed Controls", "Controls are inverted", 1.4f),
        
        EXTRA_LIVES("Extra Lives", "Start with 5 lives", 0.8f),
        SLOW_ENEMIES("Slow Enemies", "Enemies move 50% slower", 0.7f),
        LONG_POWERUPS("Extended Power-Ups", "Power-ups last twice as long", 0.8f);
        
        private final String displayName;
        private final String description;
        private final float scoreMultiplier;
        
        ModifierType(String displayName, String description, float scoreMultiplier) {
            this.displayName = displayName;
            this.description = description;
            this.scoreMultiplier = scoreMultiplier;
        }
        
        public String getDisplayName() { return displayName; }
        public String getDescription() { return description; }
        public float getScoreMultiplier() { return scoreMultiplier; }
    }
    
    private final List<ModifierType> activeModifiers;
    private float combinedScoreMultiplier;
    
    public ChallengeModifier() {
        this.activeModifiers = new ArrayList<>();
        this.combinedScoreMultiplier = 1.0f;
    }
    
    /**
     * Adds a modifier to the active list.
     * @param type The modifier to add
     */
    public void addModifier(ModifierType type) {
        if (!activeModifiers.contains(type)) {
            activeModifiers.add(type);
            recalculateMultiplier();
        }
    }
    
    /**
     * Removes a modifier from the active list.
     * @param type The modifier to remove
     */
    public void removeModifier(ModifierType type) {
        activeModifiers.remove(type);
        recalculateMultiplier();
    }
    
    /**
     * Clears all active modifiers.
     */
    public void clearModifiers() {
        activeModifiers.clear();
        combinedScoreMultiplier = 1.0f;
    }
    
    /**
     * Checks if a modifier is active.
     */
    public boolean hasModifier(ModifierType type) {
        return activeModifiers.contains(type);
    }
    
    private void recalculateMultiplier() {
        combinedScoreMultiplier = 1.0f;
        for (ModifierType mod : activeModifiers) {
            combinedScoreMultiplier *= mod.getScoreMultiplier();
        }
    }
    
    /**
     * Gets the enemy count multiplier based on active modifiers.
     */
    public float getEnemyCountMultiplier() {
        if (hasModifier(ModifierType.DOUBLE_ENEMIES)) return 2.0f;
        return 1.0f;
    }
    
    /**
     * Gets the enemy speed multiplier based on active modifiers.
     */
    public float getEnemySpeedMultiplier() {
        float mult = 1.0f;
        if (hasModifier(ModifierType.FAST_ENEMIES)) mult *= 1.5f;
        if (hasModifier(ModifierType.SLOW_ENEMIES)) mult *= 0.5f;
        return mult;
    }
    
    /**
     * Gets the time multiplier based on active modifiers.
     */
    public float getTimeMultiplier() {
        if (hasModifier(ModifierType.HALF_TIME)) return 0.5f;
        return 1.0f;
    }
    
    /**
     * Gets the player max lives based on active modifiers.
     */
    public int getModifiedLives(int baseLives) {
        if (hasModifier(ModifierType.ONE_HIT)) return 1;
        if (hasModifier(ModifierType.EXTRA_LIVES)) return baseLives + 2;
        return baseLives;
    }
    
    /**
     * Gets the power-up duration multiplier.
     */
    public float getPowerUpDurationMultiplier() {
        if (hasModifier(ModifierType.LONG_POWERUPS)) return 2.0f;
        return 1.0f;
    }
    
    /**
     * Checks if power-ups should spawn.
     */
    public boolean powerUpsEnabled() {
        return !hasModifier(ModifierType.NO_POWERUPS);
    }
    
    /**
     * Checks if running is allowed.
     */
    public boolean runningEnabled() {
        return !hasModifier(ModifierType.NO_RUNNING);
    }
    
    /**
     * Checks if controls are reversed.
     */
    public boolean areControlsReversed() {
        return hasModifier(ModifierType.REVERSED);
    }
    
    /**
     * Checks if darkness mode is active.
     */
    public boolean isDarknessEnabled() {
        return hasModifier(ModifierType.DARKNESS);
    }
    
    /**
     * Gets coin collection requirement (0-1, 0 means none required).
     */
    public float getCoinRequirement() {
        if (hasModifier(ModifierType.COIN_HUNTER)) return 0.8f;
        return 0f;
    }
    
    public List<ModifierType> getActiveModifiers() {
        return new ArrayList<>(activeModifiers);
    }
    
    public float getCombinedScoreMultiplier() {
        return combinedScoreMultiplier;
    }
    
    /**
     * Creates a preset challenge configuration.
     */
    public static ChallengeModifier createPreset(String presetName) {
        ChallengeModifier modifier = new ChallengeModifier();
        
        switch (presetName.toLowerCase()) {
            case "nightmare":
                modifier.addModifier(ModifierType.DOUBLE_ENEMIES);
                modifier.addModifier(ModifierType.FAST_ENEMIES);
                modifier.addModifier(ModifierType.NO_POWERUPS);
                break;
            case "speedster":
                modifier.addModifier(ModifierType.HALF_TIME);
                modifier.addModifier(ModifierType.SLOW_ENEMIES);
                break;
            case "collector":
                modifier.addModifier(ModifierType.ALL_KEYS);
                modifier.addModifier(ModifierType.COIN_HUNTER);
                modifier.addModifier(ModifierType.EXTRA_LIVES);
                break;
            case "hardcore":
                modifier.addModifier(ModifierType.ONE_HIT);
                modifier.addModifier(ModifierType.NO_POWERUPS);
                modifier.addModifier(ModifierType.DARKNESS);
                break;
            case "relaxed":
                modifier.addModifier(ModifierType.EXTRA_LIVES);
                modifier.addModifier(ModifierType.SLOW_ENEMIES);
                modifier.addModifier(ModifierType.LONG_POWERUPS);
                break;
        }
        
        return modifier;
    }
    
    @Override
    public String toString() {
        if (activeModifiers.isEmpty()) return "No modifiers";
        
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < activeModifiers.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(activeModifiers.get(i).getDisplayName());
        }
        sb.append(String.format(" (%.1fx score)", combinedScoreMultiplier));
        return sb.toString();
    }
}
