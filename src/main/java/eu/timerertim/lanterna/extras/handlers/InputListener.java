package eu.timerertim.lanterna.extras.handlers;

import com.googlecode.lanterna.input.KeyStroke;

import java.io.Closeable;
import java.io.IOException;

/**
 * This libraries InputListeners are classes,
 * which handle the processing of input.
 * <p>
 * Apart from this, they also can be used transmit the
 * input it should handle to another piece of code
 * through the functions defined in the {@link InputListener}
 * interface.
 */
public interface InputListener extends Runnable, Closeable {
    /**
     * Returns the next {@code Key} provided by the input queue from now on, blocking until that very moment.
     * The input queue is provided by the {@link com.googlecode.lanterna.input.InputProvider InputProvider} this
     * InputListener is handling.
     * <p>
     * NOTE: This method is blocking.
     *
     * @return the transmitted {@code Key}
     */
    KeyStroke readInput();

    /**
     * Returns the next {@code Key} off the input queue or null if there is no more input events available.
     * The input queue is provided by the {@link com.googlecode.lanterna.input.InputProvider InputProvider} this
     * InputListener is handling.
     * <p>
     * NOTE: This method is <b>not</b> blocking.
     *
     * @return the transmitted {@code Key}
     */
    KeyStroke pollInput() throws IOException;


    /**
     * Closes this InputListener and releasing it's resources.
     * It stops its activity and stops underlying threads.
     */
    @Override
    void close();
}
