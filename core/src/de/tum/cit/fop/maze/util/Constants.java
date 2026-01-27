package de.tum.cit.fop.maze.util;

/**
 * Global constants for the Maze Runner game.
 */
public final class Constants {

    private Constants() {
    } // Prevent instantiation

    // Tile & Grid
    public static final int TILE_SIZE = 64;
    public static final int DEFAULT_PLAYER_WIDTH = 32;
    public static final int DEFAULT_PLAYER_HEIGHT = 64;
    // Enemy dimensions (original entity size)
    public static final int DEFAULT_ENEMY_WIDTH = 32;
    public static final int DEFAULT_ENEMY_HEIGHT = 64;

    // Player Movement
    public static final float PLAYER_WALK_SPEED = 200f; // pixels per second
    public static final float PLAYER_RUN_MULTIPLIER = 2.0f;
    public static final float MOVE_COOLDOWN = 0.15f;
    public static final float RUN_COOLDOWN = 0.06f;

    // Player Stats
    public static final int DEFAULT_PLAYER_LIVES = 3;
    public static final float INVINCIBILITY_DURATION = 1.5f;

    // Camera
    public static final float CAMERA_LERP_SPEED = 5f;
    public static final float MIN_ZOOM = 0.5f;
    public static final float MAX_ZOOM = 2.0f;
    public static final float ZOOM_STEP = 0.1f;

    // Animation
    public static final float ANIMATION_FRAME_DURATION = 0.1f;

    // Corner Forgiveness / Movement Assist
    public static final float CORNER_FORGIVENESS_PADDING = 6f; // Collision leniency in pixels
    public static final float CORNER_SNAP_DISTANCE = 12f; // Max distance to auto-align to corridor center
    public static final float CORNER_SNAP_SPEED = 8f; // How fast to nudge player (multiplier for deltaTime)

    // Paths
    public static final String MAPS_DIRECTORY = "maps/";
    public static final String SAVES_DIRECTORY = "saves/";
    public static final String CONFIG_FILE = "config/bindings.json";

    // Asset paths
    public static final String SKIN_PATH = "craft/craftacular-ui.json";
    public static final String CHARACTER_SPRITE = "Swordsman.png";
    public static final String MENU_MUSIC = "background.mp3";
    public static final String GAMEPLAY_MUSIC = "gameplay.mp3";
}
