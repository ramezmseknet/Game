package de.tum.cit.fop.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import de.tum.cit.fop.maze.ui.AnimatedBackground;
import de.tum.cit.fop.maze.ui.UISoundHelper;

/**
 * The LevelSelectionScreen allows players to choose between different levels.
 */
@SuppressWarnings("unused")
public class  LevelSelectionScreen implements Screen {

    private final MazeRunnerGame game;
    private final Stage stage;
    private final AnimatedBackground background;

    public LevelSelectionScreen(MazeRunnerGame game) {
        this.game = game;

        var camera = new OrthographicCamera();
        camera.zoom = 1.5f;

        Viewport viewport = new ScreenViewport(camera);
        stage = new Stage(viewport, game.getSpriteBatch());

        background = new AnimatedBackground("backgrounds/menu_bg_sheet.png", 10, 6, 0.25f);

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        table.add(new Label("Select Level", game.getSkin(), "title")).padBottom(60).row();

        // Create buttons for each level
        com.badlogic.gdx.utils.Array<com.badlogic.gdx.scenes.scene2d.ui.TextButton> navButtons = new com.badlogic.gdx.utils.Array<>();
        for (int i = 1; i <= 5; i++) {
            final int levelNum = i;
            TextButton levelButton = new TextButton("Level " + i, game.getSkin());
            table.add(levelButton).width(300).height(70).padBottom(15).row();

            UISoundHelper.addClickSound(levelButton);
            levelButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    game.goToGame("maps/level-" + levelNum + ".properties");
                }
            });

            navButtons.add(levelButton);
        }

        // Back button
        table.row().padTop(20);
        TextButton backButton = new TextButton("Back to Menu", game.getSkin());
        table.add(backButton).width(300).height(70).row();
        UISoundHelper.addClickSound(backButton);
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.goToMenu();
            }
        });

        // Add back button to navigation and install helper
        navButtons.add(backButton);
        de.tum.cit.fop.maze.ui.UIInputHelper.installVerticalNavigation(stage, navButtons);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        background.update(delta);
        background.render(game.getSpriteBatch());
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        background.dispose();
        stage.dispose();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
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
}
