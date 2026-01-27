package de.tum.cit.fop.maze.entity;

/**
 * Enum representing the direction an entity is facing.
 */
public enum Direction {
    UP(0, 1),
    DOWN(0, -1),
    LEFT(-1, 0),
    RIGHT(1, 0),
    NONE(0, 0);
    
    public final int dx;
    public final int dy;
    
    Direction(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }
    
    /**
     * Gets the opposite direction.
     * @return The opposite direction
     */
    public Direction opposite() {
        switch (this) {
            case UP: return DOWN;
            case DOWN: return UP;
            case LEFT: return RIGHT;
            case RIGHT: return LEFT;
            default: return NONE;
        }
    }
    
    /**
     * Gets a direction from delta values.
     * @param dx X change (-1, 0, or 1)
     * @param dy Y change (-1, 0, or 1)
     * @return The corresponding direction
     */
    public static Direction fromDelta(int dx, int dy) {
        if (dy > 0) return UP;
        if (dy < 0) return DOWN;
        if (dx < 0) return LEFT;
        if (dx > 0) return RIGHT;
        return NONE;
    }
}
