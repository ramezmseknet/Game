package de.tum.cit.fop.maze.endless;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ScreenUtils;
import de.tum.cit.fop.maze.MazeRunnerGame;
import de.tum.cit.fop.maze.screen.AbstractScreen;

/**
 * Game over screen for endless mode showing run statistics.
 */
public class EndlessGameOverScreen extends AbstractScreen {

    private final EndlessRunStats stats;

    /**
     * Creates a game over screen with the run statistics.
     * @param game The main game instance
     * @param stats The run statistics
     */
    public EndlessGameOverScreen(MazeRunnerGame game, EndlessRunStats stats) {
        super(game, AbstractScreen.DEFAULT_ZOOM);
        this.stats = stats;

        setupUI();
    }

    /**
     * Sets up the UI elements.
     */
    private void setupUI() {
        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        // Title
        Label titleLabel = new Label("GAME OVER", game.getSkin(), "title");
        titleLabel.setColor(Color.RED);
        table.add(titleLabel).padBottom(30).row();

        // Subtitle with wave reached
        Label waveLabel = new Label("You reached Wave " + (stats.getWavesCompleted() + 1), game.getSkin());
        waveLabel.setColor(Color.GOLD);
        table.add(waveLabel).padBottom(20).row();

        // Stats table
        Table statsTable = new Table();
        statsTable.defaults().padBottom(10).padRight(20).padLeft(20);

        addStatRow(statsTable, "Final Score:", String.valueOf(stats.getScore()), Color.WHITE);
        addStatRow(statsTable, "Waves Completed:", String.valueOf(stats.getWavesCompleted()), Color.CYAN);
        addStatRow(statsTable, "Total Time:", stats.getFormattedTime(), Color.YELLOW);
        addStatRow(statsTable, "Enemies Defeated:", String.valueOf(stats.getEnemiesDefeated()), Color.ORANGE);
        addStatRow(statsTable, "Hearts Collected:", String.valueOf(stats.getHeartsCollected()), Color.PINK);
        addStatRow(statsTable, "Coins Collected:", String.valueOf(stats.getCoinsCollected()), Color.GOLD);
        addStatRow(statsTable, "Damage Taken:", String.valueOf(stats.getTotalDamageTaken()), Color.RED);

        table.add(statsTable).padBottom(30).row();

        // Performance rating
        String rating = calculateRating();
        Label ratingLabel = new Label("Rating: " + rating, game.getSkin());
        ratingLabel.setColor(getRatingColor(rating));
        table.add(ratingLabel).padBottom(30).row();

        // Buttons
        Table buttonTable = new Table();
        buttonTable.defaults().padRight(20);

        TextButton retryButton = new TextButton("Try Again", game.getSkin());
        retryButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new EndlessGameScreen(game));
            }
        });
        buttonTable.add(retryButton).width(150).height(50);

        TextButton menuButton = new TextButton("Main Menu", game.getSkin());
        menuButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.goToMenu();
            }
        });
        buttonTable.add(menuButton).width(150).height(50);

        table.add(buttonTable).row();
    }

    /**
     * Adds a stat row to the stats table.
     */
    private void addStatRow(Table table, String label, String value, Color valueColor) {
        Label labelL = new Label(label, game.getSkin());
        labelL.setColor(Color.LIGHT_GRAY);

        Label valueL = new Label(value, game.getSkin());
        valueL.setColor(valueColor);

        table.add(labelL).left();
        table.add(valueL).right().row();
    }

    /**
     * Calculates a performance rating based on stats.
     */
    private String calculateRating() {
        int score = stats.getScore();
        int waves = stats.getWavesCompleted();

        if (waves >= 20 || score >= 50000) return "S";
        if (waves >= 15 || score >= 30000) return "A";
        if (waves >= 10 || score >= 15000) return "B";
        if (waves >= 5 || score >= 5000) return "C";
        if (waves >= 3 || score >= 2000) return "D";
        return "F";
    }

    /**
     * Gets the color for a rating.
     */
    private Color getRatingColor(String rating) {
        switch (rating) {
            case "S": return Color.GOLD;
            case "A": return Color.GREEN;
            case "B": return Color.CYAN;
            case "C": return Color.YELLOW;
            case "D": return Color.ORANGE;
            default: return Color.RED;
        }
    }

    @Override
    protected void draw(float delta) {
        ScreenUtils.clear(0.1f, 0.05f, 0.1f, 1);
        stage.draw();
    }

    @Override
    public void show() {
        super.show();
        Gdx.input.setInputProcessor(stage);
    }
}
