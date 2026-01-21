package de.tum.cit.fop.maze.persistence;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;

import java.util.ArrayList;
import java.util.List;

/**
 * JSON-based implementation of the save system.
 * Stores game state in human-readable JSON files.
 */
public class JsonSaveSystem implements SaveSystem {
    
    private static final String SAVE_DIRECTORY = "saves/";
    private static final String SAVE_EXTENSION = ".json";
    private static final String AUTO_SAVE_NAME = "autosave";
    
    private final Json json;
    
    public JsonSaveSystem() {
        json = new Json();
        json.setOutputType(JsonWriter.OutputType.json);
        
        // Ensure save directory exists
        FileHandle saveDir = Gdx.files.local(SAVE_DIRECTORY);
        if (!saveDir.exists()) {
            saveDir.mkdirs();
        }
    }
    
    @Override
    public boolean save(GameState state, String slotName) {
        try {
            String jsonString = json.prettyPrint(state);
            FileHandle file = getSaveFile(slotName);
            file.writeString(jsonString, false);
            Gdx.app.log("SaveSystem", "Game saved to slot: " + slotName);
            return true;
        } catch (Exception e) {
            Gdx.app.error("SaveSystem", "Failed to save game: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public GameState load(String slotName) {
        FileHandle file = getSaveFile(slotName);
        if (!file.exists()) {
            Gdx.app.log("SaveSystem", "No save file found: " + slotName);
            return null;
        }
        
        try {
            String jsonString = file.readString();
            GameState state = json.fromJson(GameState.class, jsonString);
            Gdx.app.log("SaveSystem", "Game loaded from slot: " + slotName);
            return state;
        } catch (Exception e) {
            Gdx.app.error("SaveSystem", "Failed to load game: " + e.getMessage());
            return null;
        }
    }
    
    @Override
    public List<String> listSaveSlots() {
        List<String> slots = new ArrayList<>();
        FileHandle saveDir = Gdx.files.local(SAVE_DIRECTORY);
        
        if (saveDir.exists()) {
            for (FileHandle file : saveDir.list()) {
                if (file.extension().equals("json") && !file.nameWithoutExtension().equals(AUTO_SAVE_NAME)) {
                    slots.add(file.nameWithoutExtension());
                }
            }
        }
        
        return slots;
    }
    
    @Override
    public boolean deleteSave(String slotName) {
        FileHandle file = getSaveFile(slotName);
        if (file.exists()) {
            file.delete();
            Gdx.app.log("SaveSystem", "Save slot deleted: " + slotName);
            return true;
        }
        return false;
    }
    
    @Override
    public boolean saveExists(String slotName) {
        return getSaveFile(slotName).exists();
    }
    
    @Override
    public void autoSave(GameState state) {
        save(state, AUTO_SAVE_NAME);
    }
    
    @Override
    public GameState loadAutoSave() {
        return load(AUTO_SAVE_NAME);
    }
    
    /**
     * Gets the file handle for a save slot.
     */
    private FileHandle getSaveFile(String slotName) {
        return Gdx.files.local(SAVE_DIRECTORY + slotName + SAVE_EXTENSION);
    }
    
    /**
     * Gets metadata for a save slot without loading full state.
     */
    public SaveMetadata getMetadata(String slotName) {
        GameState state = load(slotName);
        if (state != null) {
            return new SaveMetadata(
                slotName,
                state.getLevelFile(),
                state.getElapsedTime(),
                state.getSaveTimestamp()
            );
        }
        return null;
    }
    
    /**
     * Metadata about a save file.
     */
    public static class SaveMetadata {
        public final String slotName;
        public final String levelFile;
        public final float playTime;
        public final long timestamp;
        
        public SaveMetadata(String slotName, String levelFile, float playTime, long timestamp) {
            this.slotName = slotName;
            this.levelFile = levelFile;
            this.playTime = playTime;
            this.timestamp = timestamp;
        }
        
        public String getFormattedPlayTime() {
            int totalSeconds = (int) playTime;
            int hours = totalSeconds / 3600;
            int minutes = (totalSeconds % 3600) / 60;
            int seconds = totalSeconds % 60;
            
            if (hours > 0) {
                return String.format("%d:%02d:%02d", hours, minutes, seconds);
            }
            return String.format("%d:%02d", minutes, seconds);
        }
    }
}
