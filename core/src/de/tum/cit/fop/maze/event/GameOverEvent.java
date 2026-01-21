package de.tum.cit.fop.maze.event;

/**
 * Event fired when the game is over (player has no lives left).
 */
public class GameOverEvent extends GameEvent {
    
    private final String cause;
    private final float elapsedTime;
    private final int score;
    
    public GameOverEvent(String cause, float elapsedTime, int score) {
        super();
        this.cause = cause;
        this.elapsedTime = elapsedTime;
        this.score = score;
    }
    
    public String getCause() { return cause; }
    public float getElapsedTime() { return elapsedTime; }
    public int getScore() { return score; }
}
