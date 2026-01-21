package de.tum.cit.fop.maze.event;

/**
 * Event fired when a level is completed.
 */
public class LevelCompletedEvent extends GameEvent {
    
    private final String levelFile;
    private final int score;
    private final float completionTime;
    private final int keysCollected;
    private final int totalKeys;
    
    public LevelCompletedEvent(String levelFile, int score, float completionTime, 
                                int keysCollected, int totalKeys) {
        super();
        this.levelFile = levelFile;
        this.score = score;
        this.completionTime = completionTime;
        this.keysCollected = keysCollected;
        this.totalKeys = totalKeys;
    }
    
    public String getLevelFile() { return levelFile; }
    public int getScore() { return score; }
    public float getCompletionTime() { return completionTime; }
    public int getKeysCollected() { return keysCollected; }
    public int getTotalKeys() { return totalKeys; }
}
