package de.tum.cit.fop.maze.ai;

import com.badlogic.gdx.math.Vector2;
import de.tum.cit.fop.maze.world.Maze;

import java.util.List;

/**
 * Interface for pathfinding algorithms.
 * Used by enemies to navigate the maze.
 */
public interface Pathfinder {
    
    /**
     * Finds a path from start to goal positions.
     * @param start Starting position in world coordinates
     * @param goal Target position in world coordinates
     * @return List of waypoints to follow, or empty list if no path exists
     */
    List<Vector2> findPath(Vector2 start, Vector2 goal);
    
    /**
     * Finds a path from start to goal tile coordinates.
     * @param startX Starting tile X
     * @param startY Starting tile Y
     * @param goalX Target tile X
     * @param goalY Target tile Y
     * @return List of waypoints to follow, or empty list if no path exists
     */
    List<Vector2> findPath(int startX, int startY, int goalX, int goalY);
    
    /**
     * Checks if a tile is walkable.
     * @param tileX Tile X coordinate
     * @param tileY Tile Y coordinate
     * @return true if entities can walk on this tile
     */
    boolean isWalkable(int tileX, int tileY);
    
    /**
     * Sets the maze reference for pathfinding queries.
     * @param maze The maze to navigate
     */
    void setMaze(Maze maze);
    
    /**
     * Gets the estimated cost from one position to another (heuristic).
     * @param fromX Starting tile X
     * @param fromY Starting tile Y
     * @param toX Target tile X
     * @param toY Target tile Y
     * @return Estimated distance/cost
     */
    float getHeuristic(int fromX, int fromY, int toX, int toY);
}
