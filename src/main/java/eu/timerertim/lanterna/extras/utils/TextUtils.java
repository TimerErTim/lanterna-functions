package eu.timerertim.lanterna.extras.utils;

import com.googlecode.lanterna.TerminalTextUtils;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

public class TextUtils {
    /**
     * Applies softwrapping on the String of text.
     * <p>
     * The way this works is by calling {@link TerminalTextUtils#getWordWrappedText(int, String...)}.
     * The maximum available space is measured in columns.
     *
     * @param line    the text to apply wrapping on
     * @param columns the maximum available space per line
     * @return String array containing the line in a softwrapped format
     */
    public static String[] applySoftwrapping(String line, int columns) {
        return TerminalTextUtils.getWordWrappedText(columns, line).toArray(String[]::new);
    }

    /**
     * Applies hardwrapping on the String of text.
     * <p>
     * The way this works is by calling {@link TerminalTextUtils#fitString(String, int)}
     * and appending its output to the end of a list. The maximum available space is
     * measured in columns.
     *
     * @param line the text to apply wrapping on
     * @param columns the maximum available space per line
     * @return String array containing the line in a hardwrapped format
     */
    public static String[] applyHardwrapping(String line, int columns) {
        String fitString = TerminalTextUtils.fitString(line, columns);
        String otherString = line.replaceFirst(Pattern.quote(fitString), ""); // String that doesn't fit in the first line
        if (otherString.length() > 0) {
            // Second line also has to be hardwrapped and added to the list
            List<String> lines = new LinkedList<>();
            lines.add(fitString);
            Collections.addAll(lines, applyHardwrapping(otherString, columns));
            return lines.toArray(String[]::new);
        } else {
            // No wrapping needed to be done
            return new String[]{fitString};
        }
    }
}
