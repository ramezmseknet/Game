package de.tum.cit.fop.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import de.tum.cit.fop.maze.ui.UISoundHelper;

/**
 * Full-screen "How to Play" screen with controls and game instructions.
 */
@SuppressWarnings("unused")
public class HowToPlayScreen implements Screen {

    private final MazeRunnerGame game;
    private final Stage stage;
    private final Skin skin;
    private final OrthographicCamera camera;

    public HowToPlayScreen(MazeRunnerGame game) {
        this.game = game;
        this.skin = game.getSkin();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        stage = new Stage(new ScreenViewport());
        Table outerTable = new Table();
        outerTable.setFillParent(true);
        stage.addActor(outerTable);

        Table mainTable = new Table();
        mainTable.center();
        mainTable.pad(20);

        Label titleLabel = new Label("How to Play", skin);
        titleLabel.setFontScale(2.0f);
        mainTable.add(titleLabel).padBottom(40).row();

        Label controlsHeader = new Label("CONTROLS", skin);
        controlsHeader.setFontScale(1.5f);
        mainTable.add(controlsHeader).padBottom(20).row();

        Table controlsTable = new Table();
        controlsTable.defaults().pad(5);

        addControlRow(controlsTable, "W / UP Arrow", "Move Up");
        addControlRow(controlsTable, "S / DOWN Arrow", "Move Down");
        addControlRow(controlsTable, "A / LEFT Arrow", "Move Left");
        addControlRow(controlsTable, "D / RIGHT Arrow", "Move Right");
        addControlRow(controlsTable, "SHIFT", "Sprint (move faster)");
        addControlRow(controlsTable, "F", "Interact (open keys)");
        addControlRow(controlsTable, "ESC", "Pause Game");
        addControlRow(controlsTable, "M", "Toggle Minimap");

        mainTable.add(controlsTable).padBottom(30).row();

        // Objective section
        Label objectiveHeader = new Label("OBJECTIVE", skin);
        objectiveHeader.setFontScale(1.5f);
        mainTable.add(objectiveHeader).padBottom(20).row();

        Label objective1 = new Label("1. Find and collect all KEYS in the maze.", skin);
        mainTable.add(objective1).padBottom(10).row();

        Label objective2 = new Label("2. Press F near a key to activate it, then walk over to collect.", skin);
        mainTable.add(objective2).padBottom(10).row();

        Label objective3 = new Label("3. Avoid TRAPS and ENEMIES that will damage you.", skin);
        mainTable.add(objective3).padBottom(10).row();

        Label objective4 = new Label("4. Once you have all keys, find the EXIT DOOR to complete the level!", skin);
        mainTable.add(objective4).padBottom(30).row();

        Label tipsHeader = new Label("TIPS", skin);
        tipsHeader.setFontScale(1.5f);
        mainTable.add(tipsHeader).padBottom(20).row();

        Label tip1 = new Label("- Collect HEARTS to restore health.", skin);
        mainTable.add(tip1).padBottom(10).row();

        Label tip2 = new Label("- Power-ups give temporary boosts like speed or shields.", skin);
        mainTable.add(tip2).padBottom(10).row();

        Label tip3 = new Label("- Use the minimap (M) to navigate the maze.", skin);
        mainTable.add(tip3).padBottom(30).row();

        TextButton backButton = new TextButton("Back to Menu", skin);
        UISoundHelper.addClickSound(backButton);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.goToMenu();
            }
        });
        backButton.pad(10, 30, 10, 30);
        mainTable.add(backButton).minWidth(250).minHeight(60).padBottom(30);

        ScrollPane scrollPane = new ScrollPane(mainTable, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);
        outerTable.add(scrollPane).expand().fill();
    }

    private void addControlRow(Table table, String key, String action) {
        Label keyLabel = new Label(key, skin);
        keyLabel.setFontScale(1.1f);
        table.add(keyLabel).width(200).left();

        Label actionLabel = new Label(action, skin);
        table.add(actionLabel).width(300).left().row();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
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
