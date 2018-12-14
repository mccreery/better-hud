package jobicade.betterhud.util.geom;

import net.minecraft.client.gui.ScaledResolution;

/**
 * A special type of point that represents a difference
 * or size between two points, for example the size of a rectangle.
 *
 * @see Rect
 */
public class Size extends Point {
    private static final long serialVersionUID = 1L;

    private static final Size ZERO = new Size();

    /**
     * Default constructor for sizes.
     * @see Point#Point()
     */
    public Size() { super(); }

    /**
     * Constructor for sizes.
     *
     * @param width The width of the size.
     * @param height The height of the size.
     * @see Point#Point(int, int)
     */
    public Size(int width, int height) {
        super(width, height);
    }

    /**
     * Copy/conversion constructor for sizes.
     *
     * @param point The point to copy.
     * @see Point#Point(Point)
     */
    public Size(Point point) {
        super(point);
    }

    /**
     * Conversion constructor for sizes.
     *
     * @param resolution The resolution to convert.
     * @see Point#Point(ScaledResolution)
     */
    public Size(ScaledResolution resolution) {
        super(resolution);
    }

    /**
     * Returns a size with both X and Y equal to zero.
     *
     * @return A size with both X and Y equal to zero.
     * @see Point#zero()
     */
    public static Size zero() {
        return ZERO;
    }

    /**
     * Size implementation includes the values of width and height.
     *
     * <p>{@inheritDoc}
     */
    @Override
    public String toString() {
        return String.format("%s{width: %d, height: %d}@%s",
            getClass().getName(), getX(), getY(), Integer.toHexString(hashCode()));
    }

    /**
     * Getter for width.
     *
     * @return The width of this size. Same as "x".
     * @see #getX()
     */
    public int getWidth() {
        return getX();
    }

    /**
     * Getter for height.
     *
     * @return The height of this size. Same as "y".
     * @see #getY()
     */
    public int getHeight() {
        return getY();
    }

    /**
     * Returns a size with a new width. Height does not change.
     *
     * @param width The new width.
     * @return A size with a new width and the existing height.
     * @see #withX(int)
     */
    public Point withWidth(int width) {
        return withX(width);
    }

    /**
     * Returns a size with a new height. Width does not change.
     *
     * @param height The new height.
     * @return A size with a new height and the existing width.
     * @see #withY(int)
     */
    public Point withHeight(int height) {
        return withY(height);
    }
}
