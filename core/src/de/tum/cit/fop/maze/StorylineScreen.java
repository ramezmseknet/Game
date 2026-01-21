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
 * Full-screen "Storyline" screen displaying the game's narrative.
 */
@SuppressWarnings("unused")
public class StorylineScreen implements Screen {

    private final MazeRunnerGame game;
    private final Stage stage;
    private final Skin skin;
    private final OrthographicCamera camera;

    public StorylineScreen(MazeRunnerGame game) {
        this.game = game;
        this.skin = game.getSkin();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        stage = new Stage(new ScreenViewport());
        Table outerTable = new Table();
        outerTable.setFillParent(true);
        stage.addActor(outerTable);

        // Create content table that will be scrollable
        Table mainTable = new Table();
        mainTable.center();
        mainTable.pad(20);

        // Title
        Label titleLabel = new Label("The Story", skin);
        titleLabel.setFontScale(2.0f);
        mainTable.add(titleLabel).padBottom(50).row();

        // Chapter 1
        Label chapter1Title = new Label("Chapter 1: The Fortress", skin);
        chapter1Title.setFontScale(1.5f);
        mainTable.add(chapter1Title).padBottom(20).row();

        String chapter1Text = "Deep within an ancient fortress, hidden in the mountains,\n" +
                "lies a maze of corridors and chambers built centuries ago.\n" +
                "Many have entered, but few have ever returned...";
        Label chapter1 = new Label(chapter1Text, skin);
        mainTable.add(chapter1).padBottom(30).row();

        // Chapter 2
        Label chapter2Title = new Label("Chapter 2: The Hero", skin);
        chapter2Title.setFontScale(1.5f);
        mainTable.add(chapter2Title).padBottom(20).row();

        String chapter2Text = "A brave soldier named Ylli ventures into the fortress,\n" +
                "seeking to uncover its secrets and find a way through.\n" +
                "Armed only with courage and determination,\n" +
                "Ylli must navigate the treacherous maze.";
        Label chapter2 = new Label(chapter2Text, skin);
        mainTable.add(chapter2).padBottom(30).row();

        // Chapter 3
        Label chapter3Title = new Label("Chapter 3: The Challenge", skin);
        chapter3Title.setFontScale(1.5f);
        mainTable.add(chapter3Title).padBottom(20).row();

        String chapter3Text = "The maze is filled with deadly traps and fearsome creatures.\n" +
                "Ancient keys are scattered throughout, each one essential\n" +
                "to unlock the final door that leads to freedom.\n\n" +
                "Collect all the keys, avoid the dangers, and escape!\n" +
                "Your journey begins now...";
        Label chapter3 = new Label(chapter3Text, skin);
        mainTable.add(chapter3).padBottom(40).row();

        // Back button
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

        // Wrap content in ScrollPane
        ScrollPane scrollPane = new ScrollPane(mainTable, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false); // Only vertical scrolling
        outerTable.add(scrollPane).expand().fill();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
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
        // no background video to dispose
        stage.dispose();
    }
}
