package de.tum.cit.fop.maze.console;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Developer console for debug commands and cheats.
 * Toggle with grave/tilde key (`).
 *
 * Commands:
 * - god: Toggle god mode (invincibility)
 * - noclip: Toggle noclip (move through walls)
 * - speed [value]: Set game speed multiplier
 * - tp [x] [y]: Teleport to tile position
 * - give lives [amount]: Give lives
 * - skip: Skip current level
 * - spawn [enemy_type] [x] [y]: Spawn enemy
 * - status: Show game status
 * - help: Show available commands
 * - clear: Clear console output
 */
public class DevConsole {

    private static final int MAX_HISTORY = 50;
    private static final int MAX_OUTPUT = 20;
    private static final float BACKGROUND_ALPHA = 0.8f;
    private static final float PADDING = 10f;
    private static final float LINE_HEIGHT = 20f;

    private final BitmapFont font;
    private final ShapeRenderer shapeRenderer;
    private final Array<String> commandHistory;
    private final Array<String> output;
    private final Map<String, BiConsumer<DevConsole, String[]>> commands;

    private StringBuilder inputBuffer;
    private int historyIndex;
    private boolean visible;
    private ConsoleContext context;
    private float cursorBlinkTimer;
    private boolean cursorVisible;

    /**
     * Creates a new developer console.
     * @param font Font to use for text rendering
     */
    public DevConsole(BitmapFont font) {
        this.font = font;
        this.shapeRenderer = new ShapeRenderer();
        this.commandHistory = new Array<>();
        this.output = new Array<>();
        this.commands = new HashMap<>();

        this.inputBuffer = new StringBuilder();
        this.historyIndex = -1;
        this.visible = false;
        this.cursorBlinkTimer = 0f;
        this.cursorVisible = true;

        registerCommands();

        println("Developer Console initialized. Type 'help' for commands.");
    }

    /**
     * Registers all available console commands.
     */
    private void registerCommands() {
        // God mode
        commands.put("god", (console, args) -> {
            if (context != null) {
                boolean enabled = context.toggleGodMode();
                println("God mode: " + (enabled ? "ON" : "OFF"));
            }
        });

        // Noclip mode
        commands.put("noclip", (console, args) -> {
            if (context != null) {
                boolean enabled = context.toggleNoclip();
                println("Noclip mode: " + (enabled ? "ON" : "OFF"));
            }
        });

        // Speed multiplier
        commands.put("speed", (console, args) -> {
            if (args.length < 1) {
                println("Usage: speed [value] (e.g., speed 2.0)");
                return;
            }
            try {
                float speed = Float.parseFloat(args[0]);
                if (context != null) {
                    context.setSpeedMultiplier(speed);
                    println("Speed set to: " + speed + "x");
                }
            } catch (NumberFormatException e) {
                println("Invalid speed value: " + args[0]);
            }
        });

        // Teleport
        commands.put("tp", (console, args) -> {
            if (args.length < 2) {
                println("Usage: tp [x] [y] (tile coordinates)");
                return;
            }
            try {
                int x = Integer.parseInt(args[0]);
                int y = Integer.parseInt(args[1]);
                if (context != null) {
                    context.teleportPlayer(x, y);
                    println("Teleported to (" + x + ", " + y + ")");
                }
            } catch (NumberFormatException e) {
                println("Invalid coordinates");
            }
        });

        // Give command (lives)
        commands.put("give", (console, args) -> {
            if (args.length < 2) {
                println("Usage: give lives [amount]");
                return;
            }
            if (args[0].equalsIgnoreCase("lives")) {
                try {
                    int amount = Integer.parseInt(args[1]);
                    if (context != null && context.getPlayer() != null) {
                        context.getPlayer().setLives(context.getPlayer().getLives() + amount);
                        println("Gave " + amount + " lives");
                    }
                } catch (NumberFormatException e) {
                    println("Invalid amount: " + args[1]);
                }
            } else {
                println("Unknown give type: " + args[0]);
            }
        });

        // Skip level / complete level
        commands.put("skip", (console, args) -> {
            if (context != null) {
                context.completeLevel();
                println("Completing level...");
            }
        });

        // Kill all enemies
        commands.put("kill", (console, args) -> {
            if (args.length > 0 && args[0].equalsIgnoreCase("all")) {
                if (context != null) {
                    int killed = context.killAllEnemies();
                    println("Killed " + killed + " enemies");
                }
            } else {
                println("Usage: kill all");
            }
        });

        // Spawn entity
        commands.put("spawn", (console, args) -> {
            if (args.length < 3) {
                println("Usage: spawn [type] [x] [y]");
                println("Types: patrol, chaser, lever, heart, trap");
                return;
            }
            try {
                String type = args[0];
                int x = Integer.parseInt(args[1]);
                int y = Integer.parseInt(args[2]);
                if (context != null) {
                    context.spawnEntity(type, x, y);
                    println("Spawned " + type + " at (" + x + ", " + y + ")");
                }
            } catch (NumberFormatException e) {
                println("Invalid coordinates");
            }
        });

        // Load level
        commands.put("level", (console, args) -> {
            if (args.length < 1) {
                println("Usage: level [path] (e.g., level maps/level-1.properties)");
                return;
            }
            if (context != null) {
                context.loadLevel(args[0]);
                println("Loading level: " + args[0]);
            }
        });

        // Status info
        commands.put("status", (console, args) -> {
            if (context != null && context.getPlayer() != null) {
                de.tum.cit.fop.maze.entity.Player p = context.getPlayer();
                println("=== Player Status ===");
                println("Position: (" + p.getTileX() + ", " + p.getTileY() + ")");
                println("Lives: " + p.getLives());
                println("Keys: " + p.getKeysCollected() + "/" + p.getKeysRequired());
                println("Score: " + p.getScore());
            }
        });

        // Help
        commands.put("help", (console, args) -> {
            println("=== Available Commands ===");
            println("god - Toggle invincibility");
            println("noclip - Toggle wall collision");
            println("speed [value] - Set game speed (1.0 = normal)");
            println("tp [x] [y] - Teleport to tile");
            println("give lives [amount] - Give extra lives");
            println("skip - Complete current level");
            println("kill all - Kill all enemies");
            println("spawn [type] [x] [y] - Spawn entity");
            println("level [path] - Load a level");
            println("status - Show player status");
            println("clear - Clear console");
            println("help - Show this help");
        });

        // Clear
        commands.put("clear", (console, args) -> {
            output.clear();
        });
    }

    /**
     * Sets the game context for command execution.
     * @param context The console context
     */
    public void setContext(ConsoleContext context) {
        this.context = context;
    }

    /**
     * Toggles console visibility.
     */
    public void toggle() {
        visible = !visible;
        if (visible) {
            inputBuffer.setLength(0);
            Gdx.input.setInputProcessor(null); // Capture all input
        }
    }

    /**
     * Checks if the console is visible.
     * @return true if visible
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * Handles input when console is active.
     */
    public void handleInput() {
        if (!visible) return;

        // Update cursor blink
        cursorBlinkTimer += Gdx.graphics.getDeltaTime();
        if (cursorBlinkTimer >= 0.5f) {
            cursorBlinkTimer = 0f;
            cursorVisible = !cursorVisible;
        }

        // Handle text input
        for (int i = 0; i < 256; i++) {
            if (Gdx.input.isKeyJustPressed(i)) {
                if (i == Input.Keys.ENTER) {
                    executeCommand(inputBuffer.toString().trim());
                    commandHistory.insert(0, inputBuffer.toString());
                    if (commandHistory.size > MAX_HISTORY) {
                        commandHistory.removeIndex(commandHistory.size - 1);
                    }
                    inputBuffer.setLength(0);
                    historyIndex = -1;
                } else if (i == Input.Keys.BACKSPACE) {
                    if (inputBuffer.length() > 0) {
                        inputBuffer.deleteCharAt(inputBuffer.length() - 1);
                    }
                } else if (i == Input.Keys.UP) {
                    if (historyIndex < commandHistory.size - 1) {
                        historyIndex++;
                        inputBuffer.setLength(0);
                        inputBuffer.append(commandHistory.get(historyIndex));
                    }
                } else if (i == Input.Keys.DOWN) {
                    if (historyIndex > 0) {
                        historyIndex--;
                        inputBuffer.setLength(0);
                        inputBuffer.append(commandHistory.get(historyIndex));
                    } else if (historyIndex == 0) {
                        historyIndex = -1;
                        inputBuffer.setLength(0);
                    }
                } else if (i == Input.Keys.GRAVE) {
                    toggle();
                } else if (i >= Input.Keys.A && i <= Input.Keys.Z) {
                    char c = (char) ('a' + (i - Input.Keys.A));
                    if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) ||
                            Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT)) {
                        c = Character.toUpperCase(c);
                    }
                    inputBuffer.append(c);
                } else if (i >= Input.Keys.NUM_0 && i <= Input.Keys.NUM_9) {
                    inputBuffer.append((char) ('0' + (i - Input.Keys.NUM_0)));
                } else if (i == Input.Keys.SPACE) {
                    inputBuffer.append(' ');
                } else if (i == Input.Keys.PERIOD) {
                    inputBuffer.append('.');
                } else if (i == Input.Keys.MINUS) {
                    inputBuffer.append('-');
                }
            }
        }
    }

    /**
     * Executes a console command.
     * @param command The command string
     */
    private void executeCommand(String command) {
        if (command.isEmpty()) return;

        println("> " + command);

        String[] parts = command.split("\\s+");
        String cmdName = parts[0].toLowerCase();
        String[] args = new String[parts.length - 1];
        System.arraycopy(parts, 1, args, 0, args.length);

        BiConsumer<DevConsole, String[]> handler = commands.get(cmdName);
        if (handler != null) {
            handler.accept(this, args);
        } else {
            println("Unknown command: " + cmdName + ". Type 'help' for commands.");
        }
    }

    /**
     * Prints a line to the console output.
     * @param text Text to print
     */
    public void println(String text) {
        // Split by newlines
        for (String line : text.split("\n")) {
            output.add(line);
            if (output.size > MAX_OUTPUT) {
                output.removeIndex(0);
            }
        }
    }

    /**
     * Renders the console.
     * @param batch SpriteBatch for rendering
     */
    public void render(SpriteBatch batch) {
        if (!visible) return;

        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        float consoleHeight = screenHeight * 0.4f;

        // Draw background
        batch.end();
        Gdx.gl.glEnable(com.badlogic.gdx.graphics.GL20.GL_BLEND);
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, BACKGROUND_ALPHA);
        shapeRenderer.rect(0, screenHeight - consoleHeight, screenWidth, consoleHeight);
        shapeRenderer.end();
        batch.begin();

        // Draw input line
        float y = screenHeight - consoleHeight + PADDING;
        String inputLine = "> " + inputBuffer.toString() + (cursorVisible ? "_" : "");
        font.setColor(Color.GREEN);
        font.draw(batch, inputLine, PADDING, y + LINE_HEIGHT);

        // Draw output
        y = screenHeight - PADDING;
        font.setColor(Color.WHITE);
        for (int i = output.size - 1; i >= 0 && y > screenHeight - consoleHeight + LINE_HEIGHT * 2; i--) {
            font.draw(batch, output.get(i), PADDING, y);
            y -= LINE_HEIGHT;
        }
    }

    /**
     * Disposes console resources.
     */
    public void dispose() {
        shapeRenderer.dispose();
    }
}
