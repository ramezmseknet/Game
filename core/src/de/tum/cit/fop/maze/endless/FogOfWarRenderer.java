package de.tum.cit.fop.maze.endless;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import de.tum.cit.fop.maze.entity.Player;
import de.tum.cit.fop.maze.util.Constants;
import de.tum.cit.fop.maze.world.Maze;

/**
 * Renders fog of war effect on the main game view.
 * Covers unexplored areas and provides limited visibility around the player.
 */
public class FogOfWarRenderer {

    private ShapeRenderer shapeRenderer;
    private boolean[][] explored;
    private boolean[][] visible;
    private int mazeWidth;
    private int mazeHeight;

    // Configuration
    private int visionRadius;
    private int exploredRadius;
    private boolean enabled;
    private float fogAlpha;
    private float unexploredAlpha;

    // Colors
    private final Color fogColor;
    private final Color unexploredColor;

    /**
     * Creates a fog of war renderer with default settings.
     */
    public FogOfWarRenderer() {
        this.shapeRenderer = new ShapeRenderer();
        this.visionRadius = 4;       // Tiles currently visible
        this.exploredRadius = 5;     // Tiles marked as explored
        this.enabled = true;
        this.fogAlpha = 0.7f;        // Semi-transparent for explored but not visible
        this.unexploredAlpha = 0.95f; // Nearly opaque for unexplored

        this.fogColor = new Color(0.1f, 0.1f, 0.15f, fogAlpha);
        this.unexploredColor = new Color(0.02f, 0.02f, 0.05f, unexploredAlpha);
    }

    /**
     * Initializes fog of war for a maze.
     * @param maze The maze to cover
     */
    public void init(Maze maze) {
        this.mazeWidth = maze.getWidth();
        this.mazeHeight = maze.getHeight();
        this.explored = new boolean[mazeWidth][mazeHeight];
        this.visible = new boolean[mazeWidth][mazeHeight];
    }

    /**
     * Updates visibility based on player position.
     * @param player The player
     */
    public void update(Player player) {
        if (!enabled || explored == null) return;

        int px = player.getTileX();
        int py = player.getTileY();

        // Reset visibility (only current vision is visible)
        for (int x = 0; x < mazeWidth; x++) {
            for (int y = 0; y < mazeHeight; y++) {
                visible[x][y] = false;
            }
        }

        // Update explored and visible based on player position
        for (int dx = -exploredRadius; dx <= exploredRadius; dx++) {
            for (int dy = -exploredRadius; dy <= exploredRadius; dy++) {
                int x = px + dx;
                int y = py + dy;

                if (x >= 0 && x < mazeWidth && y >= 0 && y < mazeHeight) {
                    // Calculate distance for circular fog
                    float dist = (float) Math.sqrt(dx * dx + dy * dy);

                    if (dist <= visionRadius) {
                        visible[x][y] = true;
                        explored[x][y] = true;
                    } else if (dist <= exploredRadius) {
                        explored[x][y] = true;
                    }
                }
            }
        }
    }

    /**
     * Renders the fog of war overlay.
     * @param camera The game camera
     */
    public void render(OrthographicCamera camera) {
        if (!enabled || explored == null) return;

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        float tileSize = Constants.TILE_SIZE;

        for (int x = 0; x < mazeWidth; x++) {
            for (int y = 0; y < mazeHeight; y++) {
                if (!visible[x][y]) {
                    if (explored[x][y]) {
                        // Explored but not currently visible - dim fog
                        shapeRenderer.setColor(fogColor);
                    } else {
                        // Never explored - dark fog
                        shapeRenderer.setColor(unexploredColor);
                    }
                    shapeRenderer.rect(x * tileSize, y * tileSize, tileSize, tileSize);
                }
            }
        }

        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    /**
     * Checks if a tile is currently visible.
     * @param tileX Tile X coordinate
     * @param tileY Tile Y coordinate
     * @return true if visible
     */
    public boolean isVisible(int tileX, int tileY) {
        if (!enabled || visible == null) return true;
        if (tileX < 0 || tileX >= mazeWidth || tileY < 0 || tileY >= mazeHeight) return false;
        return visible[tileX][tileY];
    }

    /**
     * Checks if a tile has been explored.
     * @param tileX Tile X coordinate
     * @param tileY Tile Y coordinate
     * @return true if explored
     */
    public boolean isExplored(int tileX, int tileY) {
        if (!enabled || explored == null) return true;
        if (tileX < 0 || tileX >= mazeWidth || tileY < 0 || tileY >= mazeHeight) return false;
        return explored[tileX][tileY];
    }

    /**
     * Sets the vision radius (tiles currently visible).
     * @param radius Vision radius in tiles
     */
    public void setVisionRadius(int radius) {
        this.visionRadius = Math.max(1, radius);
    }

    /**
     * Sets the explored radius (tiles marked as explored).
     * @param radius Explored radius in tiles
     */
    public void setExploredRadius(int radius) {
        this.exploredRadius = Math.max(this.visionRadius, radius);
    }

    /**
     * Scales difficulty by reducing vision radius.
     * @param wavesCompleted Number of waves completed
     */
    public void applyDifficultyScaling(int wavesCompleted) {
        // Start with vision radius 5, decrease by 1 every 5 waves, minimum 2
        int baseRadius = 5;
        int reduction = wavesCompleted / 5;
        this.visionRadius = Math.max(2, baseRadius - reduction);
        this.exploredRadius = this.visionRadius + 1;
    }

    /**
     * Enables or disables fog of war.
     * @param enabled Whether fog is enabled
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Checks if fog of war is enabled.
     * @return true if enabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Gets the current vision radius.
     * @return Vision radius in tiles
     */
    public int getVisionRadius() {
        return visionRadius;
    }

    /**
     * Reveals the entire map (for debugging or power-ups).
     */
    public void revealAll() {
        if (explored == null) return;
        for (int x = 0; x < mazeWidth; x++) {
            for (int y = 0; y < mazeHeight; y++) {
                explored[x][y] = true;
            }
        }
    }

    /**
     * Disposes of resources.
     */
    public void dispose() {
        if (shapeRenderer != null) {
            shapeRenderer.dispose();
        }
    }
}
