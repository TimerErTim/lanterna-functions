package eu.timerertim.lanterna.extras.handlers;

import com.googlecode.lanterna.input.InputProvider;
import com.googlecode.lanterna.input.KeyStroke;

import java.io.IOException;

abstract class AbstractInputListener implements InputListener {
    private final InputProvider input;
    private final Thread thisThread;
    private Thread helperThread;
    private KeyStroke latestKey;

    protected AbstractInputListener(InputProvider input) {
        this.input = input;
        (thisThread = new Thread(this)).setDaemon(true);
        thisThread.start();
    }

    @Override
    public KeyStroke readInput() {
        // Preparation
        latestKey = null;
        helperThread = Thread.currentThread();

        // Waiting for input
        try {
            synchronized (helperThread) {
                helperThread.wait();
            }
        } catch (InterruptedException e) {
            return latestKey;
        }

        // Input was made
        helperThread = null;
        return latestKey;
    }

    @Override
    public KeyStroke pollInput() throws IOException {
        return input.pollInput();
    }

    @Override
    public void run() {
        while (!thisThread.isInterrupted()) {
            try {
                // Checking for new input
                KeyStroke key = input.readInput();
                if (processInput(key)) {
                    if (latestKey == null && helperThread != null) {
                        latestKey = key;
                        synchronized (helperThread) {
                            helperThread.notify();
                        }
                    } else {
                        latestKey = key;
                    }
                }
            } catch (IOException e) {
                thisThread.interrupt();
            }
        }
    }

    @Override
    public void close() {
        thisThread.interrupt();
        helperThread.notify();
        helperThread = null;
    }

    /**
     * Processes input prior to transmitting it to the user of this
     * {@code InputListener}.
     * <p>
     * This method is used to process and use user input. By
     * returning a boolean value, it is possible to tell the
     * {@code InputListener} to not further transmit the {@code KeyStroke}.
     *
     * @return false -> not transmit input<br>
     * true -> transmit input
     */
    protected abstract boolean processInput(KeyStroke key);
}
