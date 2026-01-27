package de.tum.cit.fop.maze.world;

/**
 * Enum representing all tile types in the maze.
 * Values correspond to the integer codes in .properties files.
 */
public enum TileType {
    WALL(0, false, "Wall"),
    ENTRY(1, true, "Entry Point"),
    EXIT(2, true, "Exit"),
    TRAP(3, true, "Trap"),
    ENEMY(4, true, "Enemy Spawn"),
    KEY(5, true, "Key"),
    HEART(6, true, "Heart"),
    SPEED_POWERUP(7, true, "Speed Power-up"),
    SHIELD_POWERUP(8, true, "Shield Power-up"),
    FLOOR(9, true, "Floor"),
    SPIKE_TRAP(10, true, "Spike Trap"),
    MOVING_OBSTACLE(11, true, "Moving Obstacle");
    
    private final int code;
    private final boolean walkable;
    private final String displayName;
    
    TileType(int code, boolean walkable, String displayName) {
        this.code = code;
        this.walkable = walkable;
        this.displayName = displayName;
    }
    
    /**
     * Gets the integer code for this tile type.
     * @return The code as used in .properties files
     */
    public int getCode() {
        return code;
    }
    
    /**
     * Checks if entities can walk on this tile.
     * @return true if walkable
     */
    public boolean isWalkable() {
        return walkable;
    }
    
    /**
     * Gets the human-readable name.
     * @return Display name
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Converts an integer code to a TileType.
     * @param code The integer code from .properties file
     * @return The corresponding TileType, or FLOOR if unknown
     */
    public static TileType fromCode(int code) {
        for (TileType type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        return FLOOR;
    }
    
    /**
     * Checks if this tile type spawns an entity (not a static tile).
     * @return true if an entity should be created at this position
     */
    public boolean isEntitySpawn() {
        return this == ENEMY || this == KEY || this == HEART || 
               this == SPEED_POWERUP || this == SHIELD_POWERUP ||
               this == SPIKE_TRAP || this == MOVING_OBSTACLE;
    }
    
    /**
     * Checks if this tile is dangerous to the player.
     * @return true if the tile can damage the player
     */
    public boolean isDangerous() {
        return this == TRAP || this == SPIKE_TRAP || this == MOVING_OBSTACLE;
    }
    
    /**
     * Checks if this tile is a dynamic obstacle (moves or changes state).
     * @return true if the obstacle is dynamic
     */
    public boolean isDynamicObstacle() {
        return this == SPIKE_TRAP || this == MOVING_OBSTACLE;
    }
}
