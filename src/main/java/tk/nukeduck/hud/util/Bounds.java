package tk.nukeduck.hud.util;

import static tk.nukeduck.hud.BetterHud.SPACER;

import java.util.ArrayList;

public class Bounds {
	public static final Bounds EMPTY = new Bounds();

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

	/** @return An inverted bounds {@code bounds} such that least coordinates
	 * are replaced by most coordinates, e.g. {@code bounds.left() == this.right()} */
	public Bounds invert() {
		return new Bounds(right(), bottom(), -width(), -height());
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

	/** @return A bounds padded by the given amount on each side */
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

	public void snapTest(Bounds... b) {snapTest(SPACER, b);}
	public void snapTest(int hitRadius, Bounds... bounds) {
		ArrayList<Integer> xClips = new ArrayList<Integer>();
		ArrayList<Integer> yClips = new ArrayList<Integer>();

		for(Bounds b : bounds) {
			b = b.pad(SPACER);

			int clipX = this.x();
			int clipY = this.y();

			if(lineOverlaps(this.x(), this.right(), b.x(), b.right())) {
				int toClip = b.bottom();
				if(Math.abs(toClip - this.y()) < hitRadius) {
					clipY = toClip;
				} else {
					toClip = b.y();
					if(Math.abs(toClip - this.bottom()) < hitRadius) {
						clipY = toClip - this.height();
					}
				}
			}
			if(lineOverlaps(this.y(), this.bottom(), b.y(), b.bottom())) {
				int toClip = b.right();
				if(Math.abs(toClip - this.x()) < hitRadius) {
					clipX = toClip;
				} else {
					toClip = b.x();
					if(Math.abs(toClip - this.right()) < hitRadius) {
						clipX = toClip - this.width();
					}
				}
			}

			if(clipX != this.x()) xClips.add(clipX);
			if(clipY != this.y()) yClips.add(clipY);
		}

		this.position = new Point(FuncsUtil.getSmallestDistance(this.x(), xClips), FuncsUtil.getSmallestDistance(this.y(), yClips));
	}

	private static boolean lineOverlaps(int min, int max, int min2, int max2) {
		return min  >= min2 && min  < max2
			|| max  >= min2 && max  < max2
			|| min2 >= min  && min2 < max
			|| max2 >= min  && max2 < max;
	}
}
