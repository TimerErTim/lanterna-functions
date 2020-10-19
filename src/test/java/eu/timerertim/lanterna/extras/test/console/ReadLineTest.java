package eu.timerertim.lanterna.extras.test.console;

import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import eu.timerertim.lanterna.extras.TerminalConsole;

import java.io.IOException;

public class ReadLineTest {
    public static void main(String[] args) throws IOException {
        TerminalConsole console = new TerminalConsole(new DefaultTerminalFactory().createScreen());
        console.println(console.readLine());
        console.update();
        console.println(console.readLine());
        console.update();
    }
}