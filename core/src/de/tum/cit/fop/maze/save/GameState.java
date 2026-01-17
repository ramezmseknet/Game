package de.tum.cit.fop.maze.save;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents the complete game state for saving/loading.
 */
public class GameState {

    public String levelFile;

    public float playerX;
    public float playerY;
    public int lives;
    public float stateTime;

    public Set<String> collectedKeys = new HashSet<>();

    public long playTime;
    public int score;

    public long saveTime;
    public String saveName;

    public GameState() {
        saveTime = System.currentTimeMillis();
    }


    public GameState(String levelFile, float playerX, float playerY, int lives) {
        this();
        this.levelFile = levelFile;
        this.playerX = playerX;
        this.playerY = playerY;
        this.lives = lives;
    }


    public void addCollectedKey(String keyId) {
        collectedKeys.add(keyId);
    }


    public boolean hasKey(String keyId) {
        return collectedKeys.contains(keyId);
    }
}
