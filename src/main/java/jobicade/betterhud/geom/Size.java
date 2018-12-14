package jobicade.betterhud.geom;

import net.minecraft.client.gui.ScaledResolution;

/**
 * A special type of {@link Point} which represents a size, or difference
 * between two points.
 *
 * @see Point
 */
public class Size extends Point {
    private static final long serialVersionUID = 1L;
    private static final Size ZERO = new Size();

    /**
     * Default constructor for sizes. Both width and height will be zero.
     */
    public Size() { super(); }

    /**
     * Constructor for sizes.
     *
     * @param width The width.
     * @param height The height.
     */
    public Size(int width, int height) {
        super(width, height);
    }

    /**
     * Copy/conversion constructor for sizes.
     * @param point The original point to copy.
     */
    public Size(Point point) {
        super(point);
    }

    /**
     * Conversion constructor from scaled resolutions. Uses the scaled width
     * and height to populate width and height.
     *
     * @param resolution The resolution to get width and height from.
     */
    public Size(ScaledResolution resolution) {
        super(resolution);
    }

    /**
     * Size objects are considered equal only to other size objects with the
     * same width and height.
     *
     * <p>{@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        return obj instanceof Size && super.equals(obj);
    }

    /**
     * Size implementation includes the values of width and height.
     *
     * <p>{@inheritDoc}
     */
    @Override
    public String toString() {
        return String.format("%s{width: %d, height: %d}@%s",
            getClass().getName(), x, y, Integer.toHexString(hashCode()));
    }

    /**
     * Returns a size with both width and height equal to zero. Prefer to use
     * this over creating one, as there may be a performance benefit.
     * @return A size with both width and height equal to zero.
     */
    public static Size zero() { return ZERO; }

    // Getters and setters

    /**
     * Getter for width.
     * @return The width.
     */
    public int getWidth() { return getX(); }

    /**
     * Getter for height.
     * @return The height.
     */
    public int getHeight() { return getY(); }

    @Override
    public Size withX(int x) { return new Size(x, y); }

    @Override
    public Size withY(int y) { return new Size(x, y); }

    /**
     * Returns a size with the given width and the original height.
     * @param width The new width.
     * @return A size with the given width and the original height.
     */
    public Size withWidth(int width) { return withX(width); }

    /**
     * Returns a size with the given height and the original width.
     * @param width The new width.
     * @return A size with the given height and the original width.
     */
    public Size withHeight(int height) { return withY(height); }

    // End getters and setters

    /**
     * Returns the sum of this size and another point.
     * @param x The X coordinate of the other point.
     * @param y The Y coordinate of the other point.
     * @return The sum of this size and the other point.
     */
    public Size add(int x, int y) { return new Size(this.x + x, this.y + y); }

    /**
     * @param point The other point.
     * @see #add(int, int)
     */
    public Size add(Point point) { return new Size(x + point.x, y + point.y); }

    /**
     * Returns a size with both width and height negated.
     * @return A point with both width and height negated.
     */
    @Override
    public Size invert() { return new Size(-x, -y); }

    /**
     * Scales the point by a factor.
     * @param factor The factor.
     * @return A point scaled by the given factor.
     */
    @Override
    public Size scale(float factor) {
        return new Size(Math.round(x * factor), Math.round(y * factor));
    }

    /**
     * Scales the size by a factor in X and Y.
     * @param xf The factor in the X axis.
     * @param yf The factor in the Y axis.
     * @return A size scaled by the given factors.
     */
    @Override
    public Size scale(float xf, float yf) {
        return new Size(Math.round(x * xf), Math.round(y * yf));
    }

    /**
     * Scales the point by a factor in X and Y.
     *
     * @param factor The scaling factor.
     * @return A point scaled by the given factor.
     */
    @Override
    public Size scale(Point factor) {
        return new Size(x * factor.x, y * factor.y);
    }

    /**
     * Scales the point by a factor in X and Y around a point.
     *
     * @param xf The factor in the X axis.
     * @param yf The factor in the Y axis.
     * @param x The point to scale around X coordinate.
     * @param y The point to scale around Y coordinate.
     * @return A size scaled by the given factors around the given point.
     * @see #scale(float, float)
     */
    @Override
    public Size scale(float xf, float yf, int x, int y) {
        return new Size(
            Math.round((this.x - x) * xf + x),
            Math.round((this.y - y) * yf + y));
    }

    /**
     * @param xf The factor in the X axis.
     * @param yf The factor in the Y axis.
     * @param point The point.
     * @return A size scaled by the given factors around the given point.
     * @see #scale(float, float, int, int)
     */
    @Override
    public Size scale(float xf, float yf, Point point) {
        return scale(xf, yf, point.x, point.y);
    }
}
