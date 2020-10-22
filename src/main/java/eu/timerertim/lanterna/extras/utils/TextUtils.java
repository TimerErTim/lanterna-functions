package eu.timerertim.lanterna.extras.utils;

import com.googlecode.lanterna.TerminalTextUtils;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

public class TextUtils {
    /**
     * @param line
     * @param columns
     * @return
     */
    public String[] applySoftwrapping(String line, int columns) {
        return TerminalTextUtils.getWordWrappedText(columns, line).toArray(String[]::new);
    }

    /**
     * @param line
     * @param columns
     * @return String array containing the line in a hardwrapped format
     */
    public String[] applyHardwrapping(String line, int columns) {
        String fitString = TerminalTextUtils.fitString(line, columns);
        String otherString = line.replaceFirst(Pattern.quote(fitString), "");
        if (otherString.length() > 0) {
            List<String> lines = new LinkedList<>();
            lines.add(fitString);
            Collections.addAll(lines, applyHardwrapping(otherString, columns));
            return lines.toArray(String[]::new);
        } else {
            return new String[]{fitString};
        }
    }
}
