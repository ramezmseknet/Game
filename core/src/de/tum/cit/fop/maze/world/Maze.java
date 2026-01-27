package de.tum.cit.fop.maze.world;

import com.badlogic.gdx.math.Vector2;
import de.tum.cit.fop.maze.util.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the maze grid structure.
 * Provides tile queries and pathfinding support.
 */
public class Maze {
    
    private final TileType[][] grid;
    private final int width;
    private final int height;
    private final Vector2 entryPoint;
    private final List<Vector2> exitPoints;
    private final List<Vector2> enemySpawns;
    private final List<Vector2> keyPositions;
    private final List<Vector2> heartPositions;
    private final List<Vector2> trapPositions;
    private final List<Vector2> powerUpPositions;
    private final List<Vector2> spikeTrapPositions;
    private final List<Vector2> movingObstaclePositions;
    
    private int totalKeys;
    
    /**
     * Creates a maze from a 2D tile type array.
     * @param grid The tile grid (y, x indexed)
     */
    public Maze(TileType[][] grid) {
        this.grid = grid;
        this.height = grid.length;
        this.width = grid.length > 0 ? grid[0].length : 0;
        
        this.exitPoints = new ArrayList<>();
        this.enemySpawns = new ArrayList<>();
        this.keyPositions = new ArrayList<>();
        this.heartPositions = new ArrayList<>();
        this.trapPositions = new ArrayList<>();
        this.powerUpPositions = new ArrayList<>();
        this.spikeTrapPositions = new ArrayList<>();
        this.movingObstaclePositions = new ArrayList<>();
        
        Vector2 tempEntry = null;
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                TileType tile = grid[y][x];
                Vector2 pos = new Vector2(x, y);
                
                switch (tile) {
                    case ENTRY:
                        tempEntry = pos;
                        break;
                    case EXIT:
                        exitPoints.add(pos);
                        break;
                    case ENEMY:
                        enemySpawns.add(pos);
                        break;
                    case KEY:
                        keyPositions.add(pos);
                        totalKeys++;
                        break;
                    case HEART:
                        heartPositions.add(pos);
                        break;
                    case TRAP:
                        trapPositions.add(pos);
                        break;
                    case SPIKE_TRAP:
                        spikeTrapPositions.add(pos);
                        break;
                    case MOVING_OBSTACLE:
                        movingObstaclePositions.add(pos);
                        break;
                    case SPEED_POWERUP:
                    case SHIELD_POWERUP:
                        powerUpPositions.add(pos);
                        break;
                    default:
                        break;
                }
            }
        }
        
        this.entryPoint = tempEntry != null ? tempEntry : new Vector2(1, 1);
    }
    
    /**
     * Gets the tile type at the specified grid coordinates.
     * @param x Grid X coordinate
     * @param y Grid Y coordinate
     * @return The tile type, or WALL if out of bounds
     */
    public TileType getTileAt(int x, int y) {
        if (isInBounds(x, y)) {
            return grid[y][x];
        }
        return TileType.WALL;
    }
    
    /**
     * Sets the tile type at the specified grid coordinates.
     * @param x Grid X coordinate
     * @param y Grid Y coordinate
     * @param type The new tile type
     */
    public void setTileAt(int x, int y, TileType type) {
        if (isInBounds(x, y)) {
            grid[y][x] = type;
        }
    }
    
    /**
     * Checks if a position is within the maze bounds.
     * @param x Grid X coordinate
     * @param y Grid Y coordinate
     * @return true if in bounds
     */
    public boolean isInBounds(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }
    
    /**
     * Checks if a tile is walkable.
     * @param x Grid X coordinate
     * @param y Grid Y coordinate
     * @return true if entities can walk on this tile
     */
    public boolean isWalkable(int x, int y) {
        TileType tile = getTileAt(x, y);
        if (tile == TileType.EXIT) {
            return false;
        }
        return tile.isWalkable();
    }
    
    /**
     * Checks if a tile is walkable, considering door state.
     * @param x Grid X coordinate
     * @param y Grid Y coordinate
     * @param doorUnlocked Whether the exit door is unlocked
     * @return true if entities can walk on this tile
     */
    public boolean isWalkable(int x, int y, boolean doorUnlocked) {
        TileType tile = getTileAt(x, y);
        if (tile == TileType.EXIT) {
            return doorUnlocked;
        }
        return tile.isWalkable();
    }
    
    /**
     * Checks if a tile is walkable for enemies (avoids traps).
     * @param x Grid X coordinate
     * @param y Grid Y coordinate
     * @return true if enemies can walk on this tile
     */
    public boolean isWalkableForEnemy(int x, int y) {
        TileType tile = getTileAt(x, y);
        return tile.isWalkable() && !tile.isDangerous();
    }
    
    /**
     * Converts grid coordinates to world position (center of tile).
     * @param tileX Grid X coordinate
     * @param tileY Grid Y coordinate
     * @return World position at tile center
     */
    public Vector2 tileToWorld(int tileX, int tileY) {
        return new Vector2(
            tileX * Constants.TILE_SIZE + Constants.TILE_SIZE / 2f,
            tileY * Constants.TILE_SIZE + Constants.TILE_SIZE / 2f
        );
    }
    
    /**
     * Converts world position to grid coordinates.
     * @param worldX World X coordinate
     * @param worldY World Y coordinate
     * @return Grid coordinates
     */
    public Vector2 worldToTile(float worldX, float worldY) {
        return new Vector2(
            (int) (worldX / Constants.TILE_SIZE),
            (int) (worldY / Constants.TILE_SIZE)
        );
    }
    
    /**
     * Finds the closest exit to a given position.
     * @param fromX Grid X coordinate
     * @param fromY Grid Y coordinate
     * @return The closest exit position, or null if no exits
     */
    public Vector2 findClosestExit(int fromX, int fromY) {
        if (exitPoints.isEmpty()) return null;
        
        Vector2 closest = null;
        float minDist = Float.MAX_VALUE;
        
        for (Vector2 exit : exitPoints) {
            float dist = Math.abs(exit.x - fromX) + Math.abs(exit.y - fromY);
            if (dist < minDist) {
                minDist = dist;
                closest = exit;
            }
        }
        
        return closest;
    }
    
    /**
     * Gets the angle to the closest exit from a position.
     * @param fromX World X coordinate
     * @param fromY World Y coordinate
     * @return Angle in degrees (0 = right, 90 = up)
     */
    public float getAngleToClosestExit(float fromX, float fromY) {
        Vector2 tile = worldToTile(fromX, fromY);
        Vector2 closestExit = findClosestExit((int) tile.x, (int) tile.y);
        
        if (closestExit == null) return 0;
        
        Vector2 exitWorld = tileToWorld((int) closestExit.x, (int) closestExit.y);
        float dx = exitWorld.x - fromX;
        float dy = exitWorld.y - fromY;
        
        return (float) Math.toDegrees(Math.atan2(dy, dx));
    }
    
    // Getters
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public Vector2 getEntryPoint() { return entryPoint.cpy(); }
    public List<Vector2> getExitPoints() { return new ArrayList<>(exitPoints); }
    public List<Vector2> getEnemySpawns() { return new ArrayList<>(enemySpawns); }
    public List<Vector2> getKeyPositions() { return new ArrayList<>(keyPositions); }
    public List<Vector2> getHeartPositions() { return new ArrayList<>(heartPositions); }
    public List<Vector2> getTrapPositions() { return new ArrayList<>(trapPositions); }
    public List<Vector2> getSpikeTrapPositions() { return new ArrayList<>(spikeTrapPositions); }
    public List<Vector2> getMovingObstaclePositions() { return new ArrayList<>(movingObstaclePositions); }
    public List<Vector2> getPowerUpPositions() { return new ArrayList<>(powerUpPositions); }
    public int getTotalKeys() { return totalKeys; }
    
    /**
     * Gets the pixel width of the entire maze.
     * @return Width in pixels
     */
    public float getPixelWidth() {
        return width * Constants.TILE_SIZE;
    }
    
    /**
     * Gets the pixel height of the entire maze.
     * @return Height in pixels
     */
    public float getPixelHeight() {
        return height * Constants.TILE_SIZE;
    }
}
