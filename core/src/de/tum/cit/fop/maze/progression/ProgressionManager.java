package de.tum.cit.fop.maze.progression;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;

/**
 * Manages player progression, XP, levels, and skill points.
 * Persistent across game sessions.
 */
public class ProgressionManager {
    
    private static ProgressionManager instance;
    
    // XP and leveling
    private int currentXP;
    private int totalXP;
    private int level;
    private int skillPoints;
    
    // XP curve parameters
    private static final int BASE_XP_PER_LEVEL = 100;
    private static final float XP_GROWTH_RATE = 1.5f;
    
    // Skill tree
    private SkillTree skillTree;
    
    // Listeners
    private Array<ProgressionListener> listeners;
    
    /**
     * Private constructor for singleton.
     */
    private ProgressionManager() {
        listeners = new Array<>();
        skillTree = new SkillTree();
        load();
    }
    
    /**
     * Gets the singleton instance.
     * @return The ProgressionManager instance
     */
    public static ProgressionManager getInstance() {
        if (instance == null) {
            instance = new ProgressionManager();
        }
        return instance;
    }
    
    /**
     * Adds XP and checks for level up.
     * @param amount XP amount to add
     */
    public void addXP(int amount) {
        if (amount <= 0) return;
        
        currentXP += amount;
        totalXP += amount;
        
        // Check for level up
        int xpNeeded = getXPForNextLevel();
        while (currentXP >= xpNeeded) {
            currentXP -= xpNeeded;
            levelUp();
            xpNeeded = getXPForNextLevel();
        }
        
        save();
        notifyXPGained(amount);
    }
    
    /**
     * Handles level up.
     */
    private void levelUp() {
        level++;
        skillPoints++;
        
        Gdx.app.log("ProgressionManager", "Level up! Now level " + level);
        notifyLevelUp();
    }
    
    /**
     * Gets XP required for next level.
     * @return XP needed
     */
    public int getXPForNextLevel() {
        // Guard against corrupted save with level=0 or negative
        int safeLevel = Math.max(1, level);
        return (int) (BASE_XP_PER_LEVEL * Math.pow(XP_GROWTH_RATE, safeLevel - 1));
    }
    
    /**
     * Gets progress to next level as percentage.
     * @return Progress (0-1)
     */
    public float getLevelProgress() {
        return (float) currentXP / getXPForNextLevel();
    }
    
    /**
     * Spends a skill point on a skill.
     * @param skillId The skill to upgrade
     * @return true if successful
     */
    public boolean spendSkillPoint(String skillId) {
        if (skillPoints <= 0) return false;
        
        Skill skill = skillTree.getSkill(skillId);
        if (skill == null) return false;
        
        if (!skill.canUpgrade()) return false;
        
        // Check prerequisites
        if (!skillTree.arePrerequisitesMet(skillId)) return false;
        
        skill.upgrade();
        skillPoints--;
        save();
        notifySkillUnlocked(skill);
        
        return true;
    }
    
    /**
     * Resets all skill points (respec).
     */
    public void resetSkills() {
        int totalSpent = skillTree.getTotalPointsSpent();
        skillTree.resetAll();
        skillPoints += totalSpent;
        save();
    }
    
    /**
     * Gets the skill tree.
     * @return The skill tree
     */
    public SkillTree getSkillTree() {
        return skillTree;
    }
    
    /**
     * Gets current level.
     * @return Level
     */
    public int getLevel() {
        return level;
    }
    
    /**
     * Gets current XP.
     * @return Current XP in level
     */
    public int getCurrentXP() {
        return currentXP;
    }
    
    /**
     * Gets total XP earned.
     * @return Total XP
     */
    public int getTotalXP() {
        return totalXP;
    }
    
    /**
     * Gets available skill points.
     * @return Skill points
     */
    public int getSkillPoints() {
        return skillPoints;
    }
    
    /**
     * Calculates XP reward for completing a level.
     * @param levelNumber The level completed
     * @param timeTaken Time in seconds
     * @param livesRemaining Lives left
     * @param score Score achieved
     * @return XP reward
     */
    public int calculateLevelXP(int levelNumber, float timeTaken, int livesRemaining, int score) {
        int baseXP = 50 + levelNumber * 25;
        
        // Time bonus (faster = more XP)
        float timeMultiplier = Math.max(0.5f, 2f - timeTaken / 120f);
        
        // Lives bonus
        int livesBonus = livesRemaining * 10;
        
        // Score bonus
        int scoreBonus = score / 100;
        
        return (int) (baseXP * timeMultiplier + livesBonus + scoreBonus);
    }
    
    /**
     * Saves progression to file.
     */
    public void save() {
        try {
            FileHandle file = Gdx.files.local("saves/progression.json");
            ProgressionData data = new ProgressionData();
            data.currentXP = currentXP;
            data.totalXP = totalXP;
            data.level = level;
            data.skillPoints = skillPoints;
            data.skillLevels = skillTree.getSkillLevels();
            
            Json json = new Json();
            json.setOutputType(JsonWriter.OutputType.json);
            file.writeString(json.prettyPrint(data), false);
        } catch (Exception e) {
            Gdx.app.error("ProgressionManager", "Failed to save: " + e.getMessage());
        }
    }
    
    /**
     * Loads progression from file.
     */
    private void load() {
        try {
            FileHandle file = Gdx.files.local("saves/progression.json");
            if (file.exists()) {
                Json json = new Json();
                ProgressionData data = json.fromJson(ProgressionData.class, file);
                
                this.currentXP = data.currentXP;
                this.totalXP = data.totalXP;
                this.level = data.level;
                this.skillPoints = data.skillPoints;
                
                if (data.skillLevels != null) {
                    skillTree.loadSkillLevels(data.skillLevels);
                }
                
                Gdx.app.log("ProgressionManager", "Loaded progression: Level " + level);
            } else {
                // Initialize defaults
                this.currentXP = 0;
                this.totalXP = 0;
                this.level = 1;
                this.skillPoints = 0;
            }
        } catch (Exception e) {
            Gdx.app.error("ProgressionManager", "Failed to load: " + e.getMessage());
            this.currentXP = 0;
            this.totalXP = 0;
            this.level = 1;
            this.skillPoints = 0;
        }
    }
    
    // Listener management
    public void addListener(ProgressionListener listener) {
        listeners.add(listener);
    }
    
    public void removeListener(ProgressionListener listener) {
        listeners.removeValue(listener, true);
    }
    
    private void notifyXPGained(int amount) {
        for (ProgressionListener l : listeners) {
            l.onXPGained(amount, currentXP, getXPForNextLevel());
        }
    }
    
    private void notifyLevelUp() {
        for (ProgressionListener l : listeners) {
            l.onLevelUp(level, skillPoints);
        }
    }
    
    private void notifySkillUnlocked(Skill skill) {
        for (ProgressionListener l : listeners) {
            l.onSkillUnlocked(skill);
        }
    }
    
    /**
     * Data class for serialization.
     */
    public static class ProgressionData {
        public int currentXP;
        public int totalXP;
        public int level;
        public int skillPoints;
        public java.util.Map<String, Integer> skillLevels;
    }
}
