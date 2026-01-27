package de.tum.cit.fop.maze.progression;

/**
 * Categories for organizing skills in the skill tree.
 */
public enum SkillCategory {
    
    MOVEMENT("Movement", "Skills that affect player movement speed and abilities"),
    DEFENSE("Defense", "Skills that improve survivability and reduce damage"),
    COLLECTION("Collection", "Skills that help with collecting items and gaining rewards"),
    UTILITY("Utility", "Skills that provide useful gameplay benefits");
    
    private final String displayName;
    private final String description;
    
    SkillCategory(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDescription() {
        return description;
    }
}
