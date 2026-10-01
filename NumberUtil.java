package it.maurof00.xeonkitpvp.util;

import java.text.DecimalFormat;

public final class NumberUtil {
    private static final DecimalFormat DF = new DecimalFormat("0.00");
    private NumberUtil() { }
    public static String decimal(double value) { return DF.format(value); }
    public static String durationSeconds(long seconds) {
        if (seconds < 60) return seconds + "s";
        long m = seconds / 60;
        long s = seconds % 60;
        if (m < 60) return m + "m " + s + "s";
        long h = m / 60;
        m %= 60;
        return h + "h " + m + "m";
    }
}
