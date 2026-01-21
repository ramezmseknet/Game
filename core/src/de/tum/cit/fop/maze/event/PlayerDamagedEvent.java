package de.tum.cit.fop.maze.event;

import de.tum.cit.fop.maze.entity.Player;

/**
 * Event fired when the player takes damage.
 */
public class PlayerDamagedEvent extends GameEvent {
    
    private final Player player;
    private final int damageAmount;
    private final int newLives;
    private final String source;
    
    public PlayerDamagedEvent(Player player, int damageAmount, String source) {
        super();
        this.player = player;
        this.damageAmount = damageAmount;
        this.newLives = player.getLives();
        this.source = source;
    }
    
    public Player getPlayer() { return player; }
    public int getDamageAmount() { return damageAmount; }
    public int getNewLives() { return newLives; }
    public String getSource() { return source; }
}
