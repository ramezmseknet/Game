package de.tum.cit.fop.maze.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;

import java.util.HashMap;
import java.util.Map;

/**
 * JSON-persistent input bindings implementation.
 * Supports saving and loading key bindings to/from a JSON file.
 */
public class JsonInputBindings implements InputBindings {

    private static final String CONFIG_FILE = "config/keybindings.json";

    private final Map<Action, Integer> bindings;
    private final Json json;

    public JsonInputBindings() {
        bindings = new HashMap<>();
        json = new Json();
        json.setOutputType(JsonWriter.OutputType.json);

        // Set defaults first
        resetToDefaults();

        // Try to load from file
        load();
    }

    @Override
    public void resetToDefaults() {
        bindings.clear();

        // Movement (WASD)
        bindings.put(Action.MOVE_UP, Keys.W);
        bindings.put(Action.MOVE_DOWN, Keys.S);
        bindings.put(Action.MOVE_LEFT, Keys.A);
        bindings.put(Action.MOVE_RIGHT, Keys.D);

        // Other actions
        bindings.put(Action.RUN, Keys.SHIFT_LEFT);
        bindings.put(Action.PAUSE, Keys.ESCAPE);
        bindings.put(Action.INTERACT, Keys.F);
        bindings.put(Action.ZOOM_IN, Keys.PLUS);
        bindings.put(Action.ZOOM_OUT, Keys.MINUS);
        bindings.put(Action.CONSOLE, Keys.GRAVE);
        bindings.put(Action.ATTACK, Keys.SPACE);
    }

    @Override
    public int getKey(Action action) {
        return bindings.getOrDefault(action, Keys.UNKNOWN);
    }

    @Override
    public void setKey(Action action, int keycode) {
        bindings.put(action, keycode);
    }

    /**
     * Gets the primary key for an action (alias for getKey).
     */
    public int getPrimaryKey(Action action) {
        return getKey(action);
    }

    /**
     * Sets the primary key for an action (alias for setKey).
     */
    public void setPrimaryKey(Action action, int keycode) {
        setKey(action, keycode);
    }

    @Override
    public void save() {
        try {
            BindingsData data = new BindingsData();

            // Convert Action enum to String keys for JSON
            for (Map.Entry<Action, Integer> entry : bindings.entrySet()) {
                data.bindings.put(entry.getKey().name(), entry.getValue());
            }

            FileHandle file = Gdx.files.local(CONFIG_FILE);
            file.parent().mkdirs();
            file.writeString(json.prettyPrint(data), false);

            Gdx.app.log("InputBindings", "Key bindings saved");
        } catch (Exception e) {
            Gdx.app.error("InputBindings", "Failed to save key bindings: " + e.getMessage());
        }
    }

    @Override
    public void load() {
        FileHandle file = Gdx.files.local(CONFIG_FILE);
        if (!file.exists()) {
            Gdx.app.log("InputBindings", "No saved key bindings found, using defaults");
            return;
        }

        try {
            String jsonString = file.readString();
            BindingsData data = json.fromJson(BindingsData.class, jsonString);

            if (data != null && data.bindings != null) {
                for (Map.Entry<String, Integer> entry : data.bindings.entrySet()) {
                    try {
                        Action action = Action.valueOf(entry.getKey());
                        bindings.put(action, entry.getValue());
                    } catch (IllegalArgumentException e) {
                        // Unknown action, skip
                    }
                }
                Gdx.app.log("InputBindings", "Key bindings loaded");
            }
        } catch (Exception e) {
            Gdx.app.error("InputBindings", "Failed to load key bindings: " + e.getMessage());
        }
    }

    /**
     * Checks if movement in any direction is pressed.
     * Also checks arrow keys as secondary bindings.
     * @param action The movement action
     * @return true if the primary or secondary key is pressed
     */
    @Override
    public boolean isPressed(Action action) {
        boolean primary = InputBindings.super.isPressed(action);

        // Check arrow keys as secondary bindings for movement
        if (!primary) {
            switch (action) {
                case MOVE_UP:
                    return com.badlogic.gdx.Gdx.input.isKeyPressed(Keys.UP);
                case MOVE_DOWN:
                    return com.badlogic.gdx.Gdx.input.isKeyPressed(Keys.DOWN);
                case MOVE_LEFT:
                    return com.badlogic.gdx.Gdx.input.isKeyPressed(Keys.LEFT);
                case MOVE_RIGHT:
                    return com.badlogic.gdx.Gdx.input.isKeyPressed(Keys.RIGHT);
                case RUN:
                    return com.badlogic.gdx.Gdx.input.isKeyPressed(Keys.SHIFT_RIGHT);
                default:
                    return false;
            }
        }
        return true;
    }

    /**
     * Gets the name of a key from its keycode.
     */
    public static String getKeyName(int keycode) {
        if (keycode == Keys.UNKNOWN) {
            return "None";
        }
        return Keys.toString(keycode);
    }

    /**
     * Data class for JSON serialization.
     */
    public static class BindingsData {
        public Map<String, Integer> bindings = new HashMap<>();
    }
}
