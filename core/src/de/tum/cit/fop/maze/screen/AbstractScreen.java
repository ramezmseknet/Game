package de.tum.cit.fop.maze.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import de.tum.cit.fop.maze.MazeRunnerGame;

/**
 * Abstract base class for all game screens.
 * Provides common setup for camera, viewport, and stage.
 */
public abstract class AbstractScreen implements Screen {

    protected final MazeRunnerGame game;
    protected final OrthographicCamera camera;
    protected final Viewport viewport;
    protected final Stage stage;

    public static final float DEFAULT_ZOOM = 1.5f;

    public AbstractScreen(MazeRunnerGame game) {
        this(game, 1.0f);
    }


    public AbstractScreen(MazeRunnerGame game, float zoom) {
        this.game = game;

        this.camera = new OrthographicCamera();
        this.camera.zoom = zoom;
        this.viewport = new ScreenViewport(camera);
        this.stage = new Stage(viewport, game.getSpriteBatch());
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        update(delta);
        draw(delta);
    }


    protected void update(float delta) {
        stage.act(Math.min(delta, 1/30f));
    }


    protected abstract void draw(float delta);

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    public MazeRunnerGame getGame() {
        return game;
    }
}
