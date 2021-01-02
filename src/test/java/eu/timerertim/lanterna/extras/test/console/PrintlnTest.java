package eu.timerertim.lanterna.extras.test.console;

import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import eu.timerertim.lanterna.extras.console.ScreenConsole;

import java.io.IOException;

public class PrintlnTest {
    public static void main(String... args) throws IOException {
        ScreenConsole console = new ScreenConsole(new DefaultTerminalFactory().createScreen(), false);
        console.println("Test");
        console.println("Test 2");
        console.println("Test 3");
        console.update();
    }
}
