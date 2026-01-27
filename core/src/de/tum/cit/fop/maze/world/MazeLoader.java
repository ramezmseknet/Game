package de.tum.cit.fop.maze.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import java.io.IOException;
import java.util.Properties;

/**
 * Utility class for loading maze data from .properties files.
 * Format: x,y=type where type is an integer TileType code.
 */
public class MazeLoader {
    
    private MazeLoader() {} // Utility class
    
    /**
     * Loads a maze from an internal .properties file.
     * @param filePath Path to the properties file (e.g., "maps/level-1.properties")
     * @return The loaded Maze object
     * @throws MazeLoadException if loading fails
     */
    public static Maze loadFromFile(String filePath) {
        try {
            FileHandle file = Gdx.files.internal(filePath);
            return loadFromFileHandle(file);
        } catch (Exception e) {
            throw new MazeLoadException("Failed to load maze from: " + filePath, e);
        }
    }
    
    /**
     * Loads a maze from an external file (user-selected).
     * @param filePath Absolute path to the properties file
     * @return The loaded Maze object
     * @throws MazeLoadException if loading fails
     */
    public static Maze loadFromExternalFile(String filePath) {
        try {
            FileHandle file = Gdx.files.absolute(filePath);
            return loadFromFileHandle(file);
        } catch (Exception e) {
            throw new MazeLoadException("Failed to load external maze from: " + filePath, e);
        }
    }
    
    /**
     * Loads a maze from a FileHandle.
     * @param file The file handle to load from
     * @return The loaded Maze object
     */
    private static Maze loadFromFileHandle(FileHandle file) throws IOException {
        Properties properties = new Properties();
        properties.load(file.read());
        
        int maxX = 0;
        int maxY = 0;
        
        for (String key : properties.stringPropertyNames()) {
            if (!key.contains(",")) continue;
            
            String[] coords = key.split(",");
            int x = Integer.parseInt(coords[0].trim());
            int y = Integer.parseInt(coords[1].trim());
            
            maxX = Math.max(maxX, x);
            maxY = Math.max(maxY, y);
        }
        
        int width = maxX + 1;
        int height = maxY + 1;
        
        TileType[][] grid = new TileType[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (x == 0 || y == 0 || x == width - 1 || y == height - 1) {
                    grid[y][x] = TileType.WALL;
                } else {
                    grid[y][x] = TileType.FLOOR;
                }
            }
        }
        
        for (String key : properties.stringPropertyNames()) {
            if (!key.contains(",")) continue;
            
            String[] coords = key.split(",");
            int x = Integer.parseInt(coords[0].trim());
            int y = Integer.parseInt(coords[1].trim());
            int code = Integer.parseInt(properties.getProperty(key).trim());
            
            grid[y][x] = TileType.fromCode(code);
        }
        
        validateMaze(grid, width, height);
        
        return new Maze(grid);
    }
    
    /**
     * Validates maze structure according to requirements.
     * @param grid The tile grid
     * @param width Grid width
     * @param height Grid height
     * @throws MazeLoadException if validation fails
     */
    private static void validateMaze(TileType[][] grid, int width, int height) {
        boolean hasEntry = false;
        boolean hasExit = false;
        int entryCount = 0;
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                TileType tile = grid[y][x];
                
                if (tile == TileType.ENTRY) {
                    hasEntry = true;
                    entryCount++;
                }
                
                if (tile == TileType.EXIT) {
                    hasExit = true;
                    if (x != 0 && y != 0 && x != width - 1 && y != height - 1) {
                        Gdx.app.log("MazeLoader", "Warning: Exit at (" + x + "," + y + ") is not on border");
                    }
                }
                
                boolean isBorder = (x == 0 || y == 0 || x == width - 1 || y == height - 1);
                if (isBorder && tile != TileType.WALL && tile != TileType.EXIT && tile != TileType.ENTRY) {
                    grid[y][x] = TileType.WALL;
                    Gdx.app.log("MazeLoader", "Auto-fixed border tile at (" + x + "," + y + ") to WALL");
                }
            }
        }
        
        if (!hasEntry) {
            throw new MazeLoadException("Maze has no entry point (tile type 1)");
        }
        
        if (entryCount > 1) {
            Gdx.app.log("MazeLoader", "Warning: Maze has multiple entry points, using first found");
        }
        
        if (!hasExit) {
            throw new MazeLoadException("Maze has no exit point (tile type 2)");
        }
    }
    
    /**
     * Creates a simple test maze programmatically.
     * @param width Maze width in tiles
     * @param height Maze height in tiles
     * @return A simple maze for testing
     */
    public static Maze createTestMaze(int width, int height) {
        TileType[][] grid = new TileType[height][width];
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (x == 0 || y == 0 || x == width - 1 || y == height - 1) {
                    grid[y][x] = TileType.WALL;
                } else {
                    grid[y][x] = TileType.FLOOR;
                }
            }
        }
        
        grid[1][1] = TileType.ENTRY;
        
        grid[height / 2][width - 1] = TileType.EXIT;
        
        grid[height / 2][width / 2] = TileType.KEY;
        
        return new Maze(grid);
    }
    
    /**
     * Exception thrown when maze loading fails.
     */
    public static class MazeLoadException extends RuntimeException {
        public MazeLoadException(String message) {
            super(message);
        }
        
        public MazeLoadException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
