package eu.timerertim.lanterna.extras.handlers;

import com.googlecode.lanterna.input.InputProvider;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import eu.timerertim.lanterna.extras.console.Console;

public class ConsoleInputListener extends AbstractInputListener {
    private Console console;

    public ConsoleInputListener(InputProvider input) {
        super(input);
    }

    @Override
    protected boolean processInput(KeyStroke key) {
        return !key.getKeyType().equals(KeyType.PageUp);
    }
}
