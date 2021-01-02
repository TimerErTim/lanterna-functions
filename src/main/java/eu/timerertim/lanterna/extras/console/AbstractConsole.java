package eu.timerertim.lanterna.extras.console;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.input.KeyType;
import eu.timerertim.lanterna.extras.handlers.ConsoleInputListener;
import eu.timerertim.lanterna.extras.utils.WrappingMode;

abstract class AbstractConsole implements Console {
    // Essential components
    protected ContentManager contentManager;
    protected ConsoleInputListener consoleInput;
    protected String[] displayContent;
    protected int scrollPosition;
    protected boolean closed;

    // Options
    protected boolean autoUpdate;
    protected boolean autoScrolling;
    protected boolean autoResize;
    protected KeyType skipTextAnimationKey; //This variable is null if animated println is deactivated

    // Config
    protected TextColor textColor;
    protected TextColor backgroundColor;
    protected WrappingMode wrapping;
    protected String readLinePrompt;

    /**
     * Basic constructor used for initializing basic AbstractConsole object with
     * default values.
     * <p>
     * It is advised to use this constructor when implementing your own in a subclass
     * as only critical fields have to be assigned manually.
     * <p>
     * Remaining fields are:
     * <ul>
     *     <li>{@link AbstractConsole#contentManager}
     *     <li>{@link AbstractConsole#displayContent}
     *     <li>{@link AbstractConsole#consoleInput}
     * </ul>
     *
     * @param autoUpdate the boolean describing the update behavior (comparable to auto flush)
     */
    protected AbstractConsole(boolean autoUpdate) {
        this.autoUpdate = autoUpdate;
        this.closed = false;
        this.autoScrolling = true;
        this.autoResize = true;
        this.scrollPosition = 0;
        this.skipTextAnimationKey = null;
        this.textColor = TextColor.ANSI.WHITE;
        this.backgroundColor = TextColor.ANSI.BLACK;
        this.wrapping = WrappingMode.SOFTWRAPPING;
        this.readLinePrompt = ">";
    }

    @Override
    public boolean isAutoUpdate() {
        return autoUpdate;
    }

    @Override
    public void setAutoUpdate(boolean autoUpdate) {
        this.autoUpdate = autoUpdate;
    }

    @Override
    public boolean isTextAnimated() {
        return skipTextAnimationKey != null;
    }

    @Override
    public void setSkipTextAnimationKey(KeyType skipTextAnimationKey) {
        this.skipTextAnimationKey = skipTextAnimationKey;
    }

    @Override
    public WrappingMode getWrapping() {
        return wrapping;
    }

    @Override
    public void setWrapping(WrappingMode wrapping) {
        //TODO: rewrapping when changing WrappingMode
        this.wrapping = wrapping;
        contentManager.setWrapping(wrapping);
    }

    @Override
    public TextColor getTextColor() {
        return textColor;
    }

    @Override
    public void setTextColor(TextColor textColor) {
        this.textColor = textColor;
    }

    @Override
    public TextColor getBackgroundColor() {
        return backgroundColor;
    }

    @Override
    public void setBackgroundColor(TextColor backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    @Override
    public boolean isAutoScrolling() {
        return autoScrolling;
    }

    @Override
    public void setAutoScrolling(boolean autoScrolling) {
        this.autoScrolling = autoScrolling;
    }

    @Override
    public boolean isAutoResize() {
        return autoResize;
    }

    @Override
    public void setAutoResize(boolean autoResize) {
        this.autoResize = autoResize;
    }

    @Override
    public String getReadLinePrompt() {
        return readLinePrompt;
    }

    @Override
    public void setReadLinePrompt(String readLinePrompt) {
        this.readLinePrompt = readLinePrompt;
    }
}
