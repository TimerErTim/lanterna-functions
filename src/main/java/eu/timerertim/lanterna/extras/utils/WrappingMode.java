package eu.timerertim.lanterna.extras.utils;

public enum WrappingMode {
    NONE((line, columns) -> new String[]{line}),
    SOFTWRAPPING(TextUtils::applySoftwrapping),
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
