package eu.timerertim.lanterna.extras;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TabBehaviour;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;

public class TerminalConsole {
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
    //TODO: Implement autoupdating
    //TODO: Add softwrapping
    //TODO: Add close function

    /**
     * Creates a new TerminalConsole object and prepares the given {@code Screen} object.
     * <p>
     *  This constructor initializes the {@code TerminalConsole} object with standard values and the parameters.
     *  It also prepares the screen by hiding the cursor, starting it (effectively entering private mode) and clearing it.
     * <p>
     *  Standard values are:
     *  <ul>
     *      <li>{@code autoScrolling} = true
     *      <li>{@code autoResize} = true
     *      <li>{@code skipTextAnimationKey} = null -> which effectively means no text animation
     *  </ul><p>
     *  After this constructor has been called the screen should not be directly modified anymore. A {@code TerminalConsole}
     *  keeps track of the consoles content, which is needed for its functionality.
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
        this.wrappedContent = new String[screen.getTerminalSize().getRows()-1]; // Array with the size of screens vertical height
        this.textColor = TextColor.ANSI.WHITE;
        this.backgroundColor = TextColor.ANSI.BLACK;
        this.scrollPosition = 0;
        this.autoScrolling = true;
        this.autoResize = true;
        this.skipTextAnimationKey = null;
        this.readLinePrompt = ">";

        // Initializes screen
        graphics = screen.newTextGraphics();
        graphics.setForegroundColor(textColor);
        graphics.setBackgroundColor(backgroundColor);
        graphics.setTabBehaviour(TabBehaviour.ALIGN_TO_COLUMN_4);
        content.add("");
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
    public void print(String text){
        String[] lines = text.split("\n", 2);
        if(lines.length > 1){
            println(lines[0]);
            print(lines[1]);
        }else{
            // Print logic goes here
            // Carriage return special character handling
            String[] carriage = text.split("\r");
            if(carriage.length > 1){
                content.set(content.size()-1, "");
                text = carriage[carriage.length-1];
            }

            // Main print function
            content.set(content.size()-1, content.get(content.size()-1) + text);
            wrappedContent[content.size()-1] = content.get(content.size()-1);
            drawLine(wrappedContent[content.size()-1], content.size()-1);
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
    public void println(String line){
        print(line);
        content.add("");
    }

    /**
     * 
     *
     * @return
     */
    public String readLine(){
        //TODO: Vertical scrolling in case of long Strings
        StringBuilder input = new StringBuilder();
        KeyStroke key;
        int cursorPos = 0;

        // Read user input
        clearInputLine(true);
        try {
            update();
            while ((key = screen.readInput()).getKeyType() != KeyType.Enter) {
                if (key.getKeyType() == KeyType.Backspace && cursorPos > 0) {
                    input.reverse().deleteCharAt(0).reverse();
                    graphics.setCharacter(readLinePrompt.length() + --cursorPos, wrappedContent.length, ' ');
                } else if (key.getKeyType() == KeyType.Character) {
                    input.append(key.getCharacter());
                    graphics.setCharacter(readLinePrompt.length() + cursorPos++, wrappedContent.length, key.getCharacter());
                }
                update();
            }
            // Reset line
            clearInputLine(false);
            update();
        }catch (IOException ex){
            return null;
        }

        // Return string
        return input.toString();
    }

    private void drawLine(String line, int row){
        int emptySpace = screen.getTerminalSize().getColumns() - line.length();
        String emptySpaces;
        if(emptySpace > 0)
         emptySpaces = String.format("%1$"+emptySpace+"s", " ");
        else{
            emptySpaces = "";
        }
        graphics.putString(0, row, line + emptySpaces);
    }

    private void redrawFull(){
        for(int row = 0; row < wrappedContent.length; row++){
            drawLine(wrappedContent[row], row);
        }
        clearInputLine(false);
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
    public void clear(){
        content.clear();
        Arrays.fill(wrappedContent, "");

        if(autoUpdate){
            try {
                updateFull();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            redrawFull();
        }
    }

    private void clearInputLine(boolean prompt){
        drawLine((prompt ? readLinePrompt : ""), wrappedContent.length);
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
     * To set this value use {@link TerminalConsole#setSkipTextAnimationKey}.
     * It takes in a {@link KeyType} the user can use to skip the text animation. If the given {@code KeyType}
     * is null, that means that animated text is deactivated and this method will return false.
     *
     * @return true if skip key is assigned, which means text is animated
     */
    public boolean isTextAnimated(){
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
}
