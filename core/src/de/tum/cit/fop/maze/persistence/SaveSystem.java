package de.tum.cit.fop.maze.persistence;

import java.util.List;

/**
 * Interface for saving and loading game state.
 */
public interface SaveSystem {
    
    /**
     * Saves the current game state to a slot.
     * @param state The game state to save
     * @param slotName Name of the save slot
     * @return true if save was successful
     */
    boolean save(GameState state, String slotName);
    
    /**
     * Loads a game state from a slot.
     * @param slotName Name of the save slot to load
     * @return The loaded game state, or null if not found
     */
    GameState load(String slotName);
    
    /**
     * Gets a list of all save slots.
     * @return List of save slot names
     */
    List<String> listSaveSlots();
    
    /**
     * Deletes a save slot.
     * @param slotName Name of the slot to delete
     * @return true if deletion was successful
     */
    boolean deleteSave(String slotName);
    
    /**
     * Checks if a save slot exists.
     * @param slotName Name of the slot to check
     * @return true if the slot exists
     */
    boolean saveExists(String slotName);
    
    /**
     * Creates an auto-save of the current state.
     * @param state The game state to auto-save
     */
    void autoSave(GameState state);
    
    /**
     * Loads the most recent auto-save.
     * @return The auto-saved state, or null if none exists
     */
    GameState loadAutoSave();
}
