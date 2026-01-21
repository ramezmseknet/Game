package de.tum.cit.fop.maze.event;

import de.tum.cit.fop.maze.entity.interactable.Lever;

/**
 * Event fired when a lever is toggled.
 */
public class LeverToggledEvent extends GameEvent {
    
    private final Lever lever;
    private final boolean isOpen;
    
    public LeverToggledEvent(Lever lever, boolean isOpen) {
        super();
        this.lever = lever;
        this.isOpen = isOpen;
    }
    
    public Lever getLever() { return lever; }
    public boolean isOpen() { return isOpen; }
}
