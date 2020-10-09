package eu.timerertim.lanterna.extras;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;

import java.io.IOException;
import java.util.LinkedList;

public class TerminalConsole {
    // Essential components
    private final String[] wrappedContent;
    private final LinkedList<String> content;
    private final Screen screen;
    private final TextGraphics graphics;

    // Options
    private TextColor textColor;
    private TextColor backgroundColor;
    private boolean autoUpdate;
    private boolean autoScrolling;
    private boolean autoResize;
    private KeyType skipTextAnimationKey; //This variable is null if animated println is deactivated

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
        this.wrappedContent = new String[0];
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

    //TODO: Implement println

    // Getter
    public TextColor getTextColor() {
        return textColor;
    }

    // Setter
    public void setTextColor(TextColor textColor) {
        this.textColor = textColor;
    }

    public TextColor getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(TextColor backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public boolean isAutoUpdate() {
        return autoUpdate;
    }

    public void setAutoUpdate(boolean autoUpdate) {
        this.autoUpdate = autoUpdate;
    }

    public boolean isAutoScrolling() {
        return autoScrolling;
    }

    public void setAutoScrolling(boolean autoScrolling) {
        this.autoScrolling = autoScrolling;
    }

    public boolean isAutoResize() {
        return autoResize;
    }

    public void setAutoResize(boolean autoResize) {
        this.autoResize = autoResize;
    }

    /**
     * Returns the state of text animation.
     * <p>
     *     To set this value use {@link TerminalConsole#setSkipTextAnimationKey}.
     *     It takes in a {@link KeyType} the user can use to skip the text animation. If the given {@code KeyType}
     *     is null, that means that animated text is deactivated and this method will return false.
     * </p>
     *
     * @return true if skip key is assigned, which means text is animated
     */
    public boolean isTextAnimated(){
        return skipTextAnimationKey != null;
    }

    /**
     * Sets the key used for skipping text animation and activates/deactivates said feature.
     * <p>
     *      Takes a {@link KeyType} that the user will use to skip text animations. If null is passed, the
     *      feature will be deactivated.
     * </p>
     *
     * @param skipTextAnimationKey null deactivates the animation for text, not null is the {@code KeyType} users have to press to skip animation
     */
    public void setSkipTextAnimationKey(KeyType skipTextAnimationKey) {
        this.skipTextAnimationKey = skipTextAnimationKey;
    }
}
