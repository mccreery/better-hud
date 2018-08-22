package tk.nukeduck.hud.util;

import static tk.nukeduck.hud.BetterHud.MANAGER;
import static tk.nukeduck.hud.BetterHud.SPACER;

import java.io.Serializable;

public final class Bounds implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final Bounds EMPTY = new Bounds(Point.ZERO);
	public static final Bounds PADDING = createPadding(SPACER);

	private final int x, y, width, height;

	public Bounds(Point size) {
		this(Point.ZERO, size);
	}

	public Bounds(int width, int height) {
		this(0, 0, width, height);
	}

	public Bounds(Point position, Point size) {
		this(position.getX(), position.getY(), size.getX(), size.getY());
	}

	public Bounds(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public Bounds(Bounds bounds) {
		this(bounds.x, bounds.y, bounds.width, bounds.height);
	}

	public static Bounds fromLeastMost(Point least, Point most) {
		return fromLeastMost(least.getX(), least.getY(), most.getX(), most.getY());
	}
	public static Bounds fromLeastMost(int left, int top, int right, int bottom) {
		return new Bounds(left, top, right - left, bottom - top);
	}

	public boolean isEmpty() {
		return this == EMPTY || width == 0 && height == 0;
	}

	@Override
	public boolean equals(Object other) {
		if(super.equals(other)) {
			return true;
		} else if(other instanceof Bounds) {
			Bounds otherBounds = (Bounds)other;

			return x == otherBounds.x && y == otherBounds.y && width == otherBounds.width && height == otherBounds.height;
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return MathUtil.hash(x, y, width, height);
	}

	// Position-size representation
	public int getX() {return x;}
	public int getY() {return y;}
	public int getWidth() {return width;}
	public int getHeight() {return height;}

	public Bounds withX(int x) {return new Bounds(x, y, width, height);}
	public Bounds withY(int y) {return new Bounds(x, y, width, height);}
	public Bounds withWidth(int width) {return new Bounds(x, y, width, height);}
	public Bounds withHeight(int height) {return new Bounds(x, y, width, height);}

	// Point methods
	public Point getPosition() {return new Point(x, y);}
	public Point getSize() {return new Point(width, height);}

	public Bounds withPosition(Point position) {return withPosition(position.getX(), position.getY());}
	public Bounds withPosition(int x, int y) {return new Bounds(x, y, width, height);}
	public Bounds withSize(Point size) {return withSize(size.getX(), size.getY());}
	public Bounds withSize(int width, int height) {return new Bounds(x, y, width, height);}

	// Low-high representation
	public int getLeft() {return x;}
	public int getTop() {return y;}
	public int getRight() {return x + width;}
	public int getBottom() {return y + height;}

	public Bounds withLeft(int left) {return new Bounds(left, y, width + x - left, height);}
	public Bounds withTop(int top) {return new Bounds(x, top, width, height + y - top);}
	public Bounds withRight(int right) {return new Bounds(x, y, right - x, height);}
	public Bounds withBottom(int bottom) {return new Bounds(x, y, width, bottom - y);}

	// Point methods
	public Point getLeast() {return new Point(x, y);}
	public Point getMost() {return new Point(x + width, y + height);}

	public Bounds withLeast(Point least) {return withLeast(least.getX(), least.getY());}
	public Bounds withLeast(int left, int top) {return new Bounds(left, top, width + x - left, height + y - top);}
	public Bounds withMost(Point most) {return withMost(most.getX(), most.getY());}
	public Bounds withMost(int right, int bottom) {return new Bounds(x, y, right - x, bottom - y);}

	/** @see #createPadding(int, int, int, int) */
	public static Bounds createPadding(int padding) {
		return createPadding(padding, padding, padding, padding);
	}

	/** @return A bounds such that the distance from the origin on each side
	 * represents padding on that side, for use with {@link #grow(Bounds)} and {@link #withInset(Bounds)} */
	public static Bounds createPadding(int left, int top, int right, int bottom) {
		return new Bounds(-left, -top, left + right, top + bottom);
	}

	/** Applies padding from {@link #createPadding(int, int, int, int)}
	 * @see #grow(int, int, int, int)
	 * @see #createPadding(int, int, int, int) */
	public Bounds grow(Bounds padding) {
		return grow(-padding.getLeft(), -padding.getTop(), padding.getRight(), padding.getBottom());
	}

	/** All sides default to {@code padding}
	 * @see #grow(int, int, int, int) */
	public Bounds grow(int padding) {
		return grow(padding, padding, padding, padding);
	}

	/** @return A bounds padded by the given amount on each side. */
	public Bounds grow(int left, int top, int right, int bottom) {
		return new Bounds(x - left, y - top, width + left + right, height + top + bottom);
	}

	public Bounds translate(Point t) {
		return withPosition(t.getX(), t.getY());
	}

	public Bounds translate(int x, int y) {
		return withPosition(this.x + x, this.y + y);
	}

	public Bounds shift(Direction direction, int x) {
		return withPosition(getPosition().shiftedBy(direction, x));
	}

	public Bounds scale(float f) {
		return scale(f, f);
	}
	public Bounds scale(float fx, float fy) {
		if(x == 1 && y == 1)
			return this;
		else
			return new Bounds(Math.round(x * fx), Math.round(y * fy),
				Math.round(width * fx), Math.round(height * fy));
	}

	public boolean contains(Point point) {return contains(point.getX(), point.getY());}
	public boolean contains(int x, int y) {
		return x >= this.x && x < this.x + width
			&& y >= this.y && y < this.y + height;
	}

	/** Applies common positioning transformations at once
	 *
	 * @param anchor The screen direction to anchor to
	 * @param offset The offset from the anchor
	 * @param alignment The alignment of the bounds around the transformed origin
	 * @return A new bounds with all the transformations applied
	 *
	 * @see #getAnchor(Direction)
	 * @see #align(Point, Direction) */
	public Bounds positioned(Direction anchor, Point offset, Direction alignment) {
		return align(MANAGER.getScreen().getAnchor(anchor).add(offset), alignment);
	}

	/** @param direction The anchor direction
	 * @return The given anchor point in this bounds */
	public Point getAnchor(Direction direction) {
		return getPosition().add(getSize().scale(direction.getColumn() / 2f, direction.getRow() / 2f));
	}

	/** Aligns this bounds around the anchor with the given alignment
	 * @param anchor The anchor
	 * @param alignment The alignment around the anchor
	 * 
	 * @return A new bounds with the alignment applied */
	public Bounds align(Point anchor, Direction alignment) {
		return withPosition(anchor.sub(withPosition(Point.ZERO).getAnchor(alignment)));
	}

	/** {@code outside} defaults to {@code false}
	 * @see #anchor(Bounds, Direction, boolean) */
	public Bounds anchor(Bounds container, Direction alignment) {
		return anchor(container, alignment, false);
	}

	/** Anchors this bounds to the anchor for the given direction on the container.
	 * <p>The final position will touch the inside edge of the container
	 * @param container The container to anchor to the edge of
	 * @param alignment The anchor direction
	 *
	 * @return A new bounds with the anchor applied */
	public Bounds anchor(Bounds container, Direction alignment, boolean outside) {
		return align(container.getAnchor(alignment), (outside ? alignment.mirror() : alignment));
	}
}
