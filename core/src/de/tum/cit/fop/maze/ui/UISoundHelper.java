package de.tum.cit.fop.maze.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import de.tum.cit.fop.maze.audio.AudioManager;

/**
 * Utility class for adding sound effects to UI elements.
 * Provides easy methods to wrap listeners with sound playback.
 */
public class UISoundHelper {
    
    private static final String DEFAULT_CLICK_SOUND = "ui_click";
    private static final String DEFAULT_HOVER_SOUND = "ui_hover";
    
    /**
     * Adds a click sound to a button.
     * The sound plays when the button is clicked.
     * 
     * @param button The button to add sound to
     */
    public static void addClickSound(Button button) {
        addClickSound(button, DEFAULT_CLICK_SOUND);
    }
    
    /**
     * Adds a custom click sound to a button.
     * Uses ClickListener touchDown for immediate sound feedback on button press.
     *
     * @param button The button to add sound to
     * @param soundName The name of the sound to play
     */
    public static void addClickSound(Button button, String soundName) {
        button.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int buttonCode) {
                playSound(soundName);
                return super.touchDown(event, x, y, pointer, buttonCode);
            }
        });
    }
    
    /**
     * Creates a ChangeListener that plays a sound before executing the action.
     * 
     * @param action The action to execute after playing the sound
     * @return A ChangeListener that plays sound and executes the action
     */
    public static ChangeListener withClickSound(Runnable action) {
        return withClickSound(DEFAULT_CLICK_SOUND, action);
    }
    
    /**
     * Creates a ChangeListener that plays a custom sound before executing the action.
     * 
     * @param soundName The name of the sound to play
     * @param action The action to execute after playing the sound
     * @return A ChangeListener that plays sound and executes the action
     */
    public static ChangeListener withClickSound(String soundName, Runnable action) {
        return new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                playSound(soundName);
                action.run();
            }
        };
    }
    
    /**
     * Adds hover and click sounds to a button.
     *
     * @param button The button to add sounds to
     */
    public static void addAllSounds(Button button) {
        button.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int buttonCode) {
                playSound(DEFAULT_CLICK_SOUND);
                return super.touchDown(event, x, y, pointer, buttonCode);
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                super.enter(event, x, y, pointer, fromActor);
                if (pointer == -1) { // -1 means mouse hover, not drag
                    playSound(DEFAULT_HOVER_SOUND);
                }
            }
        });
    }
    
    /**
     * Plays a sound effect by name.
     * 
     * @param soundName The name of the sound to play
     */
    public static void playSound(String soundName) {
        AudioManager.getInstance().playSound(soundName);
    }
    
    /**
     * Plays the default UI click sound.
     */
    public static void playClickSound() {
        playSound(DEFAULT_CLICK_SOUND);
    }
}
