package eu.timerertim.lanterna.extras.test.console;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import eu.timerertim.lanterna.extras.TerminalConsole;

import java.io.IOException;

public class ColorChangeTest {
    public static void main(String[] args) throws IOException {
        TerminalConsole console = new TerminalConsole(new DefaultTerminalFactory().createScreen(), false);
        console.println("Testing");
        console.update();
        console.setBackgroundColor(TextColor.ANSI.BLUE);
        console.print("Test other background");
        console.update();
        console.setTextColor(TextColor.ANSI.RED);
        console.println(" and textcolor");
        console.update();
    }
}
