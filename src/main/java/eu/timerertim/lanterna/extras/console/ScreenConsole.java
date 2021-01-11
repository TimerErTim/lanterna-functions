package eu.timerertim.lanterna.extras.console;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TabBehaviour;
import eu.timerertim.lanterna.extras.handlers.ConsoleInputListener;

import java.io.IOException;

public class ScreenConsole extends AbstractConsole {
    private final Screen screen;
    private final TextGraphics graphics;
    //TODO: Implement autoupdating

    /**
     * Creates a new ScreenConsole object and prepares the given {@code Screen} object.
     * <p>
     * This constructor initializes the {@code ScreenConsole} object with standard values and the parameters.
     * It also prepares the screen by hiding the cursor, starting it (effectively entering private mode) and clearing it.
     * <p>
     * Standard values are:
     * <ul>
     *     <li>{@code autoScrolling} = true
     *     <li>{@code autoResize} = true
     *     <li>{@code skipTextAnimationKey} = null -> which effectively means no text animation
     * </ul><p>
     * After this constructor has been called the screen should not be directly modified anymore. A {@code ScreenConsole}
     * keeps track of the consoles content, which is needed for its functionality.
     *
     * @param screen     the underlying screen
     * @param autoUpdate the boolean describing the update behavior (comparable to auto flush)
     * @throws IOException the {@code Exception} thrown if there is an underlying IO error when starting the screen
     */
    public ScreenConsole(Screen screen, boolean autoUpdate) throws IOException {
        super(autoUpdate);

        // Initializes screen
        this.screen = screen;
        contentManager = new ContentManager(screen.getTerminalSize(), wrapping);
        displayContent = contentManager.getDisplayContent();
        consoleInput = new ConsoleInputListener(screen);
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
     * Same as calling {@link ScreenConsole#ScreenConsole(Screen, boolean) ScreenConsole(screen, true)}.
     * <p>
     * Creates a new ScreenConsole with {@code autoUpdate} enabled.
     *
     * @param screen the underlying screen
     * @throws IOException the {@code Exception} thrown if there is an underlying IO error when starting the screen
     */
    public ScreenConsole(Screen screen) throws IOException {
        this(screen, true);
    }

    @Override
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
                contentManager.replaceLine("");
                text = carriage[carriage.length - 1];
            }

            // Main print function
            contentManager.appendLine(text);
            contentManager.fillDisplayContent(scrollPosition);
            redrawFull();
        }
    }

    @Override
    public void println(String line) {
        print(line);
        contentManager.addLine("");
    }

    @Override
    public String readLine() {
        StringBuilder input = new StringBuilder();
        int selectedPos = 0;
        KeyStroke key;

        // Read user input
        screen.setCursorPosition(new TerminalPosition(readLinePrompt.length(), screen.getTerminalSize().getRows()));
        clearInputLine(true);
        try {
            update();
            while ((key = consoleInput.readInput()).getKeyType() != KeyType.Enter) {
                if (key.getKeyType() == KeyType.Character) {
                    input.insert(selectedPos++, key.getCharacter());
                } else if (key.getKeyType() == KeyType.Backspace && selectedPos > 0) {
                    input.deleteCharAt(--selectedPos);
                } else if (key.getKeyType() == KeyType.Delete && selectedPos < input.length()) {
                    input.deleteCharAt(selectedPos);
                } else if (key.getKeyType() == KeyType.ArrowLeft && selectedPos > 0) {
                    selectedPos--;
                } else if (key.getKeyType() == KeyType.ArrowRight && selectedPos < input.length()) {
                    selectedPos++;
                }

                // Give user feedback
                String inputLine = readLinePrompt + input.toString();
                int offset = ((selectedPos + readLinePrompt.length()) / screen.getTerminalSize().getColumns()) * screen.getTerminalSize().getColumns();
                drawLine(inputLine.substring(offset), screen.getTerminalSize().getRows() - 1);
                screen.setCursorPosition(screen.getCursorPosition().withColumn(readLinePrompt.length() + selectedPos - offset));
                update();
            }

            // Reset line
            clearInputLine(false);
            update();
        } catch (IOException ex) {
            return null;
        } finally {
            screen.setCursorPosition(null);
        }

        // Return string
        return input.toString();
    }

    @Override
    public void update() throws IOException {
        screen.refresh(Screen.RefreshType.AUTOMATIC);
    }

    @Override
    public void updateFull() throws IOException {
        redrawFull();
        screen.refresh(Screen.RefreshType.COMPLETE);
    }

    @Override
    public void clear() {
        contentManager.clear();

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
     * {@inheritDoc}
     * <p>
     * Basically the same as calling {@link ScreenConsole#stopScreen()} and closing
     * internal objects but has no effect on second call
     * as it is specified this way in the {@code Closeable} interface.
     */
    @Override
    public void close() {
        if (!closed) {
            stopScreen();
            consoleInput.close();
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
            consoleInput.close();
            screen.close();
        } catch (IOException e) {
            screen.clear();
        }
    }

    @Override
    public void setTextColor(TextColor textColor) {
        this.textColor = textColor;
        graphics.setForegroundColor(textColor);
        redrawFull();
    }

    @Override
    public void setBackgroundColor(TextColor backgroundColor) {
        this.backgroundColor = backgroundColor;
        graphics.setBackgroundColor(backgroundColor);
        redrawFull();
    }

    private void drawLine(String line, int row) {
        // Check for null value
        if (line == null) {
            line = "";
        }

        // Generate empty spaces needed for filling up the line
        int emptySpace = screen.getTerminalSize().getColumns() - line.length();
        String emptySpaces;
        if (emptySpace > 0)
            emptySpaces = String.format("%1$" + emptySpace + "s", " ");
        else {
            emptySpaces = "";
        }

        // Actually print line
        graphics.putString(0, row, line + emptySpaces);
    }

    private void redrawFull() {
        for (int row = 0; row < displayContent.length; row++) {
            drawLine(displayContent[row], row);
        }
        clearInputLine(false);
    }

    private void clearInputLine(boolean prompt) {
        drawLine((prompt ? readLinePrompt : ""), displayContent.length);
    }
}
