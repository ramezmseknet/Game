package de.tum.cit.fop.maze;


import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import de.tum.cit.fop.maze.audio.AudioManager;
import de.tum.cit.fop.maze.screen.AbstractScreen;
import de.tum.cit.fop.maze.ui.AnimatedBackground;
import de.tum.cit.fop.maze.ui.UISoundHelper;

/**
 * The FinishScreen class displays a victory screen when the player completes
 * the maze.
 * Extends AbstractScreen for common screen functionality.
 */
public class FinishScreen extends AbstractScreen {

    private final AnimatedBackground background;

    public FinishScreen(MazeRunnerGame game) {
        super(game, AbstractScreen.DEFAULT_ZOOM);
        this.background = new AnimatedBackground("backgrounds/menu_bg_sheet.png", 10, 6, 0.25f);

        // Play menu music instead of game music
        AudioManager.getInstance().playMenuMusic();

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        table.add(new Label("Congratulations!", game.getSkin(), "title")).padBottom(40).row();
        table.add(new Label("You completed the maze!", game.getSkin())).padBottom(80).row();

        TextButton newGameButton = new TextButton("New Game", game.getSkin());
        table.add(newGameButton).width(300).padBottom(20).row();
        UISoundHelper.addClickSound(newGameButton);
        newGameButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.goToLevelSelection();
            }
        });

        TextButton menuButton = new TextButton("Return to Menu", game.getSkin());
        table.add(menuButton).width(300).row();
        UISoundHelper.addClickSound(menuButton);
        menuButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.goToMenu();
            }
        });

        com.badlogic.gdx.utils.Array<com.badlogic.gdx.scenes.scene2d.ui.TextButton> navButtons = new com.badlogic.gdx.utils.Array<>();
        navButtons.add(newGameButton, menuButton);
        de.tum.cit.fop.maze.ui.UIInputHelper.installVerticalNavigation(stage, navButtons);
    }

    @Override
    protected void draw(float delta) {
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
