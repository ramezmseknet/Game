package de.tum.cit.fop.maze.ai;

import com.badlogic.gdx.math.Vector2;
import de.tum.cit.fop.maze.util.Constants;
import de.tum.cit.fop.maze.world.Maze;

import java.util.*;

/**
 * A* pathfinding implementation for grid-based maze navigation.
 */
public class AStarPathfinder implements Pathfinder {
    
    private Maze maze;
    private boolean allowDiagonal;
    
    // Direction offsets for neighbors (4-directional)
    private static final int[][] DIRECTIONS = {
        {0, 1},   // Up
        {0, -1},  // Down
        {-1, 0},  // Left
        {1, 0}    // Right
    };
    
    // Direction offsets for diagonal movement (8-directional)
    private static final int[][] DIAGONAL_DIRECTIONS = {
        {0, 1}, {0, -1}, {-1, 0}, {1, 0},
        {-1, 1}, {1, 1}, {-1, -1}, {1, -1}
    };
    
    public AStarPathfinder() {
        this.allowDiagonal = false;
    }
    
    public AStarPathfinder(boolean allowDiagonal) {
        this.allowDiagonal = allowDiagonal;
    }
    
    @Override
    public void setMaze(Maze maze) {
        this.maze = maze;
    }
    
    @Override
    public boolean isWalkable(int tileX, int tileY) {
        return maze != null && maze.isWalkableForEnemy(tileX, tileY);
    }
    
    @Override
    public List<Vector2> findPath(Vector2 start, Vector2 goal) {
        int startX = (int) (start.x / Constants.TILE_SIZE);
        int startY = (int) (start.y / Constants.TILE_SIZE);
        int goalX = (int) (goal.x / Constants.TILE_SIZE);
        int goalY = (int) (goal.y / Constants.TILE_SIZE);
        
        return findPath(startX, startY, goalX, goalY);
    }
    
    @Override
    public List<Vector2> findPath(int startX, int startY, int goalX, int goalY) {
        if (maze == null) return Collections.emptyList();
        
        if (!maze.isInBounds(startX, startY) || !maze.isInBounds(goalX, goalY)) {
            return Collections.emptyList();
        }
        
        if (!isWalkable(goalX, goalY)) {
            return Collections.emptyList();
        }
        
        // A* algorithm
        PriorityQueue<Node> openSet = new PriorityQueue<>();
        Map<Long, Node> allNodes = new HashMap<>();
        Set<Long> closedSet = new HashSet<>();
        
        Node startNode = new Node(startX, startY, null, 0, getHeuristic(startX, startY, goalX, goalY));
        openSet.add(startNode);
        allNodes.put(nodeKey(startX, startY), startNode);
        
        while (!openSet.isEmpty()) {
            Node current = openSet.poll();
            
            // Goal reached
            if (current.x == goalX && current.y == goalY) {
                return reconstructPath(current);
            }
            
            closedSet.add(nodeKey(current.x, current.y));
            
            // Check neighbors
            int[][] directions = allowDiagonal ? DIAGONAL_DIRECTIONS : DIRECTIONS;
            for (int[] dir : directions) {
                int nx = current.x + dir[0];
                int ny = current.y + dir[1];
                
                long neighborKey = nodeKey(nx, ny);
                
                if (closedSet.contains(neighborKey)) continue;
                if (!isWalkable(nx, ny)) continue;
                
                if (allowDiagonal && dir[0] != 0 && dir[1] != 0) {
                    if (!isWalkable(current.x + dir[0], current.y) || 
                        !isWalkable(current.x, current.y + dir[1])) {
                        continue;
                    }
                }
                
                float moveCost = (dir[0] != 0 && dir[1] != 0) ? 1.414f : 1f;
                float tentativeG = current.g + moveCost;
                
                Node neighbor = allNodes.get(neighborKey);
                if (neighbor == null) {
                    neighbor = new Node(nx, ny, current, tentativeG, getHeuristic(nx, ny, goalX, goalY));
                    allNodes.put(neighborKey, neighbor);
                    openSet.add(neighbor);
                } else if (tentativeG < neighbor.g) {
                    openSet.remove(neighbor);
                    neighbor.g = tentativeG;
                    neighbor.f = neighbor.g + neighbor.h;
                    neighbor.parent = current;
                    openSet.add(neighbor);
                }
            }
        }
        
        return Collections.emptyList();
    }
    
    @Override
    public float getHeuristic(int fromX, int fromY, int toX, int toY) {
        // Manhattan distance for 4-directional, Euclidean for diagonal
        if (allowDiagonal) {
            float dx = Math.abs(toX - fromX);
            float dy = Math.abs(toY - fromY);
            return (float) Math.sqrt(dx * dx + dy * dy);
        } else {
            return Math.abs(toX - fromX) + Math.abs(toY - fromY);
        }
    }
    
    /**
     * Reconstructs the path from goal to start.
     */
    private List<Vector2> reconstructPath(Node goalNode) {
        List<Vector2> path = new ArrayList<>();
        Node current = goalNode;
        
        while (current != null) {
            path.add(new Vector2(current.x, current.y));
            current = current.parent;
        }
        
        Collections.reverse(path);
        return path;
    }
    
    /**
     * Creates a unique key for a node position.
     */
    private long nodeKey(int x, int y) {
        return ((long) x << 32) | (y & 0xFFFFFFFFL);
    }
    
    /**
     * Sets whether diagonal movement is allowed.
     * @param allow true to allow diagonal movement
     */
    public void setAllowDiagonal(boolean allow) {
        this.allowDiagonal = allow;
    }
    
    /**
     * Internal node class for A* algorithm.
     */
    private static class Node implements Comparable<Node> {
        int x, y;
        Node parent;
        float g;
        float h;
        float f;
        
        Node(int x, int y, Node parent, float g, float h) {
            this.x = x;
            this.y = y;
            this.parent = parent;
            this.g = g;
            this.h = h;
            this.f = g + h;
        }
        
        @Override
        public int compareTo(Node other) {
            return Float.compare(this.f, other.f);
        }
    }
}
