package de.tum.cit.fop.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import de.tum.cit.fop.maze.audio.AudioManager;
import de.tum.cit.fop.maze.input.Action;
import de.tum.cit.fop.maze.input.JsonInputBindings;
import de.tum.cit.fop.maze.ui.UISoundHelper;

/**
 * Settings screen for adjusting audio and controls.
 */
public class SettingsScreen implements Screen {

    private final MazeRunnerGame game;
    private final Screen previousScreen;
    private final Stage stage;
    private final Skin skin;
    private final OrthographicCamera camera;

    // Audio settings
    private Slider musicSlider;
    private Slider sfxSlider;
    private Label musicValueLabel;
    private Label sfxValueLabel;

    // Key rebinding
    private JsonInputBindings inputBindings;
    private Action rebindingAction;
    private TextButton rebindingButton;
    private boolean isRebinding;

    public SettingsScreen(MazeRunnerGame game, Screen previousScreen) {
        this.game = game;
        this.previousScreen = previousScreen;
        this.skin = game.getSkin();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        stage = new Stage(new ScreenViewport());

        createUI();
    }

    private void createUI() {
        // Create outer table for layout
        Table outerTable = new Table();
        outerTable.setFillParent(true);
        stage.addActor(outerTable);

        // Create content table that will be scrollable
        Table mainTable = new Table();
        mainTable.center();
        mainTable.pad(20);

        // Title
        Label titleLabel = new Label("Settings", skin);
        titleLabel.setFontScale(2.0f);
        mainTable.add(titleLabel).colspan(3).padBottom(40).row();

        // Audio Section
        Label audioTitle = new Label("Audio", skin);
        audioTitle.setFontScale(1.5f);
        mainTable.add(audioTitle).colspan(3).padBottom(20).row();

        AudioManager audio = AudioManager.getInstance();

        // Music Volume
        mainTable.add(new Label("Music Volume:", skin)).padRight(20);
        musicSlider = new Slider(0f, 1f, 0.05f, false, skin);
        musicSlider.setValue(audio.getMusicVolume());
        mainTable.add(musicSlider).width(200);
        musicValueLabel = new Label(String.format("%d%%", (int) (audio.getMusicVolume() * 100)), skin);
        mainTable.add(musicValueLabel).width(60).padLeft(10).row();

        musicSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                float value = musicSlider.getValue();
                audio.setMusicVolume(value);
                musicValueLabel.setText(String.format("%d%%", (int) (value * 100)));
            }
        });

        // SFX Volume
        mainTable.add(new Label("SFX Volume:", skin)).padRight(20);
        sfxSlider = new Slider(0f, 1f, 0.05f, false, skin);
        sfxSlider.setValue(audio.getSfxVolume());
        mainTable.add(sfxSlider).width(200);
        sfxValueLabel = new Label(String.format("%d%%", (int) (audio.getSfxVolume() * 100)), skin);
        mainTable.add(sfxValueLabel).width(60).padLeft(10).row();

        sfxSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                float value = sfxSlider.getValue();
                audio.setSfxVolume(value);
                sfxValueLabel.setText(String.format("%d%%", (int) (value * 100)));
            }
        });

        mainTable.add().padBottom(30).row();

        // Controls Section
        Label controlsTitle = new Label("Controls", skin);
        controlsTitle.setFontScale(1.5f);
        mainTable.add(controlsTitle).colspan(3).padBottom(20).row();

        // Try to get JsonInputBindings
        inputBindings = new JsonInputBindings();

        // Create binding buttons for common actions
        addKeyBindingRow(mainTable, "Move Up", Action.MOVE_UP);
        addKeyBindingRow(mainTable, "Move Down", Action.MOVE_DOWN);
        addKeyBindingRow(mainTable, "Move Left", Action.MOVE_LEFT);
        addKeyBindingRow(mainTable, "Move Right", Action.MOVE_RIGHT);
        addKeyBindingRow(mainTable, "Run", Action.RUN);
        addKeyBindingRow(mainTable, "Interact (Open Key)", Action.INTERACT);
        addKeyBindingRow(mainTable, "Pause", Action.PAUSE);

        mainTable.add().padBottom(30).row();

        // Back button
        TextButton backButton = new TextButton("Back", skin);
        UISoundHelper.addClickSound(backButton);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                saveSettings();
                game.setScreen(previousScreen);
            }
        });
        mainTable.add(backButton).colspan(3).padTop(30).padBottom(30);

        // Wrap content in ScrollPane
        ScrollPane scrollPane = new ScrollPane(mainTable, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false); // Only vertical scrolling
        outerTable.add(scrollPane).expand().fill();
    }

    private void addKeyBindingRow(Table table, String actionName, Action action) {
        table.add(new Label(actionName + ":", skin)).padRight(20);

        String keyName = JsonInputBindings.getKeyName(inputBindings.getPrimaryKey(action));
        TextButton keyButton = new TextButton(keyName, skin);

        UISoundHelper.addClickSound(keyButton);
        keyButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                startRebinding(action, keyButton);
            }
        });

        table.add(keyButton).width(150).colspan(2).row();
    }

    private void startRebinding(Action action, TextButton button) {
        if (isRebinding) {
            // Cancel previous rebinding
            rebindingButton.setText(JsonInputBindings.getKeyName(inputBindings.getPrimaryKey(rebindingAction)));
        }

        isRebinding = true;
        rebindingAction = action;
        rebindingButton = button;
        button.setText("Press a key...");
    }

    private void finishRebinding(int keycode) {
        if (!isRebinding)
            return;

        inputBindings.setPrimaryKey(rebindingAction, keycode);
        rebindingButton.setText(JsonInputBindings.getKeyName(keycode));

        isRebinding = false;
        rebindingAction = null;
        rebindingButton = null;
    }

    private void cancelRebinding() {
        if (!isRebinding)
            return;

        rebindingButton.setText(JsonInputBindings.getKeyName(inputBindings.getPrimaryKey(rebindingAction)));
        isRebinding = false;
        rebindingAction = null;
        rebindingButton = null;
    }

    private void saveSettings() {
        AudioManager.getInstance().saveSettings();
        inputBindings.save();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        // Handle key rebinding
        if (isRebinding) {
            // Use Backspace to cancel rebinding (allows Escape to be bound)
            if (Gdx.input.isKeyJustPressed(Keys.BACKSPACE)) {
                cancelRebinding();
            } else {
                for (int i = 0; i < 256; i++) {
                    if (Gdx.input.isKeyJustPressed(i) && i != Keys.BACKSPACE) {
                        finishRebinding(i);
                        break;
                    }
                }
            }
        }

        ScreenUtils.clear(0, 0, 0, 1);

        camera.update();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
