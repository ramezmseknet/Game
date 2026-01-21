package de.tum.cit.fop.maze.entity.collectible;

/**
 * Types of collectible items in the game.
 */
public enum CollectibleType {
    KEY,            // Required to unlock exit
    HEART,          // Extra life
    SPEED_BOOST,    // Temporary speed increase
    SHIELD,         // Temporary invincibility
    SCORE_BONUS,    // Extra points
    COIN,           // Currency for upgrades
    GEM,            // Rare valuable item
    TIME_BONUS,     // Extra time (for timed modes)
    FREEZE,         // Freezes enemies temporarily
    MAGNET,         // Attracts nearby collectibles
    DOUBLE_POINTS,  // 2x score for duration
    INVISIBILITY,   // Enemies can't see you
    REVEAL_MAP      // Shows entire map briefly
}
