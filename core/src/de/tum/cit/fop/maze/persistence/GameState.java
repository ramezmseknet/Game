package de.tum.cit.fop.maze.persistence;

import com.badlogic.gdx.math.Vector2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Serializable snapshot of the game state for saving/loading.
 */
public class GameState implements Serializable {
    
    private static final long serialVersionUID = 1L;
    

    private String levelFile;
    private long seed;
    
    private float playerX;
    private float playerY;
    private int playerLives;
    private Set<String> collectedKeyIds;
    private boolean hasShield;
    private float shieldTimeRemaining;
    private boolean hasSpeedBoost;
    private float speedBoostTimeRemaining;
    
    private int score;
    private float elapsedTime;
    
    private List<EnemyState> enemyStates;
    
    private long saveTimestamp;
    private String saveName;
    
    public GameState() {
        this.collectedKeyIds = new HashSet<>();
        this.enemyStates = new ArrayList<>();
        this.saveTimestamp = System.currentTimeMillis();
    }
    
    public GameState setLevelFile(String levelFile) {
        this.levelFile = levelFile;
        return this;
    }
    
    public GameState setSeed(long seed) {
        this.seed = seed;
        return this;
    }
    
    public GameState setPlayerPosition(float x, float y) {
        this.playerX = x;
        this.playerY = y;
        return this;
    }
    
    public GameState setPlayerLives(int lives) {
        this.playerLives = lives;
        return this;
    }
    
    public GameState addCollectedKey(String keyId) {
        this.collectedKeyIds.add(keyId);
        return this;
    }
    
    public GameState setScore(int score) {
        this.score = score;
        return this;
    }
    
    public GameState setElapsedTime(float time) {
        this.elapsedTime = time;
        return this;
    }
    
    public GameState setSaveName(String name) {
        this.saveName = name;
        return this;
    }
    
    public GameState setShield(boolean hasShield, float timeRemaining) {
        this.hasShield = hasShield;
        this.shieldTimeRemaining = timeRemaining;
        return this;
    }
    
    public GameState setSpeedBoost(boolean hasSpeedBoost, float timeRemaining) {
        this.hasSpeedBoost = hasSpeedBoost;
        this.speedBoostTimeRemaining = timeRemaining;
        return this;
    }
    
    public void addEnemyState(String id, float x, float y, String aiState) {
        enemyStates.add(new EnemyState(id, x, y, aiState));
    }
    
    public String getLevelFile() { return levelFile; }
    public long getSeed() { return seed; }
    public float getPlayerX() { return playerX; }
    public float getPlayerY() { return playerY; }
    public Vector2 getPlayerPosition() { return new Vector2(playerX, playerY); }
    public int getPlayerLives() { return playerLives; }
    public Set<String> getCollectedKeyIds() { return collectedKeyIds; }
    public int getScore() { return score; }
    public float getElapsedTime() { return elapsedTime; }
    public long getSaveTimestamp() { return saveTimestamp; }
    public String getSaveName() { return saveName; }
    public boolean hasShield() { return hasShield; }
    public float getShieldTimeRemaining() { return shieldTimeRemaining; }
    public boolean hasSpeedBoost() { return hasSpeedBoost; }
    public float getSpeedBoostTimeRemaining() { return speedBoostTimeRemaining; }
    public List<EnemyState> getEnemyStates() { return enemyStates; }
    
    /**
     * Inner class for enemy state serialization.
     */
    public static class EnemyState implements Serializable {
        private static final long serialVersionUID = 1L;
        
        public final String id;
        public final float x;
        public final float y;
        public final String aiState;
        
        public EnemyState(String id, float x, float y, String aiState) {
            this.id = id;
            this.x = x;
            this.y = y;
            this.aiState = aiState;
        }
    }
}
