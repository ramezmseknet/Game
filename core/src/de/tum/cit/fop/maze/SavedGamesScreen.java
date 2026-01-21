package de.tum.cit.fop.maze;

import com.badlogic.gdx.Gdx;
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
import de.tum.cit.fop.maze.save.GameState;
import de.tum.cit.fop.maze.save.JsonSaveSystem;
import de.tum.cit.fop.maze.ui.UISoundHelper;

/**
 * Screen displaying saved games, accessible from the main menu.
 */
public class SavedGamesScreen implements Screen {
    
    private final MazeRunnerGame game;
    private final Stage stage;
    private final Skin skin;
    private final OrthographicCamera camera;
    private final JsonSaveSystem saveSystem;
    
    private Table savesTable;
    
    public SavedGamesScreen(MazeRunnerGame game) {
        this.game = game;
        this.skin = game.getSkin();
        this.saveSystem = new JsonSaveSystem();
        
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        
        stage = new Stage(new ScreenViewport());
        
        createUI();
    }
    
    private void createUI() {
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.center();
        stage.addActor(mainTable);
        
        // Title
        Label titleLabel = new Label("Saved Games", skin);
        titleLabel.setFontScale(2.0f);
        mainTable.add(titleLabel).padBottom(40).row();
        
        // Saves list container
        savesTable = new Table();
        savesTable.defaults().pad(10);
        
        refreshSavesList();
        
        ScrollPane scrollPane = new ScrollPane(savesTable, skin);
        scrollPane.setFadeScrollBars(false);
        mainTable.add(scrollPane).expand().fill().pad(20, 40, 30, 40).row();
        
        // Buttons row
        Table buttonsTable = new Table();
        
        TextButton backButton = new TextButton("Back to Menu", skin);
        UISoundHelper.addClickSound(backButton);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.goToMenu();
            }
        });
        backButton.pad(10, 30, 10, 30);
        buttonsTable.add(backButton).minWidth(250).minHeight(60).pad(10);
        
        mainTable.add(buttonsTable);
    }
    
    private void refreshSavesList() {
        savesTable.clear();
        
        String[] slots = saveSystem.getSaveSlots();
        
        if (slots.length == 0) {
            Label noSavesLabel = new Label("No saved games found.", skin);
            savesTable.add(noSavesLabel).pad(20);
            return;
        }
        
        for (String slot : slots) {
            Table rowTable = new Table();
            
            Label nameLabel = new Label(slot, skin);
            nameLabel.setFontScale(1.1f);
            rowTable.add(nameLabel).expandX().left().padRight(40);
            
            TextButton loadButton = new TextButton("Load", skin);
            loadButton.pad(5, 12, 5, 12);
            UISoundHelper.addClickSound(loadButton);
            loadButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    loadGame(slot);
                }
            });
            rowTable.add(loadButton).padRight(15);
            
            TextButton deleteButton = new TextButton("Delete", skin);
            deleteButton.pad(5, 12, 5, 12);
            UISoundHelper.addClickSound(deleteButton);
            deleteButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    showDeleteConfirmation(slot);
                }
            });
            rowTable.add(deleteButton);
            
            savesTable.add(rowTable).fillX().expandX().row();
        }
    }
    
    private void loadGame(String slotName) {
        GameState state = saveSystem.load(slotName);
        if (state != null) {
            game.setScreen(new GameScreen(game, state.levelFile));
        } else {
            showMessage("Error", "Failed to load save file.");
        }
    }
    
    private void showDeleteConfirmation(String slotName) {
        Dialog dialog = new Dialog("Delete Save", skin) {
            @Override
            protected void result(Object object) {
                if ((Boolean) object) {
                    saveSystem.deleteSave(slotName);
                    refreshSavesList();
                }
            }
        };
        dialog.text("Are you sure you want to delete\n\"" + slotName + "\"?");
        dialog.button("Delete", true);
        dialog.button("Cancel", false);
        dialog.show(stage);
    }
    
    private void showMessage(String title, String message) {
        Dialog dialog = new Dialog(title, skin);
        dialog.text(message);
        dialog.button("OK");
        dialog.show(stage);
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
    public void pause() {}
    
    @Override
    public void resume() {}
    
    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }
    
    @Override
    public void dispose() {
        stage.dispose();
    }
}
