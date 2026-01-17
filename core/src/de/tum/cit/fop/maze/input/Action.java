package de.tum.cit.fop.maze.input;

/**
 * Enum representing all player actions that can be bound to keys.
 */
public enum Action {
    MOVE_UP("Move Up"),
    MOVE_DOWN("Move Down"),
    MOVE_LEFT("Move Left"),
    MOVE_RIGHT("Move Right"),
    RUN("Run (Hold)"),
    PAUSE("Pause/Menu"),
    INTERACT("Interact"),
    ZOOM_IN("Zoom In"),
    ZOOM_OUT("Zoom Out"),
    CONSOLE("Dev Console"),
    ATTACK("Attack");
    
    private final String displayName;
    
    Action(String displayName) {
        this.displayName = displayName;
    }
    
    /**
     * Gets the human-readable name for settings UI.
     * @return Display name
     */
    public String getDisplayName() {
        return displayName;
    }
}
