package de.tum.cit.fop.maze.event;

import de.tum.cit.fop.maze.entity.Player;

/**
 * Event fired when the player dies.
 */
public class PlayerDeathEvent extends GameEvent {
    
    private final Player player;
    private final String cause;
    
    public PlayerDeathEvent(Player player, String cause) {
        super();
        this.player = player;
        this.cause = cause;
    }
    
    public Player getPlayer() { return player; }
    public String getCause() { return cause; }
}
