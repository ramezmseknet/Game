package de.tum.cit.fop.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import de.tum.cit.fop.maze.audio.AudioManager;
import de.tum.cit.fop.maze.save.JsonSaveSystem;
import de.tum.cit.fop.maze.save.GameState;
import de.tum.cit.fop.maze.ui.AnimatedBackground;
import de.tum.cit.fop.maze.ui.UIUtils;
import de.tum.cit.fop.maze.ui.UISoundHelper;

/**
 * The PauseScreen class displays a pause menu overlay when the player presses
 * ESC.
 */
public class PauseScreen implements Screen {

    private final MazeRunnerGame game;
    private final Screen previousScreen;
    private final Stage stage;
    private final JsonSaveSystem saveSystem;
    private final AnimatedBackground background;

    /**
     *
     * @param game
     * @param previousScreen
     */
    public PauseScreen(MazeRunnerGame game, Screen previousScreen) {
        this.game = game;
        this.previousScreen = previousScreen;
        this.saveSystem = new JsonSaveSystem();
        this.background = new AnimatedBackground("backgrounds/menu_bg_sheet.png", 10, 6, 0.25f);

        // Pause game music and play menu music instead
        AudioManager audioManager = AudioManager.getInstance();
        audioManager.pauseGameAndPlayMenu();

        var camera = new OrthographicCamera();
        camera.zoom = 1.5f;

        Viewport viewport = new ScreenViewport(camera);
        stage = new Stage(viewport, game.getSpriteBatch());

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        table.add(new Label("GAME PAUSED", game.getSkin(), "title")).padBottom(80).row();

        // Continue button
        TextButton continueButton = new TextButton("Continue Playing", game.getSkin());
        UIUtils.fitTextToButton(continueButton, 400);
        table.add(continueButton).width(400).height(80).padBottom(20).row();
        UISoundHelper.addClickSound(continueButton);
        continueButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // Stop menu music and resume the paused game music from where it was
                AudioManager audioManager = AudioManager.getInstance();
                audioManager.resumePausedGameMusic();
                game.setScreen(previousScreen);
            }
        });

        // Save Game button
        TextButton saveButton = new TextButton("Save Game", game.getSkin());
        UIUtils.fitTextToButton(saveButton, 400);
        table.add(saveButton).width(400).height(80).padBottom(20).row();
        UISoundHelper.addClickSound(saveButton);
        saveButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                showSaveDialog();
            }
        });

        // Back to menu button
        TextButton menuButton = new TextButton("Back to Main Menu", game.getSkin());
        UIUtils.fitTextToButton(menuButton, 400);
        table.add(menuButton).width(400).height(80).row();
        UISoundHelper.addClickSound(menuButton);
        menuButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.goToMenu();
            }
        });
    }

    private void showSaveDialog() {
        if (!(previousScreen instanceof GameScreen)) {
            showMessage("Error", "Cannot save from this screen.");
            return;
        }

        final TextField slotNameField = new TextField("", game.getSkin());
        slotNameField.setMessageText("...");

        Dialog dialog = new Dialog("Save Game", game.getSkin()) {
            @Override
            protected void result(Object object) {
                if ((Boolean) object) {
                    String slotName = slotNameField.getText().trim();
                    if (slotName.isEmpty()) {
                        slotName = "save_" + System.currentTimeMillis();
                    }

                    GameScreen gameScreen = (GameScreen) previousScreen;
                }
            }
        };

        dialog.getTitleLabel().setFontScale(0.9f);
        dialog.padTop(50);
        dialog.getContentTable().add(new Label("Save Name:", game.getSkin())).padRight(15);
        dialog.getContentTable().add(slotNameField).width(250).row();
        dialog.getContentTable().pad(30);
        dialog.button("Save", true);
        dialog.button("Cancel", false);
        dialog.show(stage);
    }

    private void showMessage(String title, String message) {
        Dialog dialog = new Dialog(title, game.getSkin());
        dialog.text(message);
        dialog.button("OK");
        dialog.show(stage);
    }

    @Override
    public void render(float delta) {
        // Render animated background
        background.update(delta);
        background.render(game.getSpriteBatch());

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            // Resume game music when continuing via ESC
            AudioManager audioManager = AudioManager.getInstance();
            audioManager.resumeMusic();
            game.setScreen(previousScreen);
        }
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
        background.dispose();
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
