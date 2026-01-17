package de.tum.cit.fop.maze.ui;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Array;

/**
 * Small helper to install vertical arrow-key navigation for a list of buttons.
 */
public final class UIInputHelper {

    private UIInputHelper() {
    }

    public static void installVerticalNavigation(final Stage stage, final Array<TextButton> buttons) {
        if (buttons == null || buttons.size == 0)
            return;

        // ensure the stage has initial keyboard focus on the first button
        if (stage.getKeyboardFocus() == null) {
            stage.setKeyboardFocus(buttons.first());
        }

        // helper to update visual state for focused button using enter/exit events
        final java.util.function.Consumer<com.badlogic.gdx.scenes.scene2d.Actor> updateVisuals = (focusActor) -> {
            for (int i = 0; i < buttons.size; i++) {
                com.badlogic.gdx.scenes.scene2d.ui.TextButton b = buttons.get(i);
                if (b == focusActor) {
                    // Fire enter event to trigger "over" state (blue highlight)
                    InputEvent enterEvent = new InputEvent();
                    enterEvent.setType(InputEvent.Type.enter);
                    enterEvent.setPointer(-1);
                    b.fire(enterEvent);
                } else {
                    // Fire exit event to clear "over" state
                    InputEvent exitEvent = new InputEvent();
                    exitEvent.setType(InputEvent.Type.exit);
                    exitEvent.setPointer(-1);
                    b.fire(exitEvent);
                }
            }
        };

        // apply initial visuals
        updateVisuals.accept(stage.getKeyboardFocus());

        stage.addListener(new com.badlogic.gdx.scenes.scene2d.InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                com.badlogic.gdx.scenes.scene2d.Actor focusActor = stage.getKeyboardFocus();
                int idx = -1;
                if (focusActor != null) {
                    for (int i = 0; i < buttons.size; i++) {
                        if (buttons.get(i) == focusActor) {
                            idx = i;
                            break;
                        }
                    }
                }

                if (keycode == Input.Keys.UP) {
                    if (idx == -1) {
                        stage.setKeyboardFocus(buttons.first());
                        updateVisuals.accept(buttons.first());
                    } else {
                        int prev = (idx - 1 + buttons.size) % buttons.size;
                        stage.setKeyboardFocus(buttons.get(prev));
                        updateVisuals.accept(buttons.get(prev));
                    }
                    return true;
                }

                if (keycode == Input.Keys.DOWN) {
                    if (idx == -1) {
                        stage.setKeyboardFocus(buttons.first());
                        updateVisuals.accept(buttons.first());
                    } else {
                        int next = (idx + 1) % buttons.size;
                        stage.setKeyboardFocus(buttons.get(next));
                        updateVisuals.accept(buttons.get(next));
                    }
                    return true;
                }

                if (keycode == Input.Keys.ENTER || keycode == Input.Keys.SPACE) {
                    TextButton target = null;
                    if (focusActor instanceof TextButton) {
                        target = (TextButton) focusActor;
                    }
                    if (target == null) {
                        target = buttons.first();
                        stage.setKeyboardFocus(target);
                        updateVisuals.accept(target);
                    }
                    // simulate click: fire touchDown and touchUp events
                    InputEvent down = new InputEvent();
                    down.setType(InputEvent.Type.touchDown);
                    down.setPointer(0);
                    down.setButton(Buttons.LEFT);
                    target.fire(down);

                    InputEvent up = new InputEvent();
                    up.setType(InputEvent.Type.touchUp);
                    up.setPointer(0);
                    up.setButton(Buttons.LEFT);
                    target.fire(up);
                    return true;
                }

                return false;
            }
        });
    }
}
