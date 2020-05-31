package jobicade.betterhud.util;

import net.minecraft.util.math.MathHelper;

/** Interface representing a slider which maintains a minimum and maximum,
 * an interval between valid values and a current value */
public interface ISlider {
    // TODO rename
    Double get();

    void set(Double value);

    /** @return the minimum of the slider's range */
    Double getMinimum();

    /** @return The maximum of the slider's range */
    Double getMaximum();

    /** @return The string to display on the background of the slider
     * given its current value */
    String getDisplayString();

    /** @return The interval between values.<br>
     * Valid values are {@link #getMinimum()} {@code + k *} {@link #getInterval()} */
    Double getInterval();

    /**
     * Processes the value so that it satisfies the
     * following requirements:
     * <ul>
     * <li>{@code getMinimum() <= value <= getMaximum()}
     * <li>{@code value - getMinimum()} is a multiple of {@code getInterval()}
     * </ul>
     *
     * @param value The value to normalize.
     * @return The normalized value.
     */
    default double normalize(double value) {
        double interval = getInterval();
        double minimum = getMinimum();

        if(interval != -1) {
            value -= minimum;
            value = Math.round(value / interval) * interval;
            value += minimum;
        }
        return MathHelper.clamp(value, minimum, getMaximum());
    }
}
