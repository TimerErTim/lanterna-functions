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

    public TerminalConsole(Screen screen) throws IOException {
        this(screen, true);
    }


}
