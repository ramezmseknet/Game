package de.tum.cit.fop.maze.save;

/**
 * Interface for save/load functionality.
 */
public interface SaveSystem {
    
    /**
     * Saves game state to a named slot.
     * @param state The game state to save
     * @param slotName The name of the save slot
     */
    void save(GameState state, String slotName);
    
    /**
     * Loads game state from a named slot.
     * @param slotName The name of the save slot
     * @return The loaded game state, or null if not found
     */
    GameState load(String slotName);
    
    /**
     * Performs an auto-save.
     * @param state The game state to save
     */
    void autoSave(GameState state);
    
    /**
     * Loads the auto-save.
     * @return The auto-saved game state, or null if not found
     */
    GameState loadAutoSave();
    
    /**
     * Checks if a save exists.
     * @param slotName The name of the save slot
     * @return true if the save exists
     */
    boolean hasSave(String slotName);
    
    /**
     * Deletes a save file.
     * @param slotName The name of the save slot to delete
     */
    void deleteSave(String slotName);
}
