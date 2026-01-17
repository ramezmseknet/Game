package de.tum.cit.fop.maze.save;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;

import java.util.ArrayList;
import java.util.List;

/**
 * JSON-based implementation of the save system.
 */
public class JsonSaveSystem implements SaveSystem {
    
    private static final String SAVES_DIR = "saves/";
    private static final String SAVE_EXTENSION = ".json";
    private static final String AUTOSAVE_SLOT = "_autosave";
    
    private final Json json;
    
    public JsonSaveSystem() {
        json = new Json();
        json.setOutputType(JsonWriter.OutputType.json);
        
        // Ensure saves directory exists
        FileHandle dir = Gdx.files.local(SAVES_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }
    
    @Override
    public void save(GameState state, String slotName) {
        try {
            state.saveName = slotName;
            state.saveTime = System.currentTimeMillis();
            
            FileHandle file = Gdx.files.local(SAVES_DIR + slotName + SAVE_EXTENSION);
            file.writeString(json.prettyPrint(state), false);
            
            Gdx.app.log("SaveSystem", "Game saved to: " + file.path());
        } catch (Exception e) {
            Gdx.app.error("SaveSystem", "Failed to save: " + e.getMessage());
        }
    }
    
    @Override
    public GameState load(String slotName) {
        FileHandle file = Gdx.files.local(SAVES_DIR + slotName + SAVE_EXTENSION);
        
        if (!file.exists()) {
            Gdx.app.log("SaveSystem", "Save file not found: " + slotName);
            return null;
        }
        
        try {
            String jsonString = file.readString();
            GameState state = json.fromJson(GameState.class, jsonString);
            Gdx.app.log("SaveSystem", "Game loaded from: " + slotName);
            return state;
        } catch (Exception e) {
            Gdx.app.error("SaveSystem", "Failed to load: " + e.getMessage());
            return null;
        }
    }
    
    @Override
    public void autoSave(GameState state) {
        save(state, AUTOSAVE_SLOT);
    }
    
    @Override
    public GameState loadAutoSave() {
        return load(AUTOSAVE_SLOT);
    }
    
    @Override
    public boolean hasSave(String slotName) {
        return Gdx.files.local(SAVES_DIR + slotName + SAVE_EXTENSION).exists();
    }
    
    @Override
    public void deleteSave(String slotName) {
        FileHandle file = Gdx.files.local(SAVES_DIR + slotName + SAVE_EXTENSION);
        if (file.exists()) {
            file.delete();
            Gdx.app.log("SaveSystem", "Deleted save: " + slotName);
        }
    }
    
    /**
     * Gets all available save slot names.
     * @return Array of slot names
     */
    public String[] getSaveSlots() {
        FileHandle dir = Gdx.files.local(SAVES_DIR);
        if (!dir.exists() || !dir.isDirectory()) {
            return new String[0];
        }
        
        List<String> slots = new ArrayList<>();
        for (FileHandle file : dir.list(SAVE_EXTENSION)) {
            String name = file.nameWithoutExtension();
            if (!name.equals(AUTOSAVE_SLOT)) {
                slots.add(name);
            }
        }
        
        return slots.toArray(new String[0]);
    }
}
