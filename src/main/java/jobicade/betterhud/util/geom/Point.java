package jobicade.betterhud.util.geom;

import java.io.Serializable;

import jobicade.betterhud.util.geom.Size;
import net.minecraft.client.gui.ScaledResolution;

/**
 * Represents an immutable 2D point in integer precision, typically in screen
 * pixel space.
 */
public class Point implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Point ZERO = new Point();

    protected final int x, y;

    /**
     * Default constructor for points. Both X and Y will be zero.
     */
    public Point() { this(0, 0); }

    /**
     * Constructor for points.
     *
     * @param x The X coordinate.
     * @param y The Y coordinate.
     */
    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Copy constructor for points.
     * @param point The original point to copy.
     */
    public Point(Point point) {
        this.x = point.x;
        this.y = point.y;
    }

    /**
     * Conversion constructor from scaled resolutions. Uses the scaled width
     * and height to populate X and Y.
     *
     * @param resolution The resolution to get width and height from.
     */
    public Point(ScaledResolution resolution) {
        this.x = resolution.getScaledWidth();
        this.y = resolution.getScaledHeight();
    }

    /**
     * Point objects are considered equal only to other point objects
     * with the same X and Y.
     *
     * <p>{@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Point)) return false;
        Point point = (Point)obj;

        return x == point.x && y == point.y;
    }

    /**
     * Point objects are considered equal only to other point objects
     * with the same X and Y.
     *
     * <p>{@inheritDoc}
     */
    @Override
    public int hashCode() {
        return (31 + x) * 31 + y;
    }

    /**
     * Point implementation includes the values of X and Y.
     *
     * <p>{@inheritDoc}
     */
    @Override
    public String toString() {
        return String.format("%s{x: %d, y: %d}@%s",
            getClass().getName(), x, y, Integer.toHexString(hashCode()));
    }

    /**
     * Returns a point with both X and Y equal to zero. Prefer to use this over
     * creating one, as there may be a performance benefit.
     * @return A point with both X and Y equal to zero.
     */
    public static Point zero() { return ZERO; }

    // Getters and setters

    /**
     * Getter for X.
     * @return The X coordinate.
     */
    public int getX() { return x; }

    /**
     * Getter for Y.
     * @return The Y coordinate.
     */
    public int getY() { return y; }

    /**
     * Returns a point with the given X coordinate and the original Y coordinate.
     * @param x The new X coordinate.
     * @return A point with the given X coordinate and the original Y coordinate.
     */
    public Point withX(int x) { return new Point(x, y); }

    /**
     * Returns a point with the given Y coordinate and the original X coordinate.
     * @param y The new Y coordinate.
     * @return A point with the given Y coordinate and the original X coordinate.
     */
    public Point withY(int y) { return new Point(x, y); }

    // End getters and setters

    /**
     * Returns the sum of this point and another point.
     * @param x The X coordinate of the other point.
     * @param y The Y coordinate of the other point.
     * @return The sum of this point and the other point.
     */
    public Point add(int x, int y) { return new Point(this.x + x, this.y + y); }

    /**
     * @param point The other point.
     * @return The result of moving this point in the given direction
     * @see #add(int, int)
     */
    public Point add(Point point) { return new Point(x + point.x, y + point.y); }

    /**
     * Returns the difference between this point and another point.
     * @param x The X coordinate of the other point.
     * @param y The Y coordinate of the other point.
     * @return The difference between this point and the other point.
     */
    public Size sub(int x, int y) { return new Size(this.x - x, this.y - y); }

    /**
     * @param point The other point
     * @return The difference between this point and the other point.
     * @see #sub(int, int)
     */
    public Size sub(Point point) { return new Size(x - point.x, y - point.y); }

    /**
     * Returns a point with both X and Y negated.
     * @return A point with both X and Y negated.
     */
    public Point invert() { return new Point(-x, -y); }

    /**
     * Scales the point by a factor in X and Y.
     * @param xf The factor in the X axis.
     * @param yf The factor in the Y axis.
     * @return A point scaled by the given factors.
     */
    public Point scale(float xf, float yf) {
        return new Point(Math.round(x * xf), Math.round(y * yf));
    }

    /**
     * Scales the point by a factor in X and Y.
     *
     * @param factor The scaling factor.
     * @return A point scaled by the given factor.
     */
    public Point scale(Point factor) {
        return new Point(x * factor.x, y * factor.y);
    }

    /**
     * Scales the point by a factor in X and Y around a point.
     *
     * @param xf The factor in the X axis.
     * @param yf The factor in the Y axis.
     * @param x The point to scale around X coordinate.
     * @param y The point to scale around Y coordinate.
     * @return A point scaled by the given factors around the given point.
     * @see #scale(float, float)
     */
    public Point scale(float xf, float yf, int x, int y) {
        return new Point(
            Math.round((this.x - x) * xf + x),
            Math.round((this.y - y) * yf + y));
    }

    /**
     * @param xf The factor in the X axis.
     * @param yf The factor in the Y axis.
     * @param point The point.
     * @return A point scaled by the given factors around the given point.
     * @see #scale(float, float, int, int)
     */
    public Point scale(float xf, float yf, Point point) {
        return scale(xf, yf, point.x, point.y);
    }
}
