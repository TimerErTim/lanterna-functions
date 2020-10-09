package eu.timerertim.lanterna.extras;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;

import java.io.IOException;
import java.util.LinkedList;

public class TerminalConsole {
    // Essential components
    private final LinkedList<String> content;
    private final Screen screen;
    private final TextGraphics graphics;
    private final TextColor textColor;
    private final TextColor backgroundColor;

    // Options
    private final boolean autoUpdate;
    private final boolean autoScrolling;
    private final boolean autoResize;
    private final KeyType skipTextAnimationKey; //This variable is null if animated println is deactivated

    /**
     * Creates a new TerminalConsole object and prepares the given {@code Screen} object.
     * <p>
     *  This constructor initializes the {@code TerminalConsole} object with standard values and the parameters.
     *  It also prepares the screen by hiding the cursor, starting it (effectively entering private mode) and clearing it.
     * </p>
     * <p>
     *  Standard values are:
     *  <ul>
     *      <li>{@code autoScrolling} = true</li>
     *      <li>{@code autoResize} = true</li>
     *      <li>{@code skipTextAnimationKey} = null -> which effectively means no text animation</li>
     *  </ul>
     * </p>
     * <p>
     *  After this constructor has been called the screen should not be directly modified anymore. A {@code TerminalConsole}
     *  keeps track of the consoles content which is needed for its functionality.
     * </p>
     *
     * @param screen the underlying screen
     * @param autoUpdate the boolean describing the update behavior (comparable to auto flush)
     * @throws IOException the {@code Exception} thrown if there is an underlying IO error when starting the screen
     */
    public TerminalConsole(Screen screen, boolean autoUpdate) throws IOException {
        // Initialize local variables and standard values
        this.screen = screen;
        this.autoUpdate = autoUpdate;
        this.content = new LinkedList<>();
        this.textColor = TextColor.ANSI.WHITE;
        this.backgroundColor = TextColor.ANSI.BLACK;
        this.autoScrolling = true;
        this.autoResize = true;
        this.skipTextAnimationKey = null;

        // Initializes screen
        screen.setCursorPosition(null);
        screen.startScreen();
        screen.clear();
        screen.refresh();
        graphics = screen.newTextGraphics();
    }

    /**
     * Same as calling {@link TerminalConsole#TerminalConsole(Screen, boolean) TerminalConsole(screen, true)}.
     * <p>
     *     Creates a new TerminalConsole with {@code autoUpdate} enabled.
     * </p>
     *
     * @param screen the underlying screen
     * @throws IOException the {@code Exception} thrown if there is an underlying IO error when starting the screen
     */
    public TerminalConsole(Screen screen) throws IOException {
        this(screen, true);
    }
}
