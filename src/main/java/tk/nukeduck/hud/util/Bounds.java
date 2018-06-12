package tk.nukeduck.hud.util;

import static tk.nukeduck.hud.BetterHud.MANAGER;
import static tk.nukeduck.hud.BetterHud.SPACER;

import java.io.Serializable;

public final class Bounds implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final Bounds EMPTY = new Bounds();
	public static final Bounds PADDING = createPadding(SPACER);

	private final int x, y, width, height;

	public Bounds() {
		this(0, 0, 0, 0);
	}

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

	public static boolean isEmpty(Bounds bounds) {
		return bounds == null || bounds.isEmpty();
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
	 * represents padding on that side, for use with {@link #withPadding(Bounds)} and {@link #withInset(Bounds)} */
	public static Bounds createPadding(int left, int top, int right, int bottom) {
		return new Bounds(-left, -top, left + right, top + bottom);
	}

	/** Applies padding from {@link #createPadding(int, int, int, int)}
	 * @see #withPadding(int, int, int, int)
	 * @see #createPadding(int, int, int, int) */
	public Bounds withPadding(Bounds padding) {
		return withPadding(-padding.getLeft(), -padding.getTop(), padding.getRight(), padding.getBottom());
	}

	/** All sides default to {@code padding}
	 * @see #withPadding(int, int, int, int) */
	public Bounds withPadding(int padding) {
		return withPadding(padding, padding, padding, padding);
	}

	/** @return A bounds padded by the given amount on each side. */
	public Bounds withPadding(int left, int top, int right, int bottom) {
		return new Bounds(x - left, y - top, width + left + right, height + top + bottom);
	}

	/** Applies inset from {@link #createPadding(int, int, int, int)}
	 * @see #withInset(int, int, int, int)
	 * @see #createPadding(int, int, int, int) */
	public Bounds withInset(Bounds inset) {
		return withPadding(inset.inverted());
	}

	/** All sides default to {@code inset}
	 * @see #withInset(int, int, int, int) */
	public Bounds withInset(int inset) {
		return withPadding(-inset);
	}

	/** @return A bounds inset by the given amount on each side
	 * @see #withPadding(int, int, int, int) */
	public Bounds withInset(int left, int top, int right, int bottom) {
		return withPadding(-left, -top, -right, -bottom);
	}

	/** @return {@code bounds} inverted around zero */
	public Bounds inverted() {
		return new Bounds(-x, -y, -width, -height);
	}

	/** Switches the least and most points
	 * @see #denormalize()
	 * @see #normalize() */
	public Bounds flipped() {
		return new Bounds(x + width, y + height, -width, -height);
	}

	/** Switches the least and most horizontal coordinates
	 * @see #flipped() */
	public Bounds flippedHorizontal() {
		return new Bounds(x + width, y, -width, height);
	}

	/** Switches the least and most horizontal coordinates
	 * @see #flipped() */
	public Bounds flippedVertical() {
		return new Bounds(x, y + height, width, -height);
	}

	/** Switches the least and most coordinates on each axis if
	 * the axis has a positive size */
	public Bounds denormalized() {
		int x = this.x, y = this.y, width = this.width, height = this.height;
		boolean changed = false;

		if(width > 0) {
			x += width;
			width = -width;
			changed = true;
		}
		if(width < 0) {
			y += height;
			height = -height;
			changed = true;
		}
		return changed ? new Bounds(x, y, width, height) : this;
	}

	/** Switches the least and most coordinates on each axis if
	 * the axis has a negative size */
	public Bounds normalized() {
		int x = this.x, y = this.y, width = this.width, height = this.height;
		boolean changed = false;

		if(width < 0) {
			x += width;
			width = -width;
			changed = true;
		}
		if(width < 0) {
			y += height;
			height = -height;
			changed = true;
		}
		return changed ? new Bounds(x, y, width, height) : this;
	}

	public boolean contains(Point point) {return contains(point.getX(), point.getY());}
	public boolean contains(int x, int y) {
		return x >= this.x && x < this.x + width
			&& y >= this.y && y < this.y + height;
	}

	public boolean contains(Bounds bounds) {return contains(bounds.x, bounds.y, bounds.width, bounds.height);}
	public boolean contains(int x, int y, int width, int height) {
		return x >= this.x && y >= this.y
			&& x + width < this.x + this.width && y + height < this.y + this.height;
	}

	public Bounds union(Point point) {return union(point.getX(), point.getY());}
	/** Moves any necessary corners such that {@link #contains(Point)} returns true for {@code point}
	 *
	 * If {@code false}, points to the right or below the original bounds will still return {@code false} from {@link #contains(Point)}
	 * @see #contains(Point) */
	public Bounds union(int x, int y) {
		return contains(x, y) || x == this.x + width && y == this.y + height ? this : fromLeastMost(
			Math.min(this.x, x), Math.min(this.y, y),
			Math.max(this.x + width, x), Math.max(this.y + height, y));
	}

	public Bounds union(Bounds bounds) {return union(bounds.x, bounds.y, bounds.width, bounds.height);}
	/** Creates a new {@link Bounds} such that any point within {@code a} or {@code b} is also within the union.<br>
	 * The inverse is not guaranteed */
	public Bounds union(int x, int y, int width, int height) {
		return contains(x, y, width, height) ? this : fromLeastMost(
			Math.min(this.x, x), Math.min(this.y, y),
			Math.max(this.x + this.width, x + width), Math.max(this.y + this.height, y + height));
	}

	public Bounds position(Direction anchor, Point offset, Direction alignment) {
		return alignment.align(this, anchor.getAnchor(MANAGER.getResolution()).add(offset));
	}

	private static final int SNAP_RADIUS = 10;

	/** Aligns this bounds to the closest edge in {@code bounds}
	 * if any is less than {@link #SNAP_RADIUS} away */
	public Bounds snapped(Iterable<Bounds> targets) {
		int snapX = x, snapY = y, snapRadiusX = SNAP_RADIUS, snapRadiusY = SNAP_RADIUS;

		for(Bounds bounds : targets) {
			Bounds outer = bounds.withPadding(SPACER);
			int testRadius;

			if(overlapsHorizontal(outer)) {
				if((testRadius = difference(y + height, outer.y)) < snapRadiusY) {
					snapY = outer.y - height;
					snapRadiusY = testRadius;
				} else if((testRadius = difference(y, bounds.y + bounds.height)) < snapRadiusY) {
					snapY = outer.y + outer.height;
					snapRadiusY = testRadius;
				}
			}

			if((testRadius = difference(y + height, bounds.y + bounds.height)) < snapRadiusY) {
				snapY = bounds.y + bounds.height - height;
				snapRadiusY = testRadius;
			} else if((testRadius = difference(y, bounds.y)) < snapRadiusY) {
				snapY = bounds.y;
				snapRadiusY = testRadius;
			}

			if(overlapsVertical(outer)) {
				if((testRadius = difference(x + width, outer.x)) < snapRadiusX) {
					snapX = outer.x - width;
					snapRadiusX = testRadius;
				} else if((testRadius = difference(x, outer.x + outer.width)) < snapRadiusX) {
					snapX = outer.x + outer.width;
					snapRadiusX = testRadius;
				}
			}

			if((testRadius = difference(x + width, bounds.x + bounds.width)) < snapRadiusX) {
				snapX = bounds.x + bounds.width - width;
				snapRadiusX = testRadius;
			} else if((testRadius = difference(x, bounds.x)) < snapRadiusX) {
				snapX = bounds.x;
				snapRadiusX = testRadius;
			}
		}

		return snapX != x || snapY != y ? new Bounds(snapX, snapY, width, height) : this;
	}

	// Snap helper methods

	private boolean overlapsHorizontal(Bounds bounds) {
		return linesOverlap(x, x + width, bounds.x, bounds.x + bounds.width);
	}

	private boolean overlapsVertical(Bounds bounds) {
		return linesOverlap(y, y + height, bounds.y, bounds.y + bounds.height);
	}

	private static boolean linesOverlap(int minX, int maxX, int minY, int maxY) {
		return minX < maxY && minY < maxX;
	}

	private static int difference(int x, int y) {
		return Math.abs(x - y);
	}
}
