package jobicade.betterhud.util;

import java.text.DecimalFormat;

public final class MathUtil {
    private MathUtil() {}

    /**
     * Performs linear interpolation.
     *
     * @param a The endpoint at {@code t == 0}.
     * @param b The endpoint at {@code t == 1}.
     * @param t Interpolation parameter. Can be outside {@code [0,1]}.
     */
    public static float lerp(float a, float b, float t) {
        // Higher precision than {a + (b - a) * t}
        return a * (1.0f - t) + b * t;
    }

    /**
     * Performs integer division rounding towards positive infinity.
     */
    public static int ceilDiv(int x, int y) {
        // Adapted from java.lang.Math.floorDiv
        int r = x / y;

        // if the signs are the same and modulo not zero, round up
        if ((x ^ y) >= 0 && (r * y != x)) {
            ++r;
        }
        return r;
    }

    /**
     * @return The least integer that is a multiple of {@code multiple} and
     * greater than or equal to {@code x}.
     */
    public static int ceil(int x, int multiple) {
        if (multiple == 0) {
            throw new IllegalArgumentException("multiple must not be 0");
        } else {
            multiple = Math.abs(multiple);
            return ceilDiv(x, multiple) * multiple;
        }
    }

    /**
     * Formats a number to a maximum number of decimal places.
     * @param x The number to format.
     * @param n The number of decimal places.
     * @return {@code x} formatted to a maximum of {@code n} decimal places.
     */
    public static String formatToPlaces(double x, int n) {
        DecimalFormat format = new DecimalFormat();
        format.setMaximumFractionDigits(n);
        return format.format(x);
    }
}
