package de.tum.cit.fop.maze;

import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import de.tum.cit.fop.maze.screen.AbstractScreen;
import de.tum.cit.fop.maze.ui.AnimatedBackground;
import de.tum.cit.fop.maze.ui.UIUtils;
import de.tum.cit.fop.maze.ui.UISoundHelper;

/**
 * The MenuScreen class is responsible for displaying the main menu of the game.
 * Extends AbstractScreen for common screen functionality.
 */
public class MenuScreen extends AbstractScreen {

    private final AnimatedBackground background;

    /**
     * Constructor for MenuScreen. Sets up the UI elements.
     *
     * @param game The main game class, used to access global resources and methods.
     */
    public MenuScreen(MazeRunnerGame game) {
        super(game, AbstractScreen.DEFAULT_ZOOM);
        // Animated background from sprite sheet (10 cols x 6 rows, 60 frames at 4fps = 0.25s per frame)
        background = new AnimatedBackground("backgrounds/menu_bg_sheet.png", 10, 6, 0.25f);

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        // Add a label as a title
        table.add(new Label("Yllezat Maze!", game.getSkin(), "title")).padBottom(80).row();

        // Create and add a button to go to the game screen
        TextButton goToGameButton = new TextButton("Start Playing", game.getSkin());
        UIUtils.fitTextToButton(goToGameButton, 300);
        table.add(goToGameButton).width(300).padBottom(20).row();
        UISoundHelper.addClickSound(goToGameButton);
        goToGameButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.goToLevelSelection();
            }
        });

        // Saved Games button
        TextButton savedGamesButton = new TextButton("Saved Games", game.getSkin());
        UIUtils.fitTextToButton(savedGamesButton, 300);
        table.add(savedGamesButton).width(300).padBottom(20).row();
        UISoundHelper.addClickSound(savedGamesButton);
        savedGamesButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new SavedGamesScreen(game));
            }
        });

        TextButton howToPlayButton = new TextButton("How to Play", game.getSkin());
        UIUtils.fitTextToButton(howToPlayButton, 300);
        table.add(howToPlayButton).width(300).padBottom(20).row();
        UISoundHelper.addClickSound(howToPlayButton);
        howToPlayButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new HowToPlayScreen(game));
            }
        });

        TextButton storylineButton = new TextButton("Storyline", game.getSkin());
        UIUtils.fitTextToButton(storylineButton, 300);
        table.add(storylineButton).width(300).padBottom(20).row();
        UISoundHelper.addClickSound(storylineButton);
        storylineButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new StorylineScreen(game));
            }
        });

        // Leaderboard button
        TextButton leaderboardButton = new TextButton("Leaderboard", game.getSkin());
        UIUtils.fitTextToButton(leaderboardButton, 300);
        table.add(leaderboardButton).width(300).padBottom(20).row();
        UISoundHelper.addClickSound(leaderboardButton);
        leaderboardButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new LeaderboardScreen(game));
            }
        });

        // Settings button
        TextButton settingsButton = new TextButton("Settings", game.getSkin());
        UIUtils.fitTextToButton(settingsButton, 300);
        table.add(settingsButton).width(300).row();
        UISoundHelper.addClickSound(settingsButton);

        // Install keyboard navigation for menu buttons
        com.badlogic.gdx.utils.Array<com.badlogic.gdx.scenes.scene2d.ui.TextButton> navButtons = new com.badlogic.gdx.utils.Array<>();
        navButtons.add(goToGameButton, savedGamesButton, howToPlayButton, storylineButton);
        navButtons.add(leaderboardButton, settingsButton);
        de.tum.cit.fop.maze.ui.UIInputHelper.installVerticalNavigation(stage, navButtons);
        settingsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new SettingsScreen(game, MenuScreen.this));
            }
        });
    }

    @Override
    protected void draw(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        background.update(delta);
        background.render(game.getSpriteBatch());
        stage.draw();
    }

    @Override
    public void dispose() {
        background.dispose();
        super.dispose();
    }
}
