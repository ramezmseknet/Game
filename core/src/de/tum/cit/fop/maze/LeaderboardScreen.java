package de.tum.cit.fop.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import de.tum.cit.fop.maze.ui.UISoundHelper;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Screen for displaying local high scores and leaderboards.
 * Shows scores for different game modes (endless, time attack).
 */
public class LeaderboardScreen implements Screen {

    private final MazeRunnerGame game;
    private final Stage stage;
    private final Skin skin;
    private final OrthographicCamera camera;
    private LeaderboardData leaderboardData = new LeaderboardData();
    private String currentLevelFilter = "all";
    private Table scoresTable;

    public LeaderboardScreen(MazeRunnerGame game) {
        this.game = game;
        this.skin = game.getSkin();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        stage = new Stage(new ScreenViewport());

        loadLeaderboard();
        createUI();
    }


    private void loadLeaderboard() {
        try {
            FileHandle file = Gdx.files.local("leaderboard.json");
            if (file.exists()) {
                Json json = new Json();
                leaderboardData = json.fromJson(LeaderboardData.class, file);
            } else {
                leaderboardData = new LeaderboardData();
            }
        } catch (Exception e) {
            Gdx.app.error("LeaderboardScreen", "Failed to load leaderboard: " + e.getMessage());
            leaderboardData = new LeaderboardData();
        }
    }


    public static void saveScore(LeaderboardEntry entry) {
        try {
            FileHandle file = Gdx.files.local("leaderboard.json");
            LeaderboardData data;

            if (file.exists()) {
                Json json = new Json();
                data = json.fromJson(LeaderboardData.class, file);
            } else {
                data = new LeaderboardData();
            }

            data.entries.add(entry);

            data.entries.sort((a, b) -> Integer.compare(b.score, a.score));

            while (data.entries.size > 100) {
                data.entries.removeIndex(data.entries.size - 1);
            }

            Json json = new Json();
            json.setOutputType(JsonWriter.OutputType.json);
            file.writeString(json.prettyPrint(data), false);

        } catch (Exception e) {
            Gdx.app.error("LeaderboardScreen", "Failed to save score: " + e.getMessage());
        }
    }


    private void createUI() {
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.pad(20);
        stage.addActor(mainTable);

        Label titleLabel = new Label("Leaderboard", skin, "title");
        mainTable.add(titleLabel).padBottom(30).row();

        Table filterTable = new Table();

        TextButton allButton = new TextButton("All", skin);
        UISoundHelper.addClickSound(allButton);
        allButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                currentLevelFilter = "all";
                refreshScores();
            }
        });
        filterTable.add(allButton).padRight(10);

        TextButton endlessButton = new TextButton("Endless", skin);
        UISoundHelper.addClickSound(endlessButton);
        endlessButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                currentLevelFilter = "endless";
                refreshScores();
            }
        });
        filterTable.add(endlessButton).padRight(10);

        TextButton campaignButton = new TextButton("Campaign", skin);
        UISoundHelper.addClickSound(campaignButton);
        campaignButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                currentLevelFilter = "campaign";
                refreshScores();
            }
        });
        filterTable.add(campaignButton);

        mainTable.add(filterTable).padBottom(20).row();

        Table headerTable = new Table();
        headerTable.defaults().pad(5, 15, 5, 15).align(Align.left);
        headerTable.add(new Label("Rank", skin)).width(80);
        headerTable.add(new Label("Player", skin)).width(180);
        headerTable.add(new Label("Score", skin)).width(120);
        headerTable.add(new Label("Time", skin)).width(100);
        headerTable.add(new Label("Level", skin)).width(120);
        headerTable.add(new Label("Date", skin)).width(140);
        mainTable.add(headerTable).fillX().padBottom(10).row();

        scoresTable = new Table();
        ScrollPane scrollPane = new ScrollPane(scoresTable, skin);
        scrollPane.setFadeScrollBars(false);
        mainTable.add(scrollPane).expand().fill().padBottom(20).row();

        TextButton backButton = new TextButton("Back to Menu", skin);
        UISoundHelper.addClickSound(backButton);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.goToMenu();
            }
        });
        backButton.pad(10, 30, 10, 30);
        mainTable.add(backButton).minWidth(250).minHeight(60);

        refreshScores();
    }


    private void refreshScores() {
        scoresTable.clear();

        if (leaderboardData.entries.size == 0) {
            scoresTable.add(new Label("No scores yet! Play a game to get on the board.", skin))
                    .pad(20);
            return;
        }

        int rank = 1;
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

        for (LeaderboardEntry entry : leaderboardData.entries) {
            if (!currentLevelFilter.equals("all")) {
                if (currentLevelFilter.equals("endless") && !entry.levelId.contains("endless"))
                    continue;
                if (currentLevelFilter.equals("campaign") && entry.levelId.contains("endless"))
                    continue;
            }

            Table rowTable = new Table();
            rowTable.defaults().pad(5, 15, 5, 15).align(Align.left);

            // Highlight top 3
            Color rowColor = Color.WHITE;
            if (rank == 1)
                rowColor = new Color(1f, 0.84f, 0f, 1f); // Gold
            else if (rank == 2)
                rowColor = new Color(0.75f, 0.75f, 0.75f, 1f); // Silver
            else if (rank == 3)
                rowColor = new Color(0.8f, 0.5f, 0.2f, 1f); // Bronze

            Label rankLabel = new Label(String.valueOf(rank), skin);
            rankLabel.setColor(rowColor);
            rowTable.add(rankLabel).width(80);

            Label nameLabel = new Label(entry.playerName, skin);
            nameLabel.setColor(rowColor);
            rowTable.add(nameLabel).width(180);

            Label scoreLabel = new Label(String.valueOf(entry.score), skin);
            scoreLabel.setColor(rowColor);
            rowTable.add(scoreLabel).width(120);

            String timeStr = formatTime(entry.completionTime);
            Label timeLabel = new Label(timeStr, skin);
            timeLabel.setColor(rowColor);
            rowTable.add(timeLabel).width(100);

            Label levelLabel = new Label(formatLevelId(entry.levelId), skin);
            levelLabel.setColor(rowColor);
            rowTable.add(levelLabel).width(120);

            String dateStr = dateFormat.format(new Date(entry.timestamp));
            Label dateLabel = new Label(dateStr, skin);
            dateLabel.setColor(rowColor);
            rowTable.add(dateLabel).width(140);

            scoresTable.add(rowTable).padBottom(5).row();
            rank++;

            if (rank > 50)
                break;
        }
    }


    private String formatTime(float seconds) {
        int mins = (int) (seconds / 60);
        int secs = (int) (seconds % 60);
        return String.format("%02d:%02d", mins, secs);
    }


    private String formatLevelId(String levelId) {
        if (levelId.contains("endless"))
            return "Endless";
        if (levelId.contains("level-")) {
            return "Level " + levelId.replace("maps/level-", "").replace(".properties", "");
        }
        return levelId;
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        stage.act(Math.min(delta, 1 / 30f));
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
    }

    @Override
    public void dispose() {
        stage.dispose();
    }


    public static class LeaderboardEntry {
        public String playerName;
        public int score;
        public String levelId;
        public float completionTime;
        public long timestamp;

        public LeaderboardEntry() {
        }

        public LeaderboardEntry(String playerName, int score, String levelId, float completionTime) {
            this.playerName = playerName;
            this.score = score;
            this.levelId = levelId;
            this.completionTime = completionTime;
            this.timestamp = System.currentTimeMillis();
        }
    }


    public static class LeaderboardData {
        public Array<LeaderboardEntry> entries = new Array<>();
    }
}
