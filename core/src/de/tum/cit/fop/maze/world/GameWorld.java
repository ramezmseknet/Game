package de.tum.cit.fop.maze.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import de.tum.cit.fop.maze.entity.Direction;
import de.tum.cit.fop.maze.entity.ExitDoor;
import de.tum.cit.fop.maze.entity.GameObject;
import de.tum.cit.fop.maze.entity.Player;
import de.tum.cit.fop.maze.entity.collectible.Collectible;
import de.tum.cit.fop.maze.entity.enemy.Enemy;
import de.tum.cit.fop.maze.entity.interactable.Lever;
import de.tum.cit.fop.maze.entity.obstacle.Obstacle;
import de.tum.cit.fop.maze.event.EventBus;
import de.tum.cit.fop.maze.event.GameEvent;
import de.tum.cit.fop.maze.event.GameOverEvent;
import de.tum.cit.fop.maze.event.LeverToggledEvent;
import de.tum.cit.fop.maze.event.PlayerDamagedEvent;
import de.tum.cit.fop.maze.event.DoorUnlockedEvent;
import de.tum.cit.fop.maze.input.Action;
import de.tum.cit.fop.maze.input.InputBindings;
import de.tum.cit.fop.maze.util.Constants;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Manages all game entities and world logic.
 * Central hub for game state during gameplay.
 */
public class GameWorld {
    
    private final Maze maze;
    private final Player player;
    private final List<Enemy> enemies;
    private final List<Collectible> collectibles;
    private final List<Obstacle> obstacles;
    private final List<GameObject> allEntities;
    private final List<ExitDoor> exitDoors;
    private final List<Lever> levers;
    
    private final InputBindings inputBindings;
    private final EventBus eventBus;
    
    private TextureRegion wallTexture;
    private TextureRegion floorTexture;
    private TextureRegion entryTexture;
    private TextureRegion exitTexture;
    private TextureRegion trapTexture;
    
    private final LinkedList<Direction> directionKeyStack;
    
    private boolean paused;
    private boolean gameOver;
    private boolean levelComplete;
    private float elapsedTime;
    private boolean doorWasUnlocked; // Track for DoorUnlockedEvent
    
    /**
     * Creates a new game world.
     * @param maze The maze to use
     * @param inputBindings Input binding configuration
     * @param eventBus Event bus for game events
     */
    public GameWorld(Maze maze, InputBindings inputBindings, EventBus eventBus) {
        this.maze = maze;
        this.inputBindings = inputBindings;
        this.eventBus = eventBus;
        
        this.enemies = new ArrayList<>();
        this.collectibles = new ArrayList<>();
        this.obstacles = new ArrayList<>();
        this.allEntities = new ArrayList<>();
        this.exitDoors = new ArrayList<>();
        this.levers = new ArrayList<>();
        
        Vector2 entry = maze.getEntryPoint();
        this.player = new Player((int) entry.x, (int) entry.y);
        this.player.setKeysRequired(maze.getTotalKeys());
        
        this.directionKeyStack = new LinkedList<>();
        this.paused = false;
        this.gameOver = false;
        this.levelComplete = false;
        this.elapsedTime = 0f;
        
        allEntities.add(player);
        
        createExitDoors();
    }
    
    /**
     * Creates ExitDoor entities at all exit positions in the maze.
     */
    private void createExitDoors() {
        for (Vector2 exitPos : maze.getExitPoints()) {
            ExitDoor door = new ExitDoor((int) exitPos.x, (int) exitPos.y);
            exitDoors.add(door);
            allEntities.add(door);
        }
    }
    
    /**
     * Updates all game logic.
     * @param deltaTime Frame delta time
     */
    public void update(float deltaTime) {
        if (paused || gameOver || levelComplete) return;
        
        elapsedTime += deltaTime;
        
        handleInput(deltaTime);
        
        boolean doorUnlocked = isAnyLeverOpen();
        player.updateMovement(deltaTime, maze, doorUnlocked);
        
        player.update(deltaTime);
        
        updateExitDoors();
        
        for (Enemy enemy : enemies) {
            enemy.update(deltaTime);
        }
        
        for (Collectible collectible : collectibles) {
            collectible.update(deltaTime);
        }
        
        for (Obstacle obstacle : obstacles) {
            obstacle.update(deltaTime);
        }
        
        checkCollisions();
        
        checkGameState();
        
        cleanupEntities();
    }
    
    /**
     * Handles player input.
     * Uses a "last key wins" system: the most recently pressed direction key takes priority.
     * When that key is released, movement resumes with the previous held key.
     */
    private void handleInput(float deltaTime) {
        boolean running = inputBindings.isPressed(Action.RUN);
        player.setRunning(running);
        
        updateDirectionKeyStack();
        
        Direction moveDir = getActiveDirection();
        
        player.setCurrentMoveDirection(moveDir);
        if (moveDir != Direction.NONE) {
            player.tryMove(moveDir, maze, isAnyLeverOpen());
        }
        
        if (inputBindings.isJustPressed(Action.INTERACT)) {
            tryInteractWithNearbyLevers();
        }
    }
    
    /**
     * Updates the direction key stack based on current key states.
     * Adds newly pressed keys to the end (highest priority).
     * Removes keys that are no longer pressed.
     */
    private void updateDirectionKeyStack() {
        if (inputBindings.isJustPressed(Action.MOVE_UP)) {
            directionKeyStack.remove(Direction.UP);
            directionKeyStack.addLast(Direction.UP);
        }
        if (inputBindings.isJustPressed(Action.MOVE_DOWN)) {
            directionKeyStack.remove(Direction.DOWN);
            directionKeyStack.addLast(Direction.DOWN);
        }
        if (inputBindings.isJustPressed(Action.MOVE_LEFT)) {
            directionKeyStack.remove(Direction.LEFT);
            directionKeyStack.addLast(Direction.LEFT);
        }
        if (inputBindings.isJustPressed(Action.MOVE_RIGHT)) {
            directionKeyStack.remove(Direction.RIGHT);
            directionKeyStack.addLast(Direction.RIGHT);
        }
        
        if (!inputBindings.isPressed(Action.MOVE_UP)) {
            directionKeyStack.remove(Direction.UP);
        }
        if (!inputBindings.isPressed(Action.MOVE_DOWN)) {
            directionKeyStack.remove(Direction.DOWN);
        }
        if (!inputBindings.isPressed(Action.MOVE_LEFT)) {
            directionKeyStack.remove(Direction.LEFT);
        }
        if (!inputBindings.isPressed(Action.MOVE_RIGHT)) {
            directionKeyStack.remove(Direction.RIGHT);
        }
    }
    
    /**
     * Gets the active movement direction (most recently pressed key that's still held).
     * @return The direction to move, or NONE if no direction keys are held
     */
    private Direction getActiveDirection() {
        if (directionKeyStack.isEmpty()) {
            return Direction.NONE;
        }
        return directionKeyStack.getLast();
    }
    
    /**
     * Tries to toggle any levers that the player is near.
     * Levers toggle between open and closed states when the player presses F.
     * Unlike keys, levers are NOT collected - they remain in place.
     */
    private void tryInteractWithNearbyLevers() {
        for (Lever lever : levers) {
            if (lever.isActive() && lever.isPlayerNearby(player)) {
                lever.toggle();
                eventBus.publish(new LeverToggledEvent(lever, lever.isOpen()));
                break;
            }
        }
    }
    
    /**
     * Checks collisions between player and other entities.
     */
    private void checkCollisions() {
        for (Collectible collectible : collectibles) {
            if (collectible.isActive() && !collectible.isCollected()) {
                boolean onSameTile = player.getTileX() == collectible.getTileX() && 
                    player.getTileY() == collectible.getTileY();
                    
                if (onSameTile) {
                    collectible.onCollect(player);
                }
            }
        }
        
        TileType currentTile = maze.getTileAt(player.getTileX(), player.getTileY());
        if (currentTile == TileType.TRAP) {
            if (!player.isInvincible()) {
                player.takeDamage(1);
                eventBus.publish(new PlayerDamagedEvent(player, 1, "trap"));
            }
        }
        
        for (Obstacle obstacle : obstacles) {
            if (obstacle.isActive() && obstacle.canDamage()) {
                if (player.getTileX() == obstacle.getTileX() && 
                    player.getTileY() == obstacle.getTileY()) {
                    obstacle.onContact(player);
                }
            }
        }
        
        for (Enemy enemy : enemies) {
            if (enemy.isActive()) {
                if (player.getTileX() == enemy.getTileX() && 
                    player.getTileY() == enemy.getTileY()) {
                    if (!player.isInvincible()) {
                        player.takeDamage(enemy.getDamage());
                        eventBus.publish(new PlayerDamagedEvent(player, enemy.getDamage(), enemy.getClass().getSimpleName()));
                    }
                }
            }
        }
    }
    
    /**
     * Updates exit door states based on lever state.
     * The door is unlocked when any lever is in the open position.
     */
    private void updateExitDoors() {
        boolean doorUnlocked = isAnyLeverOpen();
        for (ExitDoor door : exitDoors) {
            door.setUnlocked(doorUnlocked);
        }
        if (doorUnlocked && !doorWasUnlocked && !exitDoors.isEmpty()) {
            eventBus.publish(new DoorUnlockedEvent(exitDoors.get(0)));
        }
        doorWasUnlocked = doorUnlocked;
    }
    
    /**
     * Checks if any lever in the world is in the open state.
     * @return true if at least one lever is open
     */
    public boolean isAnyLeverOpen() {
        for (Lever lever : levers) {
            if (lever.isActive() && lever.isOpen()) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Checks win/lose conditions.
     */
    private void checkGameState() {
        if (player.isDead()) {
            gameOver = true;
            eventBus.publish(new GameOverEvent("death", elapsedTime, 0));
            return;
        }
        
        TileType currentTile = maze.getTileAt(player.getTileX(), player.getTileY());
        if (currentTile == TileType.EXIT) {
            if (isAnyLeverOpen()) {
                levelComplete = true;
            }
        }
    }
    
    /**
     * Removes inactive entities from lists.
     */
    private void cleanupEntities() {
        collectibles.removeIf(c -> !c.isActive());
        obstacles.removeIf(o -> !o.isActive());
        enemies.removeIf(e -> !e.isActive());
        allEntities.removeIf(e -> !e.isActive());
    }
    
    /**
     * Renders the game world.
     * @param batch SpriteBatch to draw with
     */
    public void render(SpriteBatch batch) {
        renderMaze(batch);
        
        for (ExitDoor door : exitDoors) {
            door.render(batch);
        }
        
        for (Lever lever : levers) {
            lever.render(batch);
        }
        
        for (Collectible collectible : collectibles) {
            collectible.render(batch);
        }
        
        for (Obstacle obstacle : obstacles) {
            obstacle.render(batch);
        }
        
        for (Enemy enemy : enemies) {
            enemy.render(batch);
        }
        
        player.render(batch);
    }
    
    /**
     * Renders the maze tiles.
     * Note: EXIT tiles are skipped as the door entity renders there instead.
     */
    private void renderMaze(SpriteBatch batch) {
        for (int y = 0; y < maze.getHeight(); y++) {
            for (int x = 0; x < maze.getWidth(); x++) {
                TileType tile = maze.getTileAt(x, y);
                
                if (tile == TileType.EXIT) {
                    continue;
                }
                
                TextureRegion texture = getTextureForTile(tile);
                
                if (texture != null) {
                    batch.draw(texture,
                        x * Constants.TILE_SIZE,
                        y * Constants.TILE_SIZE,
                        Constants.TILE_SIZE,
                        Constants.TILE_SIZE
                    );
                }
            }
        }
    }
    
    /**
     * Gets the texture for a tile type.
     */
    private TextureRegion getTextureForTile(TileType tile) {
        switch (tile) {
            case WALL:
                return wallTexture;
            case ENTRY:
                return entryTexture != null ? entryTexture : floorTexture;
            case EXIT:
                return exitTexture != null ? exitTexture : wallTexture;
            case TRAP:
                return trapTexture != null ? trapTexture : floorTexture;
            default:
                return floorTexture;
        }
    }
    
    /**
     * Adds an enemy to the world.
     * @param enemy The enemy to add
     */
    public void addEnemy(Enemy enemy) {
        enemies.add(enemy);
        allEntities.add(enemy);
    }
    
    /**
     * Adds a collectible to the world.
     * @param collectible The collectible to add
     */
    public void addCollectible(Collectible collectible) {
        collectibles.add(collectible);
        allEntities.add(collectible);
    }
    
    /**
     * Adds an obstacle to the world.
     * @param obstacle The obstacle to add
     */
    public void addObstacle(Obstacle obstacle) {
        obstacles.add(obstacle);
        allEntities.add(obstacle);
    }
    
    /**
     * Adds a lever to the world.
     * @param lever The lever to add
     */
    public void addLever(Lever lever) {
        levers.add(lever);
        allEntities.add(lever);
    }
    
    public void setWallTexture(TextureRegion texture) { this.wallTexture = texture; }
    public void setFloorTexture(TextureRegion texture) { this.floorTexture = texture; }
    public void setEntryTexture(TextureRegion texture) { this.entryTexture = texture; }
    public void setExitTexture(TextureRegion texture) { this.exitTexture = texture; }
    public void setTrapTexture(TextureRegion texture) { this.trapTexture = texture; }
    
    public Maze getMaze() { return maze; }
    public Player getPlayer() { return player; }
    public List<Enemy> getEnemies() { return enemies; }
    public List<Collectible> getCollectibles() { return collectibles; }
    public List<Obstacle> getObstacles() { return obstacles; }
    public List<Lever> getLevers() { return levers; }
    public boolean isPaused() { return paused; }
    public boolean isGameOver() { return gameOver; }
    public boolean isLevelComplete() { return levelComplete; }
    public float getElapsedTime() { return elapsedTime; }
    
    public void setPaused(boolean paused) { this.paused = paused; }
    public void setGameOver(boolean gameOver) { this.gameOver = gameOver; }
    public void setLevelComplete(boolean complete) { this.levelComplete = complete; }
    
    /**
     * Disposes of world resources.
     */
    public void dispose() {}
}
