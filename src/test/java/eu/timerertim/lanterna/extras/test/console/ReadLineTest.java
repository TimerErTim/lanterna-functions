package eu.timerertim.lanterna.extras.test.console;

import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import eu.timerertim.lanterna.extras.console.ScreenConsole;

import java.io.IOException;

public class ReadLineTest {
    public static void main(String[] args) throws IOException, InterruptedException {
        ScreenConsole console = new ScreenConsole(new DefaultTerminalFactory().createScreen());
        console.setSwingTitle("ReadLineTest");
        Thread.sleep(3000);
        console.println(console.readLine());
        console.update();
        console.println(console.readLine());
        console.update();
    }
}
