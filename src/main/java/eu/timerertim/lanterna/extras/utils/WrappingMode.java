package eu.timerertim.lanterna.extras.utils;

public enum WrappingMode {
    /**
     * No wrapping at all.
     * <p>
     * Lines are printed as they are. If they are too long,
     * the last part will not be displayed.
     */
    NONE((line, columns) -> new String[]{line}),

    /**
     * Word based wrapping.
     * <p>
     * Lines, which are too long to be displayed correctly,
     * will be word wrapped. Which means, that a word
     * will not be ripped in half during a linebreak.
     * Furthermore, the first character in a line
     * will not be an empty space.
     * If a word is too long to fit in a line, that word will be
     * wrapped using hardwrapping. Refer to {@link WrappingMode#HARDWRAPPING}.
     */
    SOFTWRAPPING(TextUtils::applySoftwrapping),

    /**
     * Non word based wrapping.
     * <p>
     * A linebreak will be inserted into the line, where it
     * would exceed the column count. Words can be ripped in half, and
     * if there happens to be a space after the last character, which fit
     * in the displaying row, the next row will start with a whitespace.
     * <p>
     * An alternative more appealing to the eye is {@link WrappingMode#SOFTWRAPPING}.
     */
    HARDWRAPPING(TextUtils::applyHardwrapping);

    private final Wrapping wrapping;

    WrappingMode(Wrapping wrapping) {
        this.wrapping = wrapping;
    }

    public String[] wrap(String line, int columns) {
        return wrapping.wrap(line, columns);
    }

    private interface Wrapping {
        String[] wrap(String line, int columns);
    }
}
