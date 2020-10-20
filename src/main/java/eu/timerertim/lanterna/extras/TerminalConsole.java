package eu.timerertim.lanterna.extras;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TabBehaviour;

import java.io.Closeable;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;

public class TerminalConsole implements Closeable {
    // Essential components
    private final String[] wrappedContent; //This are the lines that are actually shown on the console
    private final LinkedList<String> content; //This effectively is the lines users of this class want to print
    private final Screen screen;
    private final TextGraphics graphics;
    private final int scrollPosition;

    // Options
    private TextColor textColor;
    private TextColor backgroundColor;
    private boolean autoUpdate;
    private boolean autoScrolling;
    private boolean autoResize;
    private KeyType skipTextAnimationKey; //This variable is null if animated println is deactivated

    // User config
    private String readLinePrompt;
    private boolean closed;
    //TODO: Implement autoupdating
    //TODO: Add softwrapping

    /**
     * Creates a new TerminalConsole object and prepares the given {@code Screen} object.
     * <p>
     * This constructor initializes the {@code TerminalConsole} object with standard values and the parameters.
     * It also prepares the screen by hiding the cursor, starting it (effectively entering private mode) and clearing it.
     * <p>
     * Standard values are:
     * <ul>
     *     <li>{@code autoScrolling} = true
     *     <li>{@code autoResize} = true
     *     <li>{@code skipTextAnimationKey} = null -> which effectively means no text animation
     * </ul><p>
     * After this constructor has been called the screen should not be directly modified anymore. A {@code TerminalConsole}
     * keeps track of the consoles content, which is needed for its functionality.
     *
     * @param screen     the underlying screen
     * @param autoUpdate the boolean describing the update behavior (comparable to auto flush)
     * @throws IOException the {@code Exception} thrown if there is an underlying IO error when starting the screen
     */
    public TerminalConsole(Screen screen, boolean autoUpdate) throws IOException {
        // Initialize local variables and standard values
        this.screen = screen;
        this.autoUpdate = autoUpdate;
        this.content = new LinkedList<>();
        this.wrappedContent = new String[screen.getTerminalSize().getRows() - 1]; // Array with the size of screens vertical height
        this.textColor = TextColor.ANSI.WHITE;
        this.backgroundColor = TextColor.ANSI.BLACK;
        this.scrollPosition = 0;
        this.autoScrolling = true;
        this.autoResize = true;
        this.skipTextAnimationKey = null;
        this.readLinePrompt = ">";
        this.closed = false;

        // Initializes screen
        graphics = screen.newTextGraphics();
        graphics.setForegroundColor(textColor);
        graphics.setBackgroundColor(backgroundColor);
        graphics.setTabBehaviour(TabBehaviour.ALIGN_TO_COLUMN_4);
        screen.setCursorPosition(null);
        screen.startScreen();
        clear();
        update();
    }

    /**
     * Same as calling {@link TerminalConsole#TerminalConsole(Screen, boolean) TerminalConsole(screen, true)}.
     * <p>
     * Creates a new TerminalConsole with {@code autoUpdate} enabled.
     *
     * @param screen the underlying screen
     * @throws IOException the {@code Exception} thrown if there is an underlying IO error when starting the screen
     */
    public TerminalConsole(Screen screen) throws IOException {
        this(screen, true);
    }

    /**
     * Prints the given text out on the console like {@code System.out.print} would do.
     * <p>
     * This method handles \r (carriage return) and \n (newline) perfectly fine. It also
     * automatically softwraps the text. Note that you manually have to call {@link TerminalConsole#update()}
     * in case {@code autoUpdate} is false.
     *
     * @param text the text to print
     */
    public void print(String text) {
        String[] lines = text.split("\n", 2);
        if (lines.length > 1) {
            println(lines[0]);
            print(lines[1]);
        } else {
            // Print logic goes here
            // Carriage return special character handling
            String[] carriage = text.split("\r");
            if (carriage.length > 1) {
                content.set(content.size() - 1, "");
                text = carriage[carriage.length - 1];
            }

            // Main print function
            content.set(content.size() - 1, content.get(content.size() - 1) + text);
            wrappedContent[content.size() - 1] = content.get(content.size() - 1);
            drawLine(wrappedContent[content.size() - 1], content.size() - 1);
        }
    }

    /**
     * Prints the given line out on the console like {@code System.out.println} would do, which also means
     * jumping to the next line.
     * <p>
     * This behaves exactly like calling {@link TerminalConsole#print(String) print(line + "\n")}, even though
     * the code base is slightly different. Therefore this method also handles \r and \n perfectly fine and
     * softwraps the text. Note that you manually have to call {@link TerminalConsole#update()}
     * in case {@code autoUpdate} is false.
     *
     * @param line the line to print
     */
    public void println(String line) {
        print(line);
        content.add("");
    }

    /**
     * Lets the user input a String and retrieve it afterwards.
     * <p>
     * The method is pretty straight forward, as it does exactly that.
     * Note, that the method is blocking until the user confirms his input
     * by pressing the ENTER key.
     * <p>
     * Also displays a small prompt String to let the user know, that his input is
     * needed. The prompt String can be specified by calling {@link TerminalConsole#setReadLinePrompt(String)}.
     * This method is always "autoUpdating", because the user needs constant feedback
     * regardless of {@code autoUpdate} being true or not.
     *
     * @return the user given String
     */
    public String readLine() {
        StringBuilder input = new StringBuilder();
        KeyStroke key;

        // Read user input
        clearInputLine(true);
        try {
            update();
            while ((key = screen.readInput()).getKeyType() != KeyType.Enter) {
                if (key.getKeyType() == KeyType.Backspace) {
                    input.reverse().deleteCharAt(0).reverse();
                } else if (key.getKeyType() == KeyType.Character) {
                    input.append(key.getCharacter());
                }
                // Give user feedback
                String inputLine = readLinePrompt + input.toString();
                drawLine(inputLine.substring(Math.max(inputLine.length() - screen.getTerminalSize().getColumns(), 0)), wrappedContent.length);
                update();
            }
            // Reset line
            clearInputLine(false);
            update();
        } catch (IOException ex) {
            return null;
        }

        // Return string
        return input.toString();
    }

    /**
     * Updates the underlying screen by either comparing the difference between backbuffer and frontbuffer or
     * redrawing the whole thing (whatever is more efficient).
     * <p>
     * This method is primarily called because it should promise the best possible performance.
     * Typically you only call {@link TerminalConsole#updateFull()} when directly editing the underlying {@code Terminal}
     * or {@code Screen} object, which is something you shouldn't do in the first place.
     *
     * @throws IOException the {@code Exception} thrown if an IO error occurs while refreshing the screen
     */
    public void update() throws IOException {
        screen.refresh(Screen.RefreshType.AUTOMATIC);
    }

    /**
     * Updates the whole underlying screen object, moving all of the backbuffer to the frontbuffer.
     * <p>
     * Typically you only call this method when directly editing the underlying {@code Terminal} or
     * {@code Screen} object, which is something you shouldn't do in the first place. It is not
     * recommended making any direct changes to one of those objects, as all of the changes are
     * lost after calling this method.
     *
     * @throws IOException the {@code Exception} thrown if an IO error occurs while refreshing the screen
     */
    public void updateFull() throws IOException {
        redrawFull();
        screen.refresh(Screen.RefreshType.COMPLETE);
    }

    /**
     * Clears the console.
     * <p>
     * Removes any content from this console and redrawing it, leaving you with a
     * blank terminal. You have to manually call {@link TerminalConsole#update()}
     * in order to make the changes visible if {@code autoUpdate} is false.
     */
    public void clear() {
        content.clear();
        content.add("");
        Arrays.fill(wrappedContent, "");

        if (autoUpdate) {
            try {
                updateFull();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            redrawFull();
        }
    }

    /**
     * Just stops the underlying screen.
     * <p>
     * Should only be called if you decide not to use this console anymore.
     * This is an alternative for calling {@link Screen#close()} on the underlying screen directly.
     * Useful for cases in which you do not have a reference to the original screen anymore.
     * <p>
     * Basically the same as calling {@link TerminalConsole#stopScreen()} but has no effect on second call
     * as it is specified this way in the {@code Closeable} interface.
     */
    public void close() {
        if (!closed) {
            stopScreen();
            closed = true;
        }
    }

    /**
     * Just stops the underlying screen.
     * <p>
     * This is an alternative for calling {@link Screen#stopScreen} on the underlying screen directly.
     * Useful for cases in which you do not have a reference to the original screen anymore.
     */
    public void stopScreen() {
        try {
            screen.close();
        } catch (IOException e) {
            screen.clear();
        }
    }

    /**
     * Gets the prompt String
     * <p>
     * Can be set by {@link TerminalConsole#setReadLinePrompt(String)}.
     *
     * @return the prompt String
     */
    public String getReadLinePrompt() {
        return readLinePrompt;
    }

    /**
     * Sets the String prompting the user for input. Typically something like {@code ">"} or
     * {@code ":"}.
     *
     * @param readLinePrompt the prompting String
     */
    public void setReadLinePrompt(String readLinePrompt) {
        this.readLinePrompt = readLinePrompt;
    }

    /**
     * Returns the autoUpdate state
     *
     * @return the autoUpdate boolean
     */
    public boolean isAutoUpdate() {
        return autoUpdate;
    }

    /**
     * Activates or deactivates autoUpdating.
     * <p>
     * The feature is comparable to autoFlush when using streams. It automatically
     * updates the console upon changes, basically calling {@link TerminalConsole#update()}.
     * <p>
     * This greatly reduces the work needed for a functional console. However Exceptions are not caught or
     * handled in any way. Only the StackTrace is printed to {@code System.out}. Depending on your
     * needs you might not want this. It should be noted, that under normal circumstances no Exception
     * will occur.
     *
     * @param autoUpdate tne autoUpdate
     */
    public void setAutoUpdate(boolean autoUpdate) {
        this.autoUpdate = autoUpdate;
    }

    /**
     * Returns the state of text animation.
     * <p>
     * To set this value use {@link TerminalConsole#setSkipTextAnimationKey}.
     * It takes in a {@link KeyType} the user can use to skip the text animation. If the given {@code KeyType}
     * is null, that means that animated text is deactivated and this method will return false.
     *
     * @return true if skip key is assigned, which means text is animated
     */
    public boolean isTextAnimated() {
        return skipTextAnimationKey != null;
    }

    /**
     * Sets the key used for skipping text animation and activates/deactivates said feature.
     * <p>
     * Takes a {@link KeyType} that the user will use to skip text animations. If null is passed, the
     * feature will be deactivated.
     *
     * @param skipTextAnimationKey null deactivates the animation for text, not null is the {@code KeyType} users have to press to skip animation
     */
    public void setSkipTextAnimationKey(KeyType skipTextAnimationKey) {
        this.skipTextAnimationKey = skipTextAnimationKey;
    }

    public TextColor getTextColor() {
        return textColor;
    }

    public void setTextColor(TextColor textColor) {
        this.textColor = textColor;
        graphics.setForegroundColor(textColor);
        redrawFull();
    }

    public TextColor getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(TextColor backgroundColor) {
        this.backgroundColor = backgroundColor;
        graphics.setBackgroundColor(backgroundColor);
        redrawFull();
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

    private void drawLine(String line, int row) {
        int emptySpace = screen.getTerminalSize().getColumns() - line.length();
        String emptySpaces;
        if (emptySpace > 0)
            emptySpaces = String.format("%1$" + emptySpace + "s", " ");
        else {
            emptySpaces = "";
        }
        graphics.putString(0, row, line + emptySpaces);
    }

    private void redrawFull() {
        for (int row = 0; row < wrappedContent.length; row++) {
            drawLine(wrappedContent[row], row);
        }
        clearInputLine(false);
    }

    private void clearInputLine(boolean prompt) {
        drawLine((prompt ? readLinePrompt : ""), wrappedContent.length);
    }
}
