package jobicade.betterhud.geom;

import java.io.Serializable;

/**
 * Represents an immutable axis-aligned rectangle in integer precision,
 * typically in screen pixel space. Rectangles with nonpositive width or height
 * are considered empty, and rectangles with negative width or height are
 * considered denormal and may behave in strange ways. For example, union and
 * intersection behave like each other when operating on denormal rectangles.
 *
 * <p>Use {@link #normalize()} to ensure rectangles are normal.
 */
public final class Rect implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Rect EMPTY = new Rect(0, 0, 0, 0);

    private final int x, y, width, height;

    /**
     * Default constructor for rectangles. All values will be zero.
     */
    public Rect() { this(0, 0, 0, 0); }

    /**
     * Constructor for rectangles. Position defaults to zero.
     *
     * @param width The width of the rectangle.
     * @param height The height of the rectangle.
     * @see #Rect(int, int, int, int)
     */
    public Rect(int width, int height) {
        this(0, 0, width, height);
    }

    /**
     * Constructor for rectangles.
     *
     * @param x The leftmost X coordinate. Same as "left".
     * @param y The topmost Y coordinate. Same as "top".
     * @param width The width of the rectangle.
     * @param height The height of the rectangle.
     */
    public Rect(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /**
     * Constructor for rectangles. Position defaults to zero.
     *
     * @param size The size of the rectangle.
     * @see #Rect(Point, Size)
     */
    public Rect(Point size) {
        this(Point.zero(), size);
    }

    /**
     * Constructor for rectangles.
     *
     * @param position The top left position. Same as "least".
     * @param size The size of the rectangle.
     */
    public Rect(Point position, Size size) {
        this.x = position.getX();
        this.y = position.getY();
        this.width = size.getWidth();
        this.height = size.getHeight();
    }

    /**
     * Constructor for rectangles.
     *
     * @param least The top left coordinate. Same as "position".
     * @param most The bottom right coordinate.
     */
    public Rect(Point least, Point most) {
        this(least, most.sub(least));
    }

    /**
     * Copy constructor for rectangles.
     * @param rect The original rectangle to copy.
     */
    public Rect(Rect rect) {
        this.x = rect.x;
        this.y = rect.y;
        this.width = rect.width;
        this.height = rect.height;
    }

    /**
     * Rectangle objects are considered equal only to other rectangle objects
     * with the same dimensions.
     *
     * <p>{@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Rect) {
            Rect rectangle = (Rect)obj;
            return x == rectangle.x && y == rectangle.y &&
                width == rectangle.width && height == rectangle.height;
        }
        return super.equals(obj);
    }

    /**
     * Rectangle objects are considered equal only to other rectangle objects
     * with the same dimensions.
     *
     * <p>{@inheritDoc}
     */
    @Override
    public int hashCode() {
        return (((31 + x) * 31 + y) * 31 + width) * 31 + height;
    }

    /**
     * Rectangle implementation includes the dimensions.
     *
     * <p>{@inheritDoc}
     */
    @Override
    public String toString() {
        return String.format("%s{x: %d, y: %d, width: %d, height: %d}@%s",
            getClass().getName(), x, y, width, height, Integer.toHexString(System.identityHashCode(this)));
    }

    // Factories

    /**
     * Returns an empty rectangle. Prefer to use this over creating one,
     * as there may be a performance benefit.
     * @return An empty rectangle.
     */
    public static Rect empty() { return EMPTY; }

    /**
     * Returns a rectangle representing padding with the origin zero. The
     * distance of the rectangle from any side of zero is the padding on that
     * side.
     *
     * @param left The left padding.
     * @param top The top padding.
     * @param right The right padding.
     * @param bottom The bottom padding.
     * @return A rectangle representing padding.
     * @see #grow(Rect)
     */
    public static Rect createPadding(int left, int top, int right, int bottom) { return new Rect(-left, -top, left + right, top + bottom); }

    /**
     * @param padding The padding for all sides.
     * @return A rectangle representing padding.
     * @see #createPadding(int, int, int, int)
     */
    public static Rect createPadding(int padding) { return new Rect(-padding, -padding, 2 * padding, 2 * padding); }

    // Getters and setters

    /**
     * @return The leftmost X coordinate of the rectangle. Same as "left".
     * @see #getLeft()
     */
    public int getX() { return x; }

    /**
     * @return The topmost Y coordinate of the rectangle. Same as "top".
     * @see #getTop()
     */
    public int getY() { return y; }

    /**
     * @return The width of the rectangle.
     */
    public int getWidth() { return width; }

    /**
     * @return The height of the rectangle.
     */
    public int getHeight() { return height; }

    /**
     * @return The leftmost X coordinate of the rectangle. Same as "x".
     * @see #getX()
     */
    public int getLeft() { return x; }

    /**
     * @return The topmost Y coordinate of the rectangle. Same as "y".
     * @see #getY()
     */
    public int getTop() { return y; }

    /**
     * @return The rightmost X coordinate of the rectangle.
     */
    public int getRight() { return x + width; }

    /**
     * @return The bottommost Y coordinate of the rectangle.
     */
    public int getBottom() { return y + height; }

    /**
     * Returns a near identical rectangle with the given leftmost X coordinate.
     * This method differs from {@link #withLeft(int)} in that the width of the
     * rectangle remains the same. There is no guarantee that the returned
     * rectangle will be distinct from {@code this}.
     *
     * <p>Prefer {@link #move(int, int)} to change both X and Y.
     *
     * @param x The new leftmost X coordinate.
     * @return A near identical rectangle with the given leftmost X coordinate.
     */
    public Rect withX(int x) { return new Rect(x, y, width, height); }

    /**
     * Returns a near identical rectangle with the given leftmost Y coordinate.
     * This method differs from {@link #withTop(int)} in that the height of the
     * rectangle remains the same. There is no guarantee that the returned
     * rectangle will be distinct from {@code this}.
     *
     * <p>Prefer {@link #move(int, int)} to change both X and Y.
     *
     * @param y The new leftmost Y coordinate.
     * @return A near identical rectangle with the given leftmost Y coordinate.
     */
    public Rect withY(int y) { return new Rect(x, y, width, height); }

    /**
     * Returns a near identical rectangle with the given width. The top left
     * position of the rectangle remains the same. There is no guarantee that
     * the returned rectangle will be distinct from {@code this}.
     *
     * <p>Prefer {@link #resize(int, int)} to change both width and height.
     *
     * @param width The new width.
     * @return A near identical rectangle with the given width.
     */
    public Rect withWidth(int width) { return new Rect(x, y, width, height); }

    /**
     * Returns a near identical rectangle with the given height. The top left
     * position of the rectangle remains the same. There is no guarantee that
     * the returned rectangle will be distinct from {@code this}.
     *
     * <p>Prefer {@link #resize(int, int)} to change both width and height.
     *
     * @param height The new height.
     * @return A near identical rectangle with the given height.
     */
    public Rect withHeight(int height) { return new Rect(x, y, width, height); }

    /**
     * Returns a near identical rectangle with the given leftmost X coordinate.
     * This method differs from {@link #withX(int)} in that the rightmost
     * X coordinate remains the same. There is no guarantee that the returned
     * rectangle will be distinct from {@code this}.
     *
     * <p>Prefer {@link #withLeast(int, int)} to change both X and Y.
     *
     * @param left The new leftmost X coordinate
     * @return A near copy of this rectangle with the given leftmost X coordinate
     */
    public Rect withLeft(int left) { return new Rect(left, y, x + width - left, height); }

    /**
     * Returns a near identical rectangle with the given topmost Y coordinate.
     * This method differs from {@link #withY(int)} in that the bottommost
     * Y coordinate remains the same. There is no guarantee that the returned
     * rectangle will be distinct from {@code this}.
     *
     * <p>Prefer {@link #withLeast(int, int)} to change both X and Y.
     *
     * @param top The new topmost Y coordinate
     * @return A near copy of this rectangle with the given topmost Y coordinate
     */
    public Rect withTop(int top) { return new Rect(x, top, width, y + height - top); }

    /**
     * Returns a near identical rectangle with the given rightmost X coordinate.
     * The top left position of the rectangle remains the same. There is no
     * guarantee that the returned rectangle will be distinct from {@code this}.
     *
     * <p>Prefer {@link #withMost(int, int)} to change both width and height.
     *
     * @param right The new rightmost X coordinate.
     * @return A near identical rectangle with the given rightmost X coordinate.
     */
    public Rect withRight(int right) { return new Rect(x, y, right - x, height); }

    /**
     * Returns a near identical rectangle with the given bottommost Y coordinate.
     * The top left position of the rectangle remains the same. There is no
     * guarantee that the returned rectangle will be distinct from {@code this}.
     *
     * <p>Prefer {@link #withMost(int, int)} to change both width and height.
     *
     * @param bottom The new bottommost Y coordinate.
     * @return A near identical rectangle with the given bottommost Y coordinate.
     */
    public Rect withBottom(int bottom) { return new Rect(x, y, width, bottom - y); }

    /**
     * @return The top left position of the rectangle. Same as "least".
     * @see #getLeast()
     */
    public Point getPosition() { return new Point(x, y); }

    /**
     * @return The size of the rectangle.
     */
    public Size getSize() { return new Size(width, height); }

    /**
     * @return The top left coordinate of the rectangle. Same as "position".
     * @see #getPosition()
     */
    public Point getLeast() { return new Point(x, y); }

    /**
     * @return The bottom right coordinate of the rectangle.
     */
    public Point getMost() { return new Point(x + width, y + height); }

    /**
     * Returns a near identical rectangle with the given top left coordinate.
     * This method differs from {@link #move(int, int)} in that the bottom right
     * coordinate remains the same. There is no guarantee that the returned
     * rectangle will be distinct from {@code this}.
     *
     * <p>Prefer {@link #withLeft(int)} or {@link #withTop(int)} to change
     * either left or top, but not both.
     *
     * @param left The new leftmost X coordinate.
     * @param top The new topmost Y coordinate.
     * @return A near identical rectangle with the given top left coordinate.
     */
    public Rect withLeast(int left, int top) { return new Rect(left, top, x + width - left, y + height - top); }

    /**
     * @param least The new top left coordinate.
     * @return A near identical rectangle with the given top left coordinate.
     * @see #withLeast(int, int)
     */
    public Rect withLeast(Point least) { return new Rect(least.getX(), least.getY(), x + width - least.getX(), y + height - least.getY()); }

    /**
     * Returns a near identical rectangle with the given bottom right coordinate.
     * The top left coordinate remains the same. There is no guarantee that the
     * returned rectangle will be distinct from {@code this}.
     *
     * <p>Prefer {@link #withRight(int)} or {@link #withBottom(int)} to change
     * either right or bottom, but not both.
     *
     * @param right The new rightmost X coordinate.
     * @param bottom The new bottommost Y coordinate.
     * @return A near identical rectangle with the given bottom right coordinate.
     */
    public Rect withMost(int right, int bottom) { return new Rect(x, y, right - x, bottom - y); }

    /**
     * @param most The new bottom right coordinate.
     * @return A near identical rectangle with the given bottom right coordinate.
     * @see #withMost(int, int)
     */
    public Rect withMost(Point most) { return new Rect(x, y, most.getX() - x, most.getY() - y); }

    // More common setters

    /**
     * Returns a near identical rectangle with the given top left position.
     * This method differs from {@link #withLeast(int, int)} in that the size
     * remains the same. There is no guarantee that the returned rectangle will
     * be distinct from {@code this}.
     *
     * <p>Prefer {@link #withX(int)} or {@link #withY(int)} to change
     * either X or Y, but not both.
     *
     * @param x The new leftmost X position.
     * @param y The new topmost Y position.
     * @return A near identical rectangle with the given top left position.
     */
    public Rect move(int x, int y) { return new Rect(x, y, width, height); }

    /**
     * @param position The new top left position.
     * @return A near identical rectangle with the given top left position.
     * @see #move(int, int)
     */
    public Rect move(Point position) { return new Rect(position.getX(), position.getY(), width, height); }

    /***
     * Returns a near identical rectangle with the given size. The top left
     * position remains the same. There is no guarantee that the returned
     * rectangle will be distinct from {@code this}.
     *
     * <p>Prefer {@link #withWidth(int)} or {@link #withHeight(int)} to change
     * either width or height, but not both.
     *
     * @param width The new width of the rectangle.
     * @param height The new height of the rectangle.
     * @return A near identical rectangle with the given size.
     */
    public Rect resize(int width, int height) { return new Rect(x, y, width, height); }

    /**
     * @param size The new size of the rectangle.
     * @return A near identical rectangle with the given size.
     * @see #resize(int, int)
     */
    public Rect resize(Point size) { return new Rect(x, y, size.getX(), size.getY()); }

    // Common operations

    /**
     * Returns the result of translating this rectangle. There is no guarantee
     * that the returned rectangle will be distinct from {@code this}.
     *
     * @param x The X coordinate offset.
     * @param y The Y coordinate offset.
     * @return The result of translating this rectangle by X and Y.
     */
    public Rect translate(int x, int y) { return new Rect(this.x + x, this.y + y, width, height); }

    /**
     * @param offset The offset.
     * @return The result of translating this rectangle by X and Y.
     * @see #translate(int, int)
     */
    public Rect translate(Point offset) { return new Rect(this.x + offset.getX(), this.y + offset.getY(), width, height); }

    /**
     * As {@link #translate(int, int)}, but inverts the translation.
     *
     * @param x The X coordinate offset.
     * @param y The Y coordinate offset.
     * @return The result of translating this rectangle by X and Y.
     */
    public Rect untranslate(int x, int y) { return new Rect(this.x - x, this.y - y, width, height); }

    /**
     * @param offset The offset.
     * @return The result of translating this rectangle by X and Y.
     * @see #untranslate(int, int)
     */
    public Rect untranslate(Point offset) { return new Rect(this.x - offset.getX(), this.y - offset.getY(), width, height); }

    /**
     * Adds the given padding distance to each side of the rectangle.
     *
     * @param left The left padding.
     * @param top The top padding.
     * @param right The right padding.
     * @param bottom The bottom padding.
     * @return The result of padding the rectangle by the given distances.
     */
    public Rect grow(int left, int top, int right, int bottom) { return new Rect(this.x - left, this.y - top, this.width + left + right, this.height + top + bottom); }

    /**
     * Adds each pair of corresponding coordinates between the two rectangles.
     * This allows for padding to be represented by a normal rectangle,
     * and inset to be represented by a denormal rectangle.
     *
     * @param padding The padding rectangle.
     * @return The result of the sum of each pair of corresponding coordinates
     * between both rectangles.
     * @see #createPadding(int, int, int, int)
     */
    public Rect grow(Rect padding) { return new Rect(this.x + padding.x, this.y + padding.y, this.width + padding.width, this.height + padding.height); }

    /**
     * Adds a constant padding distance to each side of the rectangle.
     *
     * @param padding The padding distance.
     * @return The result of padding the rectangle by the distance.
     */
    public Rect grow(int padding) { return new Rect(this.x - padding, this.y - padding, this.width + padding * 2, this.height + padding * 2); }

    // Boolean operations

    /**
     * Tests whether the given point is inside this rectangle. Points are
     * considered inside using the top left coordinate as an inclusive lower
     * bound and the bottom right coordinate as an exclusive upper bound.
     *
     * @param x The X coordinate of the point.
     * @param y The Y coordinate of the point.
     * @return {@code true} if the point is inside this rectangle.
     */
    public boolean contains(int x, int y) { return x >= this.x && x < this.x + width && y >= this.y && y < this.y + height; }

    /**
     * @param point The point.
     * @return {@code true} if the point is inside this rectangle.
     * @see #contains(int, int)
     */
    public boolean contains(Point point) { return point.getX() >= x && point.getX() < x + width && point.getY() >= y && point.getY() < y + height; }

    /**
     * Tests whether the rectangle is empty. An empty rectangle is any
     * rectangle which contains no points. Equivalently, empty rectangles are
     * either denormal or have a width or height of zero.
     *
     * @return {@code true} if the rectangle is empty.
     * @see #isNormal()
     */
    public boolean isEmpty() { return width <= 0 || height <= 0; }

    /**
     * Tests whether the rectangle is normal. A normal rectangle has a strictly
     * positive width and height. Denormal rectangles behave differently for
     * certain operations, see {@link Rect} for details.
     *
     * @return {@code true} if the rectangle is normal.
     */
    public boolean isNormal() { return width > 0 && height > 0; }

    /**
     * Returns an equivalent rectangle that is normal. The result must behave
     * normally for all operations. See {@link Rect} for details.
     *
     * @return An equivalent rectangle that is normal.
     */
    public Rect normalize() {
        if(isNormal()) return this;
        int x = this.x, y = this.y, width = this.width, height = this.height;

        if(width < 0) {
            x += width;
            width = -width;
        }
        if(height < 0) {
            y += height;
            height = -height;
        }
        return new Rect(x, y, width, height);
    }

    /**
     * Returns the result of the union operation over two rectangles. The result
     * must contain at least all points in either rectangle.
     *
     * <p>This function behaves like {@link #intersection(Rect)} on
     * denormal rectangles.
     *
     * @param rect The other rectangle to union with.
     * @return The result of the union operation over two rectangles.
     */
    public Rect union(Rect rect) {
        int x = Math.min(this.x, rect.x);
        int y = Math.min(this.y, rect.y);
        int width = Math.max(this.x + this.width, rect.x + rect.width) - x;
        int height = Math.max(this.y + this.height, rect.x + rect.height) - y;

        return new Rect(x, y, width, height);
    }

    /**
     * Returns the result of the intersection operation over two rectangles.
     * The result must contain exactly all points in both rectangles.
     *
     * <p>This function behaves like {@link #intersection(Rect)} on
     * denormal rectangles.
     *
     * @param rect The other rectangle to union with.
     * @return The result of the union operation over two rectangles.
     */
    public Rect intersection(Rect rect) {
        int x = Math.max(this.x, rect.x);
        int y = Math.max(this.y, rect.y);
        int width = Math.min(this.x + this.width, rect.x + rect.width) - x;
        int height = Math.min(this.y + this.height, rect.y + rect.height) - y;

        return new Rect(x, y, width, height);
    }

    // Direction functions

    /**
     * Returns the anchor point in this rectangle for a direction.
     * For example, {@link Direction#NORTH_WEST} will return the top left
     * coordinate.
     *
     * @param direction The anchor direction.
     * @return The anchor point in this rectangle for the given direction.
     */
    public Point getAnchor(Direction direction) {
        return new Point(
            x + width * direction.getCol() / 2,
            y + height * direction.getRow() / 2);
    }

    /**
     * Aligns the given anchor direction of the rectangle with an anchor point.
     * For example, {@link Direction#NORTH_WEST} would align the northwest
     * corner of the rectangle with the anchor point. The size of the rectangle
     * remains the same.
     *
     * @param anchor The anchor point.
     * @param alignment The anchor direction within the rectangle.
     * @return The result of aligning the anchor direction to the anchor point.
     */
    public Rect align(Point anchor, Direction alignment) {
        return move(anchor.sub(move(Point.zero()).getAnchor(alignment)));
    }

    /**
     * Anchors the rectangle inside or outside another rectangle. The anchor
     * for the given direction in the result will be the same as the same anchor
     * in the container. The size of the rectangle remains the same.
     *
     * <p>If outside, the anchor direction will be mirrored for this
     * rectangle, so the result will always sit on the outside edge of the
     * container.
     *
     * @param container The container to anchor to.
     * @param direction The direction of the anchor point.
     * @param outside {@code true} to align the opposite point on this
     * rectangle to the anchor point on the container.
     * @return The rectangle anchored inside or outside another rectangle.
     */
    public Rect anchor(Rect container, Direction direction, boolean outside) {
        return align(container.getAnchor(direction), outside ? direction.mirror() : direction);
    }

    /**
     * @see #anchor(Rect, Direction, boolean)
     */
    public Rect anchor(Rect container, Direction direction) {
        return anchor(container, direction, false);
    }

    /**
     * Returns a rectangle with all the coordinates negated.
     * @return A rectangle with all the coordinates negated.
     */
    public Rect invert() { return new Rect(-x, -y, -width, -height); }

    /**
     * Scales the rectangle by a factor.
     *
     * @param factor The scaling factor.
     * @return A rectangle scaled by the given factor.
     */
    public Rect scale(int factor) {
        return new Rect(x * factor, y * factor, width * factor, height * factor);
    }

    /**
     * Scales the rectangle by a factor in X and Y.
     * @param xf The factor in the X axis.
     * @param yf The factor in the Y axis.
     * @return A rectangle scaled by the given factors.
     */
    public Rect scale(float xf, float yf) {
        return new Rect(Math.round(x * xf), Math.round(y * yf), Math.round(width * xf), Math.round(height * yf));
    }

    /**
     * Scales the rectangle by a factor in X and Y.
     *
     * @param factor The scaling factor.
     * @return A rectangle scaled by the given factor.
     */
    public Rect scale(Point factor) {
        return new Rect(x * factor.x, y * factor.y, width * factor.x, height * factor.y);
    }

    /**
     * Scales the rectangle by a factor in X and Y around a point.
     *
     * @param xf The factor in the X axis.
     * @param yf The factor in the Y axis.
     * @param x The point to scale around X coordinate.
     * @param y The point to scale around Y coordinate.
     * @return A rectangle scaled by the given factors around the given point.
     * @see #scale(float, float)
     */
    public Rect scale(float xf, float yf, int x, int y) {
        return new Rect(
            Math.round((this.x - x) * xf + x),
            Math.round((this.y - y) * yf + y),
            Math.round(this.width * xf),
            Math.round(this.height * yf));
    }

    /**
     * @param xf The factor in the X axis.
     * @param yf The factor in the Y axis.
     * @param point The point.
     * @return A rectangle scaled by the given factors around the given point.
     * @see #scale(float, float, int, int)
     */
    public Rect scale(float xf, float yf, Point point) {
        return scale(xf, yf, point.x, point.y);
    }
}
