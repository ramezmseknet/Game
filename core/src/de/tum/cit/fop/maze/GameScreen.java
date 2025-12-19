package de.tum.cit.fop.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.files.FileHandle;
import java.util.Properties;
import java.io.IOException;


/**
 * The GameScreen class is responsible for rendering the gameplay screen.
 * It handles the game logic and rendering of the game elements.
 */
public class GameScreen implements Screen {

    private final MazeRunnerGame game;
    private final OrthographicCamera camera;
    private final OrthographicCamera uiCamera;
    private final BitmapFont font;


    private static final int PLAYER_WIDTH = 32;
    private static final int PLAYER_HEIGHT= 64;
    private float moveCooldown = 0f;
    private static final float MOVE_DELAY = 0.15f;
    private float stateTime; // Used for animation frame time


    private int[][] mazeLayout;
    private int exitX;
    private int exitY;
    private int playerPositionX;
    private int playerPositionY;


    private int TILE_SIZE;


    /**
     * Constructor for GameScreen.
     *
     * @param game The main game class, used to access global resources and methods.
     */
    public GameScreen(MazeRunnerGame game, String levelFile) {
        this.game = game;

        // Create and configure the camera for the game view
        camera = new OrthographicCamera();
        uiCamera = new OrthographicCamera();

        // Get the font from the game's skin
        font = game.getSkin().getFont("font");
        stateTime = 0f;

        loadMazeFromFile(levelFile);
        setupCamera();
    }




    private boolean isMoveValid(int newX, int newY) {
        if (newX < 0 || newX >= mazeLayout[0].length || newY < 0 || newY >= mazeLayout.length) {
            return false;
        }
        return mazeLayout[newY][newX] != 0 && mazeLayout[newY][newX] != 3;  // Can't walk on walls or traps
    }

    private boolean hasReachedFinish() {
        return playerPositionX == exitX && playerPositionY == exitY;
    }

    // Screen interface methods with necessary functionality
    @Override
    public void render(float delta) {
        stateTime += delta * 2.0f;

        // Check for escape key press FIRST
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new PauseScreen(game, this));
            return;
        }

        // Handle movement
        moveCooldown -= delta;


        int nextTileX = playerPositionX;
        int nextTileY = playerPositionY;
        boolean isMoving = false;

        float currentDelay = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT) ? 0.06f : MOVE_DELAY;

        if (moveCooldown <= 0f) {
            // W - Up
            if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) {
                nextTileY = playerPositionY + 1;
                isMoving = true;
            }
            // S - Down
            else if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
                nextTileY = playerPositionY - 1;
                isMoving = true;
            }
            // A - Left
            else if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                nextTileX = playerPositionX - 1;
                isMoving = true;
            }
            // D - Right
            else if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                nextTileX = playerPositionX + 1;
                isMoving = true;
            }

            if (isMoving && isMoveValid(nextTileX, nextTileY)) {
                playerPositionX = nextTileX;
                playerPositionY = nextTileY;
                moveCooldown = currentDelay;;

                // Calculate desired camera position
                float camX = playerPositionX * TILE_SIZE + TILE_SIZE / 2f;
                float camY = playerPositionY * TILE_SIZE + TILE_SIZE / 2f;

                // Calculate maze boundaries
                float mazeWidth = mazeLayout[0].length * TILE_SIZE;
                float mazeHeight = mazeLayout.length * TILE_SIZE;

                // Use viewportWidth/Height to handle the current screen size
                if (mazeWidth <= camera.viewportWidth) {
                    camX = mazeWidth / 2f;
                } else {
                    camX = Math.max(camera.viewportWidth / 2f, Math.min(camX, mazeWidth - camera.viewportWidth / 2f));
                }

                if (mazeHeight <= camera.viewportHeight) {
                    camY = mazeHeight / 2f;
                } else {
                    camY = Math.max(camera.viewportHeight / 2f, Math.min(camY, mazeHeight - camera.viewportHeight / 2f));
                }

                camera.position.set(camX, camY, 0);
                camera.update();

            } else if (!isMoving || !isMoveValid(nextTileX, nextTileY)) {
                stateTime = 0; // Reset if not moving OR if the intended move is blocked by a wall
            }
            // Check if player reached the finish
            if (hasReachedFinish()) {
                game.setScreen(new FinishScreen(game));
                dispose();
                return;
            }
        }

        ScreenUtils.clear(0, 0, 0, 1); // Clear the screen
        // Set up and begin drawing with the sprite batch
        game.getSpriteBatch().setProjectionMatrix(camera.combined);

        game.getSpriteBatch().begin(); // Important to call this before drawing anything

        for (int y = 0; y < mazeLayout.length; y++) {
            for (int x = 0; x < mazeLayout[y].length; x++) {
                if (mazeLayout[y][x] == 0) {
                    game.getSpriteBatch().draw(
                            game.getWallTile(),
                            x * TILE_SIZE,
                            y * TILE_SIZE,
                            TILE_SIZE,
                            TILE_SIZE
                    );
                }
            }
        }

        float charX = playerPositionX * TILE_SIZE + (TILE_SIZE - PLAYER_WIDTH) / 2.0f;
        float charY = playerPositionY * TILE_SIZE + (TILE_SIZE - PLAYER_HEIGHT) / 2.0f;



        game.getSpriteBatch().draw(
                game.getCharacterDownAnimation().getKeyFrame(stateTime, true),
                charX,
                charY,
                PLAYER_WIDTH,
                PLAYER_HEIGHT
        );

        game.getSpriteBatch().end(); // End world drawing

        // Draw UI with separate camera
        game.getSpriteBatch().setProjectionMatrix(uiCamera.combined);
        game.getSpriteBatch().begin();

        font.draw(game.getSpriteBatch(),
                "Press ESC to go to menu",
                10,
                Gdx.graphics.getHeight() - 10
        );

        game.getSpriteBatch().end(); // Important to call this after drawing everything
    }

    /**
     * Loads the maze layout from a properties file.
     */
    private void loadMazeFromFile(String filePath) {
        try {
            FileHandle file = Gdx.files.internal(filePath);
            Properties properties = new Properties();
            properties.load(file.read());

            // Find the dimensions by checking max x and y
            int maxX = 0;
            int maxY = 0;

            for (String key : properties.stringPropertyNames()) {
                if (!key.contains(",")) {
                    continue;
                }

                String[] coords = key.split(",");
                int x = Integer.parseInt(coords[0]);
                int y = Integer.parseInt(coords[1]);
                maxX = Math.max(maxX, x);
                maxY = Math.max(maxY, y);
            }

            // Create the maze array
            int width = maxX + 1;
            int height = maxY + 1;
            mazeLayout = new int[height][width];

            // Initialize with walls (1) by default
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    mazeLayout[y][x] = 1;
                }
            }

            // Parse the properties and fill the maze
            for (String key : properties.stringPropertyNames()) {
                // Skip non-coordinate keys
                if (!key.contains(",")) {
                    continue;
                }

                String[] coords = key.split(",");
                int x = Integer.parseInt(coords[0]);
                int y = Integer.parseInt(coords[1]);
                int value = Integer.parseInt(properties.getProperty(key));

                mazeLayout[y][x] = value;

                if (value == 1) {
                    playerPositionX = x;
                    playerPositionY = y;
                }
                if (value == 2) {
                    exitX = x;
                    exitY = y;
                }
            }

        } catch (IOException e) {
            Gdx.app.error("GameScreen", "Failed to load maze file: " + filePath, e);
            mazeLayout = new int[15][15];
        }
    }

    private void setupCamera() {
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight= Gdx.graphics.getHeight();

        // Fixed tile size instead of scaling to fit entire maze
        TILE_SIZE = 64;


        camera.setToOrtho(false, screenWidth, screenHeight);
        uiCamera.setToOrtho(false, screenWidth, screenHeight);
        // Calculate desired camera position on player
        float mazeWidth = mazeLayout[0].length * TILE_SIZE;
        float mazeHeight = mazeLayout.length * TILE_SIZE;

        float camX = playerPositionX * TILE_SIZE + TILE_SIZE / 2f;
        float camY = playerPositionY * TILE_SIZE + TILE_SIZE / 2f;

        // CENTER logic for small maps
        if (mazeWidth <= screenWidth) {
            camX = mazeWidth / 2f;
        } else {
            camX = Math.max(screenWidth / 2f, Math.min(camX, mazeWidth - screenWidth / 2f));
        }

        if (mazeHeight <= screenHeight) {
            camY = mazeHeight / 2f;
        } else {
            camY = Math.max(screenHeight / 2f, Math.min(camY, mazeHeight - screenHeight / 2f));
        }

        camera.position.set(camX, camY, 0);
        camera.update();
        uiCamera.update();

    }





    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
        uiCamera.setToOrtho(false, width, height); // ADD THIS
        setupCamera();
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void show() {

    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
    }
}