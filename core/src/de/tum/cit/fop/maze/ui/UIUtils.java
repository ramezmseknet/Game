package de.tum.cit.fop.maze.ui;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

/**
 * Utility class for common UI operations.
 * Provides consistent button styling and text scaling across all menus.
 */
public class UIUtils {
    
    private static final GlyphLayout glyphLayout = new GlyphLayout();
    
    /**
     * Default button width used across menus.
     */
    public static final float DEFAULT_BUTTON_WIDTH = 300f;
    
    /**
     * Default button height used across menus.
     */
    public static final float DEFAULT_BUTTON_HEIGHT = 60f;
    
    /**
     * Padding inside buttons for text.
     */
    public static final float BUTTON_PADDING = 20f;
    
    /**
     * Creates a TextButton with auto-scaled text to fit within the button width.
     * 
     * @param text The button label text
     * @param skin The UI skin to use
     * @param width The desired button width
     * @return A TextButton with appropriately scaled text
     */
    public static TextButton createFittedButton(String text, Skin skin, float width) {
        TextButton button = new TextButton(text, skin);
        fitTextToButton(button, width);
        return button;
    }
    
    /**
     * Creates a TextButton with auto-scaled text using default width.
     * 
     * @param text The button label text
     * @param skin The UI skin to use
     * @return A TextButton with appropriately scaled text
     */
    public static TextButton createFittedButton(String text, Skin skin) {
        return createFittedButton(text, skin, DEFAULT_BUTTON_WIDTH);
    }
    
    /**
     * Adjusts an existing TextButton's label scale to fit within a specified width.
     * 
     * @param button The TextButton to adjust
     * @param width The target width to fit text within
     */
    public static void fitTextToButton(TextButton button, float width) {
        Label label = button.getLabel();
        BitmapFont font = label.getStyle().font;
        String text = button.getText().toString();
        
        // Calculate available width for text (accounting for padding)
        float availableWidth = width - BUTTON_PADDING * 2;
        
        // Measure text at scale 1.0
        glyphLayout.setText(font, text);
        float textWidth = glyphLayout.width;
        
        // Calculate the scale needed to fit
        if (textWidth > availableWidth && textWidth > 0) {
            float scale = availableWidth / textWidth;
            // Clamp to reasonable minimum scale
            scale = Math.max(0.5f, scale);
            label.setFontScale(scale);
        } else {
            // Reset to default scale if text fits
            label.setFontScale(1.0f);
        }
    }
    
    /**
     * Adjusts an existing TextButton's label scale using a specified maximum scale.
     * Useful for buttons that should have smaller text by default.
     * 
     * @param button The TextButton to adjust
     * @param width The target width to fit text within
     * @param maxScale The maximum scale to use (e.g., 0.8f for smaller text)
     */
    public static void fitTextToButton(TextButton button, float width, float maxScale) {
        Label label = button.getLabel();
        BitmapFont font = label.getStyle().font;
        String text = button.getText().toString();
        
        // Calculate available width for text (accounting for padding)
        float availableWidth = width - BUTTON_PADDING * 2;
        
        // Measure text at scale 1.0
        glyphLayout.setText(font, text);
        float textWidth = glyphLayout.width;
        
        // Calculate the scale needed to fit
        float scale = maxScale;
        if (textWidth * maxScale > availableWidth && textWidth > 0) {
            scale = availableWidth / textWidth;
            // Clamp to reasonable minimum scale
            scale = Math.max(0.5f, scale);
        }
        label.setFontScale(scale);
    }
    
    /**
     * Sets a consistent font scale on a TextButton's label.
     * 
     * @param button The TextButton to adjust
     * @param scale The font scale to apply
     */
    public static void setButtonFontScale(TextButton button, float scale) {
        button.getLabel().setFontScale(scale);
    }
}
