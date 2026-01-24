package de.tum.cit.fop.maze.endless;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import de.tum.cit.fop.maze.MazeRunnerGame;
import de.tum.cit.fop.maze.PauseScreen;
import de.tum.cit.fop.maze.ai.AStarPathfinder;
import de.tum.cit.fop.maze.entity.CharacterAnimations;
import de.tum.cit.fop.maze.entity.Player;
import de.tum.cit.fop.maze.entity.collectible.CollectibleType;
import de.tum.cit.fop.maze.entity.collectible.Heart;
import de.tum.cit.fop.maze.entity.collectible.PowerUp;
import de.tum.cit.fop.maze.entity.enemy.ChaserEnemy;
import de.tum.cit.fop.maze.entity.enemy.Enemy;
import de.tum.cit.fop.maze.entity.enemy.EnemyAnimations;
import de.tum.cit.fop.maze.entity.enemy.PatrolEnemy;
import de.tum.cit.fop.maze.entity.interactable.Lever;
import de.tum.cit.fop.maze.entity.obstacle.MovingObstacle;
import de.tum.cit.fop.maze.entity.obstacle.Trap;
import de.tum.cit.fop.maze.event.SimpleEventBus;
import de.tum.cit.fop.maze.input.Action;
import de.tum.cit.fop.maze.input.InputBindings;
import de.tum.cit.fop.maze.input.JsonInputBindings;
import de.tum.cit.fop.maze.ui.HUD;
import de.tum.cit.fop.maze.ui.Minimap;

/**
 * Game screen for endless mode with procedural maze generation,
 * progressive difficulty, and fog of war.
 */
public class EndlessGameScreen implements Screen {

    private final MazeRunnerGame game;
    private final BitmapFont font;

    // Core components
    private GameWorld gameWorld;
    private Maze maze;
    private Camera2DController cameraController;
    private HUD hud;
    private InputBindings inputBindings;
    private SimpleEventBus eventBus;

    // Endless mode specific
    private DifficultyManager difficultyManager;
    private EndlessRunStats runStats;
    private FogOfWarRenderer fogOfWar;
    private Minimap minimap;

    // Game state
    private boolean godMode;
    private float stateTime;
    private InputAdapter gameInputAdapter;
    private OrthographicCamera uiCamera;

    // Wave transition
    private boolean transitioning;
    private float transitionTimer;
    private static final float TRANSITION_DURATION = 2f;

    /**
     * Creates a new endless game screen.
     * @param game The main game instance
     */
    public EndlessGameScreen(MazeRunnerGame game) {
        this(game, DifficultyManager.DifficultyLevel.NORMAL);
    }

    /**
     * Creates a new endless game screen with specified difficulty.
     * @param game The main game instance
     * @param difficulty Starting difficulty level
     */
    public EndlessGameScreen(MazeRunnerGame game, DifficultyManager.DifficultyLevel difficulty) {
        this.game = game;
        this.font = game.getSkin().getFont("font");
        this.stateTime = 0f;
        this.godMode = false;
        this.transitioning = false;
        this.transitionTimer = 0f;

        // Initialize UI camera
        uiCamera = new OrthographicCamera();
        uiCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Initialize endless mode systems
        this.difficultyManager = new DifficultyManager();
        this.difficultyManager.setBaseDifficulty(difficulty);
        this.runStats = new EndlessRunStats();
        this.fogOfWar = new FogOfWarRenderer();

        // Initialize input
        this.inputBindings = new JsonInputBindings();
        this.eventBus = new SimpleEventBus();

        // Create input adapter for scroll wheel zoom
        gameInputAdapter = new InputAdapter() {
            @Override
            public boolean scrolled(float amountX, float amountY) {
                cameraController.handleScrollZoom(amountY);
                return true;
            }
        };

        // Generate first wave
        generateNewWave();

        // Setup event listeners
        setupEventListeners();
    }

    /**
     * Generates a new procedural maze for the current wave.
     */
    private void generateNewWave() {
        // Generate maze based on current difficulty
        ProceduralMazeGenerator generator = new ProceduralMazeGenerator()
            .withDifficulty(difficultyManager);

        maze = generator.generate();

        // Create game world
        gameWorld = new GameWorld(maze, inputBindings, eventBus);

        // Set up textures
        gameWorld.setFloorTexture(game.getFloorTile());
        gameWorld.setEntryTexture(game.getEntryTile());
        gameWorld.setExitTexture(game.getExitTile());
        gameWorld.setAutoTileMapper(game.getAutoTileMapper());

        // Setup player
        Player player = gameWorld.getPlayer();
        CharacterAnimations playerAnimations = new CharacterAnimations("Swordsman.png", "SwordsmanAttack.png");
        player.setCharacterAnimations(playerAnimations);
        player.setAllDirectionAnimation(game.getCharacterDownAnimation());
        player.applySkillEffects();

        // Initialize fog of war
        fogOfWar.init(maze);
        fogOfWar.applyDifficultyScaling(difficultyManager.getWavesCompleted());

        // Initialize minimap with fog of war
        if (minimap == null) {
            minimap = new Minimap(150);
        }
        minimap.initFogOfWar(maze);

        // Create camera controller
        cameraController = new Camera2DController(
            Gdx.graphics.getWidth(),
            Gdx.graphics.getHeight()
        );
        cameraController.setTarget(player);
        cameraController.setMaze(maze);
        cameraController.snapToTarget();

        // Create HUD if needed
        if (hud == null) {
            hud = new HUD(font);
        }

        // Spawn entities
        spawnEntities();
    }

    /**
     * Spawns entities based on maze data with difficulty scaling.
     */
    private void spawnEntities() {
        // Spawn levers
        int leverIndex = 0;
        for (Vector2 pos : maze.getKeyPositions()) {
            Lever lever = new Lever("lever_" + leverIndex++, (int) pos.x, (int) pos.y);
            gameWorld.addLever(lever);
        }

        // Spawn hearts
        for (Vector2 pos : maze.getHeartPositions()) {
            Heart heart = new Heart((int) pos.x, (int) pos.y);
            gameWorld.addCollectible(heart);
        }

        // Spawn power-ups
        for (Vector2 pos : maze.getPowerUpPositions()) {
            TileType tile = maze.getTileAt((int) pos.x, (int) pos.y);
            if (tile == TileType.SPRINT_POWERUP) {
                de.tum.cit.fop.maze.entity.collectible.SprintPowerUp sprintPowerUp =
                    new de.tum.cit.fop.maze.entity.collectible.SprintPowerUp((int) pos.x, (int) pos.y);
                gameWorld.addCollectible(sprintPowerUp);
            } else {
                CollectibleType type = tile == TileType.SPEED_POWERUP
                    ? CollectibleType.SPEED_BOOST
                    : CollectibleType.SHIELD;
                PowerUp powerUp = new PowerUp(type, (int) pos.x, (int) pos.y, 10f);
                gameWorld.addCollectible(powerUp);
            }
        }

        // Spawn traps
        for (Vector2 pos : maze.getTrapPositions()) {
            Trap trap = new Trap((int) pos.x, (int) pos.y);
            gameWorld.addObstacle(trap);
        }

        // Spawn moving obstacles
        for (Vector2 pos : maze.getMovingObstaclePositions()) {
            int leftBound = Math.max(1, (int) pos.x - 2);
            int rightBound = Math.min(maze.getWidth() - 2, (int) pos.x + 2);
            MovingObstacle boulder = MovingObstacle.horizontal((int) pos.y, leftBound, rightBound);
            gameWorld.addObstacle(boulder);
        }

        // Spawn enemies with pathfinding and difficulty scaling
        AStarPathfinder pathfinder = new AStarPathfinder(false);
        pathfinder.setMaze(maze);

        float speedMultiplier = difficultyManager.getEnemySpeedMultiplier();
        int enemyIndex = 0;

        for (Vector2 pos : maze.getEnemySpawns()) {
            Enemy enemy;
            // More chasers at higher difficulty
            boolean isChaser = enemyIndex % 3 != 0 || difficultyManager.getWavesCompleted() > 5;

            if (isChaser) {
                enemy = new ChaserEnemy("chaser_" + enemyIndex, (int) pos.x, (int) pos.y);
            } else {
                enemy = new PatrolEnemy("patrol_" + enemyIndex, (int) pos.x, (int) pos.y);
            }

            enemy.setPathfinder(pathfinder);
            enemy.setMaze(maze);
            enemy.setTargetPlayer(gameWorld.getPlayer());
            enemy.setEnemyAnimations(new EnemyAnimations("mobs.png"));

            // Apply speed scaling
            enemy.setSpeed(enemy.getSpeed() * speedMultiplier);

            gameWorld.addEnemy(enemy);
            enemyIndex++;
        }
    }

    /**
     * Sets up event listeners for the endless mode.
     */
    private void setupEventListeners() {
        de.tum.cit.fop.maze.audio.AudioManager audio = de.tum.cit.fop.maze.audio.AudioManager.getInstance();

        // Sound effects
        eventBus.subscribe(de.tum.cit.fop.maze.event.PlayerDamagedEvent.class, event -> {
            audio.playSound("player_hurt");
            runStats.onDamageTaken(1);
        });

        eventBus.subscribe(de.tum.cit.fop.maze.event.HeartCollectedEvent.class, event -> {
            audio.playSound("collect_heart");
            runStats.onHeartCollected();
        });

        eventBus.subscribe(de.tum.cit.fop.maze.event.CoinCollectedEvent.class, event -> {
            audio.playSound("collect_coin");
            runStats.onCoinCollected(event.getValue());
        });

        eventBus.subscribe(de.tum.cit.fop.maze.event.EnemyDefeatedEvent.class, event -> {
            runStats.onEnemyDefeated(100);
            // Spawn heart every 3 kills
            if (gameWorld.getTotalEnemiesKilled() % 3 == 0) {
                gameWorld.spawnHeartDrop(event.getDeathX(), event.getDeathY());
            }
        });

        eventBus.subscribe(de.tum.cit.fop.maze.event.LevelCompletedEvent.class, event -> {
            audio.playSound("level_complete");
        });
    }

    @Override
    public void render(float delta) {
        stateTime += delta;

        // Handle pause
        if (inputBindings.isJustPressed(Action.PAUSE)) {
            game.setScreen(new PauseScreen(game, this));
            return;
        }

        // Handle zoom
        if (inputBindings.isJustPressed(Action.ZOOM_IN)) {
            cameraController.zoomIn();
        }
        if (inputBindings.isJustPressed(Action.ZOOM_OUT)) {
            cameraController.zoomOut();
        }

        // Toggle minimap with M key
        if (Gdx.input.isKeyJustPressed(Input.Keys.M)) {
            minimap.toggle();
        }

        // Handle transition between waves
        if (transitioning) {
            updateTransition(delta);
        } else {
            updateGame(delta);
        }

        // Render
        renderGame(delta);
    }

    /**
     * Updates game logic during normal gameplay.
     */
    private void updateGame(float delta) {
        // Update difficulty over time
        difficultyManager.update(delta);

        // Update run stats
        runStats.update(delta);

        // Update game world
        gameWorld.update(delta);

        // Update fog of war
        fogOfWar.update(gameWorld.getPlayer());

        // Update minimap exploration
        minimap.updateExploration(gameWorld.getPlayer());

        // Update camera
        cameraController.update(delta);

        // Check for game over
        if (!godMode && gameWorld.isGameOver()) {
            game.setScreen(new EndlessGameOverScreen(game, runStats));
            dispose();
            return;
        }

        // Check for wave complete
        if (gameWorld.isLevelComplete()) {
            startWaveTransition();
        }
    }

    /**
     * Starts transition to next wave.
     */
    private void startWaveTransition() {
        transitioning = true;
        transitionTimer = 0f;

        // Record wave completion
        int waveBonus = 500 * (difficultyManager.getWavesCompleted() + 1);
        runStats.onWaveCompleted(waveBonus);
        difficultyManager.onWaveCompleted();
    }

    /**
     * Updates wave transition animation.
     */
    private void updateTransition(float delta) {
        transitionTimer += delta;

        if (transitionTimer >= TRANSITION_DURATION) {
            transitioning = false;
            generateNewWave();
        }
    }

    /**
     * Renders the game.
     */
    private void renderGame(float delta) {
        ScreenUtils.clear(0.1f, 0.1f, 0.15f, 1);

        // Render world
        game.getSpriteBatch().setProjectionMatrix(cameraController.getCamera().combined);
        game.getSpriteBatch().begin();
        gameWorld.render(game.getSpriteBatch());
        game.getSpriteBatch().end();

        // Render fog of war overlay
        fogOfWar.render(cameraController.getCamera());

        // Render HUD
        hud.render(game.getSpriteBatch(), gameWorld.getPlayer(), maze, gameWorld.isAnyLeverOpen());

        // Render endless mode stats overlay
        renderEndlessHUD();

        // Render minimap
        minimap.render(maze, gameWorld.getPlayer(), getEnemyPositions());

        // Render transition overlay
        if (transitioning) {
            renderTransitionOverlay();
        }
    }

    /**
     * Renders endless mode specific HUD elements.
     */
    private void renderEndlessHUD() {
        game.getSpriteBatch().setProjectionMatrix(uiCamera.combined);
        game.getSpriteBatch().begin();

        float x = 10;
        float y = Gdx.graphics.getHeight() - 10;

        // Wave number
        font.setColor(Color.GOLD);
        font.draw(game.getSpriteBatch(), "Wave: " + (difficultyManager.getWavesCompleted() + 1), x, y);

        // Score
        font.setColor(Color.WHITE);
        font.draw(game.getSpriteBatch(), "Score: " + runStats.getScore(), x, y - 25);

        // Time
        font.draw(game.getSpriteBatch(), "Time: " + runStats.getFormattedTime(), x, y - 50);

        // Combo multiplier (if active)
        if (runStats.getComboMultiplier() > 1) {
            font.setColor(Color.CYAN);
            font.draw(game.getSpriteBatch(), "x" + runStats.getComboMultiplier() + " COMBO!", x, y - 75);
        }

        // Difficulty indicator
        font.setColor(Color.ORANGE);
        font.draw(game.getSpriteBatch(), difficultyManager.getDifficultyDescription(),
            Gdx.graphics.getWidth() - 100, y);

        // Vision indicator (fog of war)
        font.setColor(Color.GRAY);
        font.draw(game.getSpriteBatch(), "Vision: " + fogOfWar.getVisionRadius(),
            Gdx.graphics.getWidth() - 100, y - 25);

        font.setColor(Color.WHITE);
        game.getSpriteBatch().end();
    }

    /**
     * Renders wave transition overlay.
     */
    private void renderTransitionOverlay() {
        game.getSpriteBatch().setProjectionMatrix(uiCamera.combined);
        game.getSpriteBatch().begin();

        float centerX = Gdx.graphics.getWidth() / 2f;
        float centerY = Gdx.graphics.getHeight() / 2f;

        font.setColor(Color.GOLD);
        String waveText = "Wave " + (difficultyManager.getWavesCompleted()) + " Complete!";
        font.draw(game.getSpriteBatch(), waveText, centerX - 80, centerY + 30);

        font.setColor(Color.WHITE);
        String nextText = "Preparing Wave " + (difficultyManager.getWavesCompleted() + 1) + "...";
        font.draw(game.getSpriteBatch(), nextText, centerX - 100, centerY - 10);

        font.setColor(Color.WHITE);
        game.getSpriteBatch().end();
    }

    /**
     * Gets enemy positions for minimap rendering.
     */
    private Vector2[] getEnemyPositions() {
        java.util.List<Enemy> enemies = gameWorld.getEnemies();
        Vector2[] positions = new Vector2[enemies.size()];
        for (int i = 0; i < enemies.size(); i++) {
            positions[i] = enemies.get(i).getPosition();
        }
        return positions;
    }

    /**
     * Gets the game world.
     */
    public GameWorld getGameWorld() {
        return gameWorld;
    }

    /**
     * Gets the camera controller.
     */
    public Camera2DController getCameraController() {
        return cameraController;
    }

    /**
     * Gets the run stats.
     */
    public EndlessRunStats getRunStats() {
        return runStats;
    }

    @Override
    public void resize(int width, int height) {
        cameraController.resize(width, height);
        uiCamera.setToOrtho(false, width, height);
        hud.resize(width, height);
        minimap.resize(width, height);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(gameInputAdapter);
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void pause() {
        if (gameWorld != null) {
            gameWorld.setPaused(true);
        }
    }

    @Override
    public void resume() {
        if (gameWorld != null) {
            gameWorld.setPaused(false);
        }
    }

    @Override
    public void dispose() {
        if (hud != null) hud.dispose();
        if (gameWorld != null) gameWorld.dispose();
        if (minimap != null) minimap.dispose();
        if (fogOfWar != null) fogOfWar.dispose();
    }
}
