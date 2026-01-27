package de.tum.cit.fop.maze.progression;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents the skill tree with all available skills.
 * Skills provide permanent upgrades to the player.
 */
public class SkillTree {
    
    private Map<String, Skill> skills;
    private Map<String, List<String>> prerequisites;
    
    public SkillTree() {
        skills = new HashMap<>();
        prerequisites = new HashMap<>();
        initializeSkills();
    }
    
    /**
     * Initializes all skills in the tree.
     */
    private void initializeSkills() {
        // Movement skills
        addSkill(new Skill("speed_1", "Swift Feet I", 
            "Increase base movement speed by 10%", 
            SkillCategory.MOVEMENT, 3, 0.1f));
        addSkill(new Skill("speed_2", "Swift Feet II", 
            "Increase base movement speed by 15%", 
            SkillCategory.MOVEMENT, 3, 0.15f), "speed_1");
        addSkill(new Skill("speed_3", "Swift Feet III", 
            "Increase base movement speed by 20%", 
            SkillCategory.MOVEMENT, 3, 0.2f), "speed_2");
        
        addSkill(new Skill("sprint_duration", "Marathon Runner", 
            "Sprint 25% longer before tiring", 
            SkillCategory.MOVEMENT, 2, 0.25f));
        addSkill(new Skill("sprint_speed", "Burst Speed", 
            "Sprint 15% faster", 
            SkillCategory.MOVEMENT, 2, 0.15f), "sprint_duration");
        
        // Defense skills
        addSkill(new Skill("health_1", "Vitality I", 
            "Start with +1 extra life", 
            SkillCategory.DEFENSE, 3, 1f));
        addSkill(new Skill("health_2", "Vitality II", 
            "Start with +2 extra lives", 
            SkillCategory.DEFENSE, 3, 2f), "health_1");
        addSkill(new Skill("health_3", "Vitality III", 
            "Start with +3 extra lives", 
            SkillCategory.DEFENSE, 3, 3f), "health_2");
        
        addSkill(new Skill("invincibility", "Extended Protection", 
            "Invincibility after damage lasts 50% longer", 
            SkillCategory.DEFENSE, 2, 0.5f));
        addSkill(new Skill("damage_reduction", "Thick Skin", 
            "10% chance to ignore damage", 
            SkillCategory.DEFENSE, 3, 0.1f), "invincibility");
        
        addSkill(new Skill("shield_duration", "Shield Expert", 
            "Shield power-ups last 30% longer", 
            SkillCategory.DEFENSE, 2, 0.3f));
        
        // Collection skills
        addSkill(new Skill("magnet_1", "Magnetic Personality", 
            "Collect items from slightly farther away", 
            SkillCategory.COLLECTION, 2, 1.2f));
        addSkill(new Skill("magnet_2", "Strong Magnet", 
            "Collect items from much farther away", 
            SkillCategory.COLLECTION, 2, 1.5f), "magnet_1");
        
        addSkill(new Skill("bonus_score", "Score Multiplier", 
            "Gain 20% more score from all sources", 
            SkillCategory.COLLECTION, 3, 0.2f));
        addSkill(new Skill("bonus_xp", "Quick Learner", 
            "Gain 15% more XP", 
            SkillCategory.COLLECTION, 3, 0.15f));
        
        addSkill(new Skill("powerup_duration", "Power Extender", 
            "All power-ups last 25% longer", 
            SkillCategory.COLLECTION, 2, 0.25f));
        
        // Utility skills
        addSkill(new Skill("key_sense", "Key Sense", 
            "See key locations on minimap", 
            SkillCategory.UTILITY, 1, 1f));
        addSkill(new Skill("enemy_sense", "Enemy Awareness", 
            "See enemies on minimap from farther", 
            SkillCategory.UTILITY, 2, 1.5f));
        addSkill(new Skill("trap_sense", "Trap Detection", 
            "Traps glow when nearby", 
            SkillCategory.UTILITY, 2, 1f), "key_sense");
        
        addSkill(new Skill("cooldown_reduction", "Efficient", 
            "Reduce ability cooldowns by 15%", 
            SkillCategory.UTILITY, 2, 0.15f));
    }
    
    /**
     * Adds a skill without prerequisites.
     */
    private void addSkill(Skill skill) {
        skills.put(skill.getId(), skill);
        prerequisites.put(skill.getId(), new ArrayList<>());
    }
    
    /**
     * Adds a skill with prerequisites.
     */
    private void addSkill(Skill skill, String... prereqs) {
        skills.put(skill.getId(), skill);
        List<String> prereqList = new ArrayList<>();
        for (String p : prereqs) {
            prereqList.add(p);
        }
        prerequisites.put(skill.getId(), prereqList);
    }
    
    /**
     * Gets a skill by ID.
     * @param skillId The skill ID
     * @return The skill, or null if not found
     */
    public Skill getSkill(String skillId) {
        return skills.get(skillId);
    }
    
    /**
     * Gets all skills.
     * @return Map of all skills
     */
    public Map<String, Skill> getAllSkills() {
        return skills;
    }
    
    /**
     * Gets skills in a category.
     * @param category The category
     * @return List of skills
     */
    public List<Skill> getSkillsByCategory(SkillCategory category) {
        List<Skill> result = new ArrayList<>();
        for (Skill skill : skills.values()) {
            if (skill.getCategory() == category) {
                result.add(skill);
            }
        }
        return result;
    }
    
    /**
     * Checks if prerequisites for a skill are met.
     * @param skillId The skill to check
     * @return true if all prerequisites are met
     */
    public boolean arePrerequisitesMet(String skillId) {
        List<String> prereqs = prerequisites.get(skillId);
        if (prereqs == null || prereqs.isEmpty()) return true;
        
        for (String prereqId : prereqs) {
            Skill prereq = skills.get(prereqId);
            if (prereq == null || prereq.getCurrentLevel() == 0) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Gets the prerequisite skills for a skill.
     * @param skillId The skill ID
     * @return List of prerequisite skill IDs
     */
    public List<String> getPrerequisites(String skillId) {
        return prerequisites.getOrDefault(skillId, new ArrayList<>());
    }
    
    /**
     * Gets total skill points spent.
     * @return Total points spent
     */
    public int getTotalPointsSpent() {
        int total = 0;
        for (Skill skill : skills.values()) {
            total += skill.getCurrentLevel();
        }
        return total;
    }
    
    /**
     * Resets all skills.
     */
    public void resetAll() {
        for (Skill skill : skills.values()) {
            skill.reset();
        }
    }
    
    /**
     * Gets skill levels for saving.
     * @return Map of skill ID to level
     */
    public Map<String, Integer> getSkillLevels() {
        Map<String, Integer> levels = new HashMap<>();
        for (Map.Entry<String, Skill> entry : skills.entrySet()) {
            if (entry.getValue().getCurrentLevel() > 0) {
                levels.put(entry.getKey(), entry.getValue().getCurrentLevel());
            }
        }
        return levels;
    }
    
    /**
     * Loads skill levels from save data with validation.
     * @param levels Map of skill ID to level
     */
    public void loadSkillLevels(Map<String, Integer> levels) {
        for (Map.Entry<String, Integer> entry : levels.entrySet()) {
            Skill skill = skills.get(entry.getKey());
            if (skill != null) {
                // Validate level doesn't exceed maxLevel
                int targetLevel = Math.min(entry.getValue(), skill.getMaxLevel());
                // Validate level is positive
                targetLevel = Math.max(0, targetLevel);
                // Only upgrade if not already at target (prevents corruption)
                for (int i = skill.getCurrentLevel(); i < targetLevel; i++) {
                    if (skill.canUpgrade()) {
                        skill.upgrade();
                    } else {
                        break; // Stop if skill can't be upgraded further
                    }
                }
            }
        }
    }
    
    /**
     * Gets the total value of a skill type.
     * @param skillId The skill ID
     * @return The total value (base * level)
     */
    public float getSkillValue(String skillId) {
        Skill skill = skills.get(skillId);
        if (skill == null || skill.getCurrentLevel() == 0) return 0f;
        return skill.getValue() * skill.getCurrentLevel();
    }
    
    /**
     * Checks if a skill is unlocked (level > 0).
     * @param skillId The skill ID
     * @return true if unlocked
     */
    public boolean isUnlocked(String skillId) {
        Skill skill = skills.get(skillId);
        return skill != null && skill.getCurrentLevel() > 0;
    }
}
