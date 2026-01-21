package de.tum.cit.fop.maze.event;

import de.tum.cit.fop.maze.entity.ExitDoor;

/**
 * Event fired when an exit door is unlocked.
 */
public class DoorUnlockedEvent extends GameEvent {
    
    private final ExitDoor door;
    
    public DoorUnlockedEvent(ExitDoor door) {
        super();
        this.door = door;
    }
    
    public ExitDoor getDoor() { return door; }
}
