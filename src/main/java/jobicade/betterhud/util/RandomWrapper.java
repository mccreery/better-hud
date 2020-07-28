package jobicade.betterhud.util;

import java.util.Random;

import jobicade.betterhud.geom.Point;
import jobicade.betterhud.geom.Rect;

public class RandomWrapper {
    private final Random random;

    public RandomWrapper(Random random) {
        this.random = random;
    }

    public Random getRandom() {
        return random;
    }

    /**
     * @see Random#nextInt()
     */
    public int nextInt() {
        return random.nextInt();
    }

    /**
     * @see Random#nextInt(int)
     */
    public int nextInt(int bound) {
        return random.nextInt(bound);
    }

    /**
     * Generates a random int between two bounds. The bounds must not be
     * equal.
     * @see Random#nextInt(int)
     */
    public int nextInt(int inclusiveBound, int exclusiveBound) {
        if (inclusiveBound == exclusiveBound) {
            throw new IllegalArgumentException("bounds must not be equal");
        } else if (inclusiveBound < exclusiveBound) {
            return inclusiveBound + random.nextInt(exclusiveBound - inclusiveBound);
        } else { // inclusiveBound > exclusiveBound
            return inclusiveBound - random.nextInt(inclusiveBound - exclusiveBound);
        }
    }

    /**
     * @see Random#nextFloat()
     */
    public float nextFloat() {
        return random.nextFloat();
    }

    /**
     * Generates a random float between two bounds. The bounds must not be
     * equal.
     * @see Random#nextFloat()
     */
    public float nextFloat(float inclusiveBound, float exclusiveBound) {
        if (inclusiveBound == exclusiveBound) {
            throw new IllegalArgumentException("bounds must not be equal");
        } else {
            return MathUtil.lerp(inclusiveBound, exclusiveBound, random.nextFloat());
        }
    }

    /**
     * Generates a random point inside a rectangle. The rectangle must not have
     * equal left and right or top and bottom sides.
     */
    public Point nextPoint(Rect bounds) {
        return new Point(
            nextInt(bounds.getLeft(), bounds.getRight()),
            nextInt(bounds.getTop(), bounds.getBottom())
        );
    }

    /**
     * Runs a random binomial trial.
     *
     * @param p The probability of success.
     * @return {@code true} for success.
     */
    public boolean nextTrial(float p) {
        return random.nextFloat() < p;
    }
}
