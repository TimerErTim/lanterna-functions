package eu.timerertim.lanterna.extras.handlers;

import com.googlecode.lanterna.input.InputProvider;
import com.googlecode.lanterna.input.KeyStroke;

public class ConsoleInputListener extends AbstractInputListener {
    public ConsoleInputListener(InputProvider input) {
        super(input);
    }

    @Override
    protected boolean processInput(KeyStroke key) {
        return true;
    }
}
