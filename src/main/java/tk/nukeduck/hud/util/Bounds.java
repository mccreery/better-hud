package tk.nukeduck.hud.util;

import static tk.nukeduck.hud.BetterHud.MANAGER;
import static tk.nukeduck.hud.BetterHud.SPACER;

public class Bounds {
	public static final Bounds EMPTY = new Bounds();
	public static final Bounds PADDING = getPadding(SPACER);

	public Point position, size;

	public Bounds() {
		this(0, 0, 0, 0);
	}

	public Bounds(int width, int height) {
		this(0, 0, width, height);
	}

	public Bounds(int x, int y, int width, int height) {
		position = new Point(x, y);
		size = new Point(width, height);
	}

	public Bounds(Point size) {
		this(Point.ZERO, size);
	}

	public Bounds(Point position, Point size) {
		this.position = new Point(position);
		this.size = new Point(size);
	}

	public Bounds(Bounds bounds) {
		this(bounds.position, bounds.size);
	}

	public boolean isEmpty() {
		return this == EMPTY || size.equals(Point.ZERO);
	}

	public static boolean isEmpty(Bounds bounds) {
		return bounds == null || bounds.isEmpty();
	}

	public int x() {return position.x;}
	public void x(int x) {position.x = x;}
	public int y() {return position.y;}
	public void y(int y) {position.y = y;}
	public int width() {return size.x;}
	public void width(int width) {size.x = width;}
	public int height() {return size.y;}
	public void height(int height) {size.y = height;}

	public int left() {return x();}
	public void left(int left) {
		width(width() + left() - left);
		x(left);
	}
	public int top() {return y();}
	public void top(int top) {
		height(height() + top() - top);
		y(top);
	}
	public int right() {return x() + width();}
	public void right(int right) {width(right - x());}
	public int bottom() {return y() + height();}
	public void bottom(int bottom) {height(bottom - y());}

	/** @return {@code bounds} inverted around zero */
	public Bounds invert() {
		return new Bounds(-x(), -y(), -width(), -height());
	}

	/** @see #getPadding(int, int, int, int) */
	public static Bounds getPadding(int padding) {
		return getPadding(padding, padding, padding, padding);
	}

	/** @return A bounds such that the distance from the origin on each side
	 * represents padding on that side, for use with {@link #pad(Bounds)} and {@link #inset(Bounds)} */
	public static Bounds getPadding(int left, int top, int right, int bottom) {
		return new Bounds(-left, -top, left + right, top + bottom);
	}

	/** All sides default to {@code padding}
	 * @see #pad(int, int, int, int) */
	public Bounds pad(int padding) {
		return pad(padding, padding, padding, padding);
	}

	/** Applies padding from {@link #getPadding(int, int, int, int)}
	 * @see #pad(int, int, int, int)
	 * @see #getPadding(int, int, int, int) */
	public Bounds pad(Bounds padding) {
		return pad(-padding.left(), -padding.top(), padding.right(), padding.bottom());
	}

	/** @return A bounds padded by the given amount on each side. */
	public Bounds pad(int left, int top, int right, int bottom) {
		return new Bounds(left() - left, top() - top, width() + left + right, height() + top + bottom);
	}

	/** All sides default to {@code inset}
	 * @see #inset(int, int, int, int) */
	public Bounds inset(int inset) {
		return pad(-inset);
	}

	/** Applies inset from {@link #getPadding(int, int, int, int)}
	 * @see #inset(int, int, int, int)
	 * @see #getPadding(int, int, int, int) */
	public Bounds inset(Bounds inset) {
		return pad(inset.invert());
	}

	/** @return A bounds inset by the given amount on each side
	 * @see #pad(int, int, int, int) */
	public Bounds inset(int left, int top, int right, int bottom) {
		return pad(-left, -top, -right, -bottom);
	}

	/** Switches the least and most points
	 * @see #denormalize()
	 * @see #normalize() */
	public Bounds flip() {
		position = position.add(size);
		size = size.invert();

		return this;
	}

	/** Switches the least and most coordinates on each axis if
	 * the axis has a positive size */
	public Bounds denormalize() {
		if(width() > 0) {
			position.x += size.x;
			size.x = -size.x;
		}
		if(height() > 0) {
			position.y += size.y;
			size.y = -size.y;
		}

		return this;
	}

	/** Switches the least and most coordinates on each axis if
	 * the axis has a negative size */
	public Bounds normalize() {
		if(width() < 0) {
			position.x += size.x;
			size.x = -size.x;
		}
		if(height() < 0) {
			position.y += size.y;
			size.y = -size.y;
		}

		return this;
	}

	public boolean contains(Point point) {
		return contains(point.x, point.y);
	}
	public boolean contains(int x, int y) {
		return x >= left() && x < right() && y >= top() && y < bottom();
	}

	/** Moves any necessary corners such that {@link #contains(Point)} returns true for {@code point}
	 *
	 * @param inclusive {@code true} if {@code point} should move the exclusive boundary slightly past it.<br>
	 * If {@code false}, points to the right or below the original bounds will still return {@code false} from {@link #contains(Point)}
	 * @see #contains(Point) */
	public Bounds ensureContains(Point point, boolean inclusive) {
		if(point.x < left()) {
			left(point.x);
		} else if(point.x >= right()) {
			right(inclusive ? point.x + 1 : point.x);
		}

		if(point.y < top()) {
			top(point.y);
		} else if(point.y >= bottom()) {
			bottom(inclusive ? point.y + 1 : point.y);
		}
		return this;
	}

	/** Creates a new {@link Bounds} such that any point within {@code a} or {@code b} is also within the union.<br>
	 * The inverse is not guaranteed */
	public static Bounds union(Bounds a, Bounds b) {
		Bounds union = new Bounds(a);
		union.ensureContains(b.position, false);
		union.ensureContains(Direction.SOUTH_EAST.getAnchor(b), false);

		return union;
	}

	/** @return {@code true} if this bounds and {@code bounds} overlap
	 * both horizontally and vertically
	 *
	 * @see #overlapsH(Bounds)
	 * @see #overlapsV(Bounds) */
	public boolean overlaps(Bounds bounds) {
		return overlapsH(bounds) && overlapsV(bounds);
	}

	/** @return {@code true} if this bounds and {@code bounds} overlap horizontally */
	public boolean overlapsH(Bounds bounds) {
		return linesOverlap(left(), right(), bounds.left(), bounds.right());
	}

	/** @return {@code true} if this bounds and {@code bounds} overlap vertically */
	public boolean overlapsV(Bounds bounds) {
		return linesOverlap(top(), bottom(), bounds.top(), bounds.bottom());
	}

	private static boolean linesOverlap(int minX, int maxX, int minY, int maxY) {
		return minX < maxY && minY < maxX;
	}

	private static int difference(int x, int y) {
		return Math.abs(x - y);
	}

	public void position(Direction anchor, Point offset, Direction alignment) {
		alignment.align(this, anchor.getAnchor(MANAGER.getResolution()).add(offset));
	}

	private static final int SNAP_RADIUS = 10;

	/** Aligns this bounds to the closest edge in {@code bounds}
	 * if any is less than {@link #SNAP_RADIUS} away */
	public void snap(Iterable<Bounds> targets) {
		Point snapPosition = new Point(position);
		Point snapRadius = new Point(SNAP_RADIUS, SNAP_RADIUS);

		for(Bounds bounds : targets) {
			Bounds outer = bounds.pad(SPACER);
			int testRadius;

			if(overlapsH(outer)) {
				if((testRadius = difference(bottom(), outer.top())) < snapRadius.y) {
					snapPosition.y = outer.top() - height();
					snapRadius.y = testRadius;
				} else if((testRadius = difference(top(), bounds.bottom())) < snapRadius.y) {
					snapPosition.y = outer.bottom();
					snapRadius.y = testRadius;
				}
			}

			if((testRadius = difference(bottom(), bounds.bottom())) < snapRadius.y) {
				snapPosition.y = bounds.bottom() - height();
				snapRadius.y = testRadius;
			} else if((testRadius = difference(top(), bounds.top())) < snapRadius.y) {
				snapPosition.y = bounds.top();
				snapRadius.y = testRadius;
			}

			if(overlapsV(outer)) {
				if((testRadius = difference(right(), outer.left())) < snapRadius.x) {
					snapPosition.x = outer.left() - width();
					snapRadius.x = testRadius;
				} else if((testRadius = difference(left(), outer.right())) < snapRadius.x) {
					snapPosition.x = outer.right();
					snapRadius.x = testRadius;
				}
			}

			if((testRadius = difference(right(), bounds.right())) < snapRadius.x) {
				snapPosition.x = bounds.right() - width();
				snapRadius.x = testRadius;
			} else if((testRadius = difference(left(), bounds.left())) < snapRadius.x) {
				snapPosition.x = bounds.left();
				snapRadius.x = testRadius;
			}
		}

		if(!snapPosition.equals(position)) {
			position = snapPosition;
		}
	}
}
