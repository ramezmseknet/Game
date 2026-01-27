package de.tum.cit.fop.maze.world;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

/**
 * Procedural maze generator for endless/survival mode.
 * Uses randomized depth-first search algorithm with seed support.
 * Difficulty parameters affect maze complexity and entity placement.
 */
public class ProceduralMazeGenerator {
    
    private final long seed;
    private final Random random;
    
    private int width;
    private int height;
    private float wallDensity;      // 0-1, affects additional walls
    private float trapDensity;      // 0-1, affects trap placement
    private int enemyCount;
    private int keyCount;
    private int powerUpCount;
    private int heartCount;
    
    /**
     * Creates a generator with a specific seed.
     * @param seed The random seed for reproducible mazes
     */
    public ProceduralMazeGenerator(long seed) {
        this.seed = seed;
        this.random = new Random(seed);
        
        this.width = 15;
        this.height = 15;
        this.wallDensity = 0.3f;
        this.trapDensity = 0.1f;
        this.enemyCount = 3;
        this.keyCount = 1;
        this.powerUpCount = 2;
        this.heartCount = 1;
    }
    
    /**
     * Creates a generator with a random seed.
     */
    public ProceduralMazeGenerator() {
        this(System.currentTimeMillis());
    }
    
    /**
     * Configures the generator based on difficulty.
     * @param difficultyManager The difficulty manager
     * @return this for chaining
     */
    public ProceduralMazeGenerator withDifficulty(DifficultyManager difficultyManager) {
        this.width = difficultyManager.getRecommendedMazeSize();
        this.height = this.width;
        this.trapDensity = 0.05f + (difficultyManager.getTrapDensityMultiplier() - 1f) * 0.1f;
        this.enemyCount = difficultyManager.getRecommendedEnemyCount(width);
        this.keyCount = Math.max(1, difficultyManager.getWavesCompleted() / 5 + 1);
        this.powerUpCount = (int) (3 * difficultyManager.getPowerUpFrequencyMultiplier());
        return this;
    }
    
    /**
     * Sets maze dimensions.
     * @param width Maze width in tiles
     * @param height Maze height in tiles
     * @return this for chaining
     */
    public ProceduralMazeGenerator withSize(int width, int height) {
        this.width = width;
        this.height = height;
        return this;
    }
    
    /**
     * Sets enemy count.
     * @param count Number of enemies
     * @return this for chaining
     */
    public ProceduralMazeGenerator withEnemies(int count) {
        this.enemyCount = count;
        return this;
    }
    
    /**
     * Sets trap density.
     * @param density Trap density (0-1)
     * @return this for chaining
     */
    public ProceduralMazeGenerator withTrapDensity(float density) {
        this.trapDensity = MathUtils.clamp(density, 0f, 1f);
        return this;
    }
    
    /**
     * Generates a maze.
     * @return The generated Maze object
     */
    public Maze generate() {
        TileType[][] grid = new TileType[height][width];
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                grid[y][x] = TileType.WALL;
            }
        }
        
        generatePaths(grid);
        
        Vector2 entry = placeEntry(grid);
        
        List<Vector2> exits = placeExits(grid, entry);
        
        placeKeys(grid, entry, exits);
        
        placeTraps(grid, entry);
        
        placeEnemies(grid, entry);
        
        placePowerUps(grid, entry);
        
        placeHearts(grid, entry);
        
        return new Maze(grid);
    }
    
    /**
     * Generates maze paths using randomized DFS.
     */
    private void generatePaths(TileType[][] grid) {
        int startX = 1 + random.nextInt((width - 2) / 2) * 2;
        int startY = 1 + random.nextInt((height - 2) / 2) * 2;
        
        if (startX % 2 == 0) startX++;
        if (startY % 2 == 0) startY++;
        
        Stack<int[]> stack = new Stack<>();
        stack.push(new int[]{startX, startY});
        grid[startY][startX] = TileType.FLOOR;
        
        int[][] directions = {{0, 2}, {2, 0}, {0, -2}, {-2, 0}};
        
        while (!stack.isEmpty()) {
            int[] current = stack.peek();
            int cx = current[0];
            int cy = current[1];
            
            List<int[]> neighbors = new ArrayList<>();
            for (int[] dir : directions) {
                int nx = cx + dir[0];
                int ny = cy + dir[1];
                
                if (nx > 0 && nx < width - 1 && ny > 0 && ny < height - 1) {
                    if (grid[ny][nx] == TileType.WALL) {
                        neighbors.add(new int[]{nx, ny, dir[0] / 2, dir[1] / 2});
                    }
                }
            }
            
            if (!neighbors.isEmpty()) {
                int[] chosen = neighbors.get(random.nextInt(neighbors.size()));
                int nx = chosen[0];
                int ny = chosen[1];
                int dx = chosen[2];
                int dy = chosen[3];
                
                grid[cy + dy][cx + dx] = TileType.FLOOR;
                grid[ny][nx] = TileType.FLOOR;
                
                stack.push(new int[]{nx, ny});
            } else {
                stack.pop();
            }
        }
        
        int extraPassages = (int) (width * height * 0.02f);
        for (int i = 0; i < extraPassages; i++) {
            int x = 1 + random.nextInt(width - 2);
            int y = 1 + random.nextInt(height - 2);
            
            if (grid[y][x] == TileType.WALL) {
                int floorNeighbors = 0;
                if (y > 0 && grid[y-1][x] == TileType.FLOOR) floorNeighbors++;
                if (y < height-1 && grid[y+1][x] == TileType.FLOOR) floorNeighbors++;
                if (x > 0 && grid[y][x-1] == TileType.FLOOR) floorNeighbors++;
                if (x < width-1 && grid[y][x+1] == TileType.FLOOR) floorNeighbors++;
                
                if (floorNeighbors >= 2) {
                    grid[y][x] = TileType.FLOOR;
                }
            }
        }
    }
    
    /**
     * Places the entry point.
     */
    private Vector2 placeEntry(TileType[][] grid) {
        for (int y = 1; y < height / 3; y++) {
            for (int x = 1; x < width / 3; x++) {
                if (grid[y][x] == TileType.FLOOR) {
                    grid[y][x] = TileType.ENTRY;
                    return new Vector2(x, y);
                }
            }
        }
        // Fallback
        grid[1][1] = TileType.ENTRY;
        return new Vector2(1, 1);
    }
    
    /**
     * Places exit point(s).
     */
    private List<Vector2> placeExits(TileType[][] grid, Vector2 entry) {
        List<Vector2> exits = new ArrayList<>();
        
        float maxDist = 0;
        int exitX = width - 2;
        int exitY = height - 2;
        
        for (int y = height * 2 / 3; y < height - 1; y++) {
            for (int x = width * 2 / 3; x < width - 1; x++) {
                if (grid[y][x] == TileType.FLOOR) {
                    float dist = Vector2.dst(entry.x, entry.y, x, y);
                    if (dist > maxDist) {
                        maxDist = dist;
                        exitX = x;
                        exitY = y;
                    }
                }
            }
        }
        
        grid[exitY][exitX] = TileType.EXIT;
        exits.add(new Vector2(exitX, exitY));
        
        return exits;
    }
    
    /**
     * Places key/lever positions.
     */
    private void placeKeys(TileType[][] grid, Vector2 entry, List<Vector2> exits) {
        List<Vector2> candidates = getFloorPositions(grid, entry, exits);
        
        for (int i = 0; i < keyCount && !candidates.isEmpty(); i++) {
            candidates.sort((a, b) -> Float.compare(
                Vector2.dst(entry.x, entry.y, b.x, b.y),
                Vector2.dst(entry.x, entry.y, a.x, a.y)
            ));
            
            int index = random.nextInt(Math.max(1, candidates.size() / 3));
            Vector2 pos = candidates.remove(index);
            grid[(int)pos.y][(int)pos.x] = TileType.KEY;
        }
    }
    
    /**
     * Places traps.
     */
    private void placeTraps(TileType[][] grid, Vector2 entry) {
        List<Vector2> candidates = getFloorPositions(grid, entry, new ArrayList<>());
        int trapCount = (int) (candidates.size() * trapDensity);
        
        for (int i = 0; i < trapCount && !candidates.isEmpty(); i++) {
            int index = random.nextInt(candidates.size());
            Vector2 pos = candidates.remove(index);
            
            if (Vector2.dst(entry.x, entry.y, pos.x, pos.y) > 3) {
                grid[(int)pos.y][(int)pos.x] = TileType.TRAP;
            }
        }
    }
    
    /**
     * Places enemy spawn points.
     */
    private void placeEnemies(TileType[][] grid, Vector2 entry) {
        List<Vector2> candidates = getFloorPositions(grid, entry, new ArrayList<>());
        
        for (int i = 0; i < enemyCount && !candidates.isEmpty(); i++) {
            candidates.sort((a, b) -> Float.compare(
                Vector2.dst(entry.x, entry.y, b.x, b.y),
                Vector2.dst(entry.x, entry.y, a.x, a.y)
            ));
            
            int index = random.nextInt(Math.max(1, candidates.size() / 2));
            Vector2 pos = candidates.remove(index);
            
            if (Vector2.dst(entry.x, entry.y, pos.x, pos.y) > 5) {
                grid[(int)pos.y][(int)pos.x] = TileType.ENEMY;
            }
        }
    }
    
    /**
     * Places power-ups.
     */
    private void placePowerUps(TileType[][] grid, Vector2 entry) {
        List<Vector2> candidates = getFloorPositions(grid, entry, new ArrayList<>());
        
        for (int i = 0; i < powerUpCount && !candidates.isEmpty(); i++) {
            int index = random.nextInt(candidates.size());
            Vector2 pos = candidates.remove(index);
            
            TileType type = (i % 2 == 0) ? TileType.SPEED_POWERUP : TileType.SHIELD_POWERUP;
            grid[(int)pos.y][(int)pos.x] = type;
        }
    }
    
    /**
     * Places heart pickups.
     */
    private void placeHearts(TileType[][] grid, Vector2 entry) {
        List<Vector2> candidates = getFloorPositions(grid, entry, new ArrayList<>());
        
        for (int i = 0; i < heartCount && !candidates.isEmpty(); i++) {
            int index = random.nextInt(candidates.size());
            Vector2 pos = candidates.remove(index);
            grid[(int)pos.y][(int)pos.x] = TileType.HEART;
        }
    }
    
    /**
     * Gets all floor positions excluding entry and exits.
     */
    private List<Vector2> getFloorPositions(TileType[][] grid, Vector2 entry, List<Vector2> exits) {
        List<Vector2> positions = new ArrayList<>();
        
        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                if (grid[y][x] == TileType.FLOOR) {
                    Vector2 pos = new Vector2(x, y);
                    if (!pos.equals(entry) && !exits.contains(pos)) {
                        positions.add(pos);
                    }
                }
            }
        }
        
        return positions;
    }
    
    /**
     * Gets the seed used for generation.
     * @return The seed
     */
    public long getSeed() {
        return seed;
    }
    
    /**
     * Gets the configured width.
     * @return Maze width
     */
    public int getWidth() {
        return width;
    }
    
    /**
     * Gets the configured height.
     * @return Maze height
     */
    public int getHeight() {
        return height;
    }
}
