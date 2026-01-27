package de.tum.cit.fop.maze.world;

import de.tum.cit.fop.maze.entity.Player;
import de.tum.cit.fop.maze.event.EventBus;
import de.tum.cit.fop.maze.event.GameEvent;

/**
 * Manages game state and transitions.
 * Provides centralized control over game flow.
 */
public class GameStateManager {
    
    /**
     * Possible game states.
     */
    public enum GameState {
        LOADING,
        PLAYING,
        PAUSED,
        GAME_OVER,
        LEVEL_COMPLETE,
        TRANSITIONING,
        CUTSCENE
    }
    
    private GameState currentState;
    private GameState previousState;
    
    private int currentLevel;
    private int score;
    private float levelTime;
    private float totalGameTime;
    
    private LevelResult lastLevelResult;
    
    private EventBus eventBus;
    
    /**
     * Contains results from completing a level.
     */
    public static class LevelResult {
        public final int level;
        public final int score;
        public final float time;
        public final int keysCollected;
        public final int coinsCollected;
        public final int gemsCollected;
        public final int livesRemaining;
        public final boolean allCoinsCollected;
        public final boolean noHitRun;
        public final int starRating; // 1-3 stars
        
        public LevelResult(int level, int score, float time, int keysCollected,
                          int coinsCollected, int gemsCollected, int livesRemaining,
                          boolean allCoinsCollected, boolean noHitRun, int starRating) {
            this.level = level;
            this.score = score;
            this.time = time;
            this.keysCollected = keysCollected;
            this.coinsCollected = coinsCollected;
            this.gemsCollected = gemsCollected;
            this.livesRemaining = livesRemaining;
            this.allCoinsCollected = allCoinsCollected;
            this.noHitRun = noHitRun;
            this.starRating = starRating;
        }
        
        /**
         * Creates a level result from player and game data.
         */
        public static LevelResult fromGame(int level, Player player, float levelTime,
                                          int totalCoins, int maxLives) {
            int score = player.getScore();
            int keys = player.getKeysCollected();
            int coins = player.getTotalCoinsCollected();
            int gems = player.getTotalGemsCollected();
            int lives = player.getLives();
            
            boolean allCoins = totalCoins > 0 && coins >= totalCoins;
            boolean noHit = lives >= maxLives;
            
            int stars = 1; // Base star for completion
            if (allCoins) stars++;
            if (noHit) stars++;
            
            return new LevelResult(level, score, levelTime, keys, coins, gems,
                                  lives, allCoins, noHit, stars);
        }
        
        public String getTimeFormatted() {
            int minutes = (int) (time / 60);
            int seconds = (int) (time % 60);
            return String.format("%d:%02d", minutes, seconds);
        }
    }
    
    public GameStateManager() {
        this.currentState = GameState.LOADING;
        this.previousState = GameState.LOADING;
        this.currentLevel = 1;
        this.score = 0;
        this.levelTime = 0;
        this.totalGameTime = 0;
    }
    
    /**
     * Sets the event bus for state change notifications.
     */
    public void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
    }
    
    /**
     * Changes the current game state.
     */
    public void setState(GameState newState) {
        if (newState != currentState) {
            previousState = currentState;
            currentState = newState;
            
            if (eventBus != null) {
                eventBus.publish(new GameStateChangedEvent(previousState, currentState));
            }
        }
    }
    
    /**
     * Updates timers.
     */
    public void update(float deltaTime) {
        if (currentState == GameState.PLAYING) {
            levelTime += deltaTime;
            totalGameTime += deltaTime;
        }
    }
    
    /**
     * Starts a new game.
     */
    public void startNewGame(int startLevel) {
        this.currentLevel = startLevel;
        this.score = 0;
        this.levelTime = 0;
        this.totalGameTime = 0;
        this.lastLevelResult = null;
        setState(GameState.PLAYING);
    }
    
    /**
     * Starts the next level.
     */
    public void startNextLevel() {
        currentLevel++;
        levelTime = 0;
        setState(GameState.PLAYING);
    }
    
    /**
     * Completes the current level.
     */
    public void completeLevel(LevelResult result) {
        this.lastLevelResult = result;
        this.score += result.score;
        setState(GameState.LEVEL_COMPLETE);
    }
    
    /**
     * Triggers game over.
     */
    public void gameOver() {
        setState(GameState.GAME_OVER);
    }
    
    /**
     * Pauses the game.
     */
    public void pause() {
        if (currentState == GameState.PLAYING) {
            setState(GameState.PAUSED);
        }
    }
    
    /**
     * Resumes the game.
     */
    public void resume() {
        if (currentState == GameState.PAUSED) {
            setState(GameState.PLAYING);
        }
    }
    
    /**
     * Returns to previous state (useful for unpausing).
     */
    public void returnToPreviousState() {
        setState(previousState);
    }
    
    public GameState getCurrentState() { return currentState; }
    public GameState getPreviousState() { return previousState; }
    public int getCurrentLevel() { return currentLevel; }
    public int getScore() { return score; }
    public float getLevelTime() { return levelTime; }
    public float getTotalGameTime() { return totalGameTime; }
    public LevelResult getLastLevelResult() { return lastLevelResult; }
    
    public boolean isPlaying() { return currentState == GameState.PLAYING; }
    public boolean isPaused() { return currentState == GameState.PAUSED; }
    public boolean isGameOver() { return currentState == GameState.GAME_OVER; }
    public boolean isLevelComplete() { return currentState == GameState.LEVEL_COMPLETE; }
    
    /**
     * Event for game state changes.
     */
    public static class GameStateChangedEvent extends GameEvent {
        private final GameState previousState;
        private final GameState newState;
        
        public GameStateChangedEvent(GameState previous, GameState newState) {
            super();
            this.previousState = previous;
            this.newState = newState;
        }
        
        public String getEventType() {
            return "GAME_STATE_CHANGED";
        }
        
        public GameState getPreviousState() { return previousState; }
        public GameState getNewState() { return newState; }
    }
}
