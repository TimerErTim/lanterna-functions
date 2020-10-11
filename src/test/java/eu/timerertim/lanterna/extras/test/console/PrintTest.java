package eu.timerertim.lanterna.extras.test.console;

import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import eu.timerertim.lanterna.extras.TerminalConsole;

import java.io.IOException;

public class PrintTest {
    public static void main(String... args) throws IOException {
        TerminalConsole console = new TerminalConsole(new DefaultTerminalFactory().createScreen(), false);
        console.print("Test");
        console.print(" abstand ");
        console.println("Test 1");
        console.println("NewLine\nAnother newline");
        console.update();
    }
}
