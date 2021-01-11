package eu.timerertim.lanterna.extras.console;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.input.KeyType;
import eu.timerertim.lanterna.extras.utils.WrappingMode;

import java.io.Closeable;
import java.io.IOException;

public interface Console extends Closeable {
    /**
     * Prints the given text out on the console like {@code System.out.print} would do.
     * <p>
     * This method handles \r (carriage return) and \n (newline) perfectly fine. It also
     * automatically applies wrapping to the text. Note that you manually have to call {@link Console#update()}
     * in case {@code autoUpdate} is disabled.
     *
     * @param text the text to print
     */
    void print(String text);

    /**
     * Prints the given line out on the console like {@code System.out.println} would do, which also means
     * jumping to the next line.
     * <p>
     * This behaves exactly like calling {@link Console#print(String) print(line + "\n")}, even though
     * the code base is slightly different. Therefore this method also handles \r and \n perfectly fine and
     * wraps the text accordingly. Note that you manually have to call {@link Console#update()}
     * in case {@code autoUpdate} is disabled.
     *
     * @param line the line to print
     */
    void println(String line);

    /**
     * Lets the user input a String and retrieve it afterwards.
     * <p>
     * The method is pretty straight forward, as it does exactly that.
     * Note, that the method is blocking until the user confirms his input
     * by pressing the ENTER key.
     * <p>
     * Also displays a small prompt String to let the user know, that his input is
     * needed. The prompt String can be specified by calling {@link Console#setReadLinePrompt(String)}.
     * This method is always "autoUpdating", because the user needs constant feedback
     * regardless of {@code autoUpdate} being true or not.
     *
     * @return the user given String
     */
    String readLine();

    /**
     * Updates this Console Object by making <b>changes</b> visible for the user.
     * <p>
     * You will most likely want to call this method because it should promise the best possible performance.
     * Typically you only call {@link Console#updateFull()} when directly editing the underlying {@code Terminal}
     * or {@code Screen} object (if one exists), which is something you shouldn't do in the first place.
     *
     * @throws IOException the {@code Exception} thrown if an IO error occurs while refreshing the console
     */
    void update() throws IOException;

    /**
     * Updates the whole console by making its whole internal buffer visible.
     * <p>
     * Typically you only call this method when directly editing the underlying {@code Terminal} or
     * {@code Screen} object (in case there is one), which is something you shouldn't do in the first place.
     * It is not recommended making any direct changes to one of those objects, as all of the changes are
     * lost after calling this method.
     *
     * @throws IOException the {@code Exception} thrown if an IO error occurs while refreshing the console
     */
    void updateFull() throws IOException;

    /**
     * Clears the console.
     * <p>
     * Removes any content from this console and redraws it, leaving you with a
     * blank console. You have to manually call {@link Console#update()}
     * in order to make the changes visible if {@code autoUpdate} is disabled.
     */
    void clear();

    /**
     * Closes this {@code Console} and releases all resources needed for its functionality.
     * <p>
     * Should only be called if you decide not to use this console anymore.
     * Note that this method also closes underlying screens and terminals, so keep that in mind
     * when calling. That can be useful for cases in which you do not have a reference to
     * the underlying objects anymore.
     */
    void close();

    /**
     * In case the console is emulated using Swing, this method can
     * be used to change the title of the resulting Swing frame.
     *
     * @param title the new title
     */
    void setSwingTitle(String title);

    /**
     * Returns the autoUpdate state
     *
     * @return the autoUpdate boolean
     */
    boolean isAutoUpdate();

    /**
     * Activates or deactivates autoUpdating.
     * <p>
     * The feature is comparable to autoFlush when using streams. It automatically
     * updates the console upon changes, basically calling {@link Console#update()}.
     * <p>
     * This greatly reduces the work needed for a functional console. However Exceptions are not caught or
     * handled in any way. Only the StackTrace is printed to {@code System.out}. Depending on your
     * needs you might not want this. It should be noted, that under normal circumstances no Exception
     * will occur.
     *
     * @param autoUpdate tne autoUpdate
     */
    void setAutoUpdate(boolean autoUpdate);

    /**
     * Returns the state of text animation.
     * <p>
     * To set this value use {@link Console#setSkipTextAnimationKey}.
     * It takes in a {@link KeyType} the user can use to skip the text animation. If the given {@code KeyType}
     * is null, that means that animated text is deactivated and this method will return false.
     *
     * @return true if skip key is assigned, which means text is animated
     */
    boolean isTextAnimated();

    /**
     * Sets the key used for skipping text animation and activates/deactivates said feature.
     * <p>
     * Takes a {@link KeyType} that the user will use to skip text animations. If null is passed, the
     * feature will be deactivated.
     *
     * @param skipTextAnimationKey null deactivates the animation for text, not null is the {@code KeyType} users have to press to skip animation
     */
    void setSkipTextAnimationKey(KeyType skipTextAnimationKey);

    /**
     * Gets the WrappingMode.
     *
     * @return the WrappingMode enum
     */
    WrappingMode getWrapping();

    /**
     * Sets the WrappingMode used for this console object.
     * <p>
     * The wrapping is the text's behavior when it surpasses
     * the horizontal space limit. That limit is defined by
     * the size of this console. For a list of different
     * behaviors please refer to {@link WrappingMode}.
     * <p>
     * Note that this also recalculates the text with the
     * new wrapping and redraws the whole console to show the
     * change. If {@code autoUpdate} is false, you will have
     * to manually update in order to make the changes visible
     * on screen.
     *
     * @param wrapping the WrappingMode used to wrap text
     */
    void setWrapping(WrappingMode wrapping);

    /**
     * Gets the current foreground color.
     *
     * @return the foreground color
     */
    TextColor getTextColor();

    /**
     * Sets the foreground color for the whole console.
     * <p>
     * The color is applied to everything inside the console.
     *
     * @param textColor the foreground color
     */
    void setTextColor(TextColor textColor);

    /**
     * Gets the current background color.
     *
     * @return the background color
     */
    TextColor getBackgroundColor();

    /**
     * Sets the background color for the whole console.
     * <p>
     * The color is applied to everything inside the console.
     *
     * @param backgroundColor the background color
     */
    void setBackgroundColor(TextColor backgroundColor);

    /**
     * Gets the prompt String
     * <p>
     * Can be set by {@link ScreenConsole#setReadLinePrompt(String)}.
     *
     * @return the prompt String
     */
    String getReadLinePrompt();

    /**
     * Sets the String prompting the user for input. Typically something like {@code ">"} or
     * {@code ":"}.
     *
     * @param prompt the prompting String
     */
    void setReadLinePrompt(String prompt);

    /**
     * Gets the state of automatic scrolling.
     *
     * @return the autoScrolling boolean
     */
    boolean isAutoScrolling();

    /**
     * Activates or deactivates automatic scrolling on
     * this Console Object.
     * <p>
     * Automatic scrolling scrolls down in order to
     * display text that would otherwise be outside of
     * the visible space. This is only done if the
     * scrolling is at the bottom of the console.
     * <p>
     * In other words automatic scrolling will not rearrange
     * the scrolling position if the user is viewing previous,
     * out of bounds content.
     *
     * @param autoScrolling whether or not to activate automatic scrolling
     */
    void setAutoScrolling(boolean autoScrolling);

    /**
     * Gets the state of automatic resizing.
     *
     * @return the autoResize boolean
     */
    boolean isAutoResize();

    /**
     * Activates or deactivates automatic resizing.
     * <p>
     * Automatic resizing automatically recalculates
     * wrapping and scroll position on window resizes.
     * If this is deactivated, the only values that
     * change on resize is the area in which
     * the content of this {@code Console} is
     * visible and viewable.
     *
     * @param autoResize whether or not to automatically react properly on resizes
     */
    void setAutoResize(boolean autoResize);
}
