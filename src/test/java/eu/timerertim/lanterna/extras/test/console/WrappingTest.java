package eu.timerertim.lanterna.extras.test.console;

import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import eu.timerertim.lanterna.extras.console.ScreenConsole;

import java.io.IOException;

public class WrappingTest {
    public static void main(String[] args) throws IOException, InterruptedException {
        ScreenConsole console = new ScreenConsole(new DefaultTerminalFactory().createScreen(), false);
        console.print("This text is really long and there should be softwrapping applied to it, unless I really messed up. In such a case I hope, that my PC crashes, so I don't have to work on this any longer");
        console.update();
        Thread.sleep(4000);
        console.println("This text\rThe wrapped text will hopefully be replaced");
        console.print("There is another really long text which should be wrapped based on words. Hopefully this text can also be replaced.");
        console.update();
        Thread.sleep(3000);
        console.println("\rlastly that Text is removed. After 5 secs the whole console will be cleared");
        console.update();
        Thread.sleep(5000);
        console.clear();
        console.update();
    }
}
