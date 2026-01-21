package de.tum.cit.fop.maze.input;

import com.badlogic.gdx.Gdx;

/**
 * Interface for managing input key bindings.
 * Allows remapping controls and persisting to JSON.
 */
public interface InputBindings {
    
    /**
     * Gets the key code bound to an action.
     * @param action The action to look up
     * @return The libGDX key code (from Input.Keys)
     */
    int getKey(Action action);
    
    /**
     * Sets a new key binding for an action.
     * @param action The action to rebind
     * @param keycode The new key code
     */
    void setKey(Action action, int keycode);
    
    /**
     * Resets all bindings to default values.
     */
    void resetToDefaults();
    
    /**
     * Saves current bindings to persistent storage.
     */
    void save();
    
    /**
     * Loads bindings from persistent storage.
     */
    void load();
    
    /**
     * Checks if the key for an action is currently pressed.
     * @param action The action to check
     * @return true if the bound key is pressed
     */
    default boolean isPressed(Action action) {
        return Gdx.input.isKeyPressed(getKey(action));
    }
    
    /**
     * Checks if the key for an action was just pressed this frame.
     * @param action The action to check
     * @return true if the bound key was just pressed
     */
    default boolean isJustPressed(Action action) {
        return Gdx.input.isKeyJustPressed(getKey(action));
    }
}
