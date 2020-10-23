package eu.timerertim.lanterna.extras.test.utils;

import eu.timerertim.lanterna.extras.utils.TextUtils;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

public class TextUtilsTest {
    @Test
    public void applySoftwrapping() {
        String originalText = "This text will be softwrapped here";
        String[] resultText = new String[]{"This text", "will be", "softwrapp", "ed here"};
        assertArrayEquals("Text should be softwrapped", resultText, TextUtils.applySoftwrapping(originalText, 9));

        originalText = "This is another softwrapped text with a column width of 12";
        resultText = new String[]{"This is", "another", "softwrapped", "text with a", "column width", "of 12"};
        assertArrayEquals("Text should be softwrapped - width 12", resultText, TextUtils.applySoftwrapping(originalText, 12));
    }

    @Test
    public void applyHardwrapping() {
        String originalText = "This text will be hardwrapped here";
        String[] resultText = new String[]{"This text", " will be ", "hardwrapp", "ed here"};
        assertArrayEquals("Text should be hardwrapped normally", resultText, TextUtils.applyHardwrapping(originalText, 9));
    }
}
