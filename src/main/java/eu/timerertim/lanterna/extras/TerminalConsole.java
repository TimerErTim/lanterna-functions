package eu.timerertim.lanterna.extras;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.screen.Screen;

import java.util.LinkedList;

public class TerminalConsole {
    private LinkedList<String> content;
    private Screen screen;
    private TextGraphics graphics;
    private TextColor textColor, backgroundColor;

    public TerminalConsole() {

    }
}
