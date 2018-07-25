package tk.nukeduck.hud.util;

import java.io.Serializable;
import java.util.Objects;

import net.minecraft.client.gui.ScaledResolution;

public final class Point implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final Point ZERO = new Point(0, 0);

	private final int x, y;

	public Point() {
		this(0, 0);
	}

	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public Point(Point point) {
		this(point.getX(), point.getY());
	}

	public Point(ScaledResolution resolution) {
		this(resolution.getScaledWidth(), resolution.getScaledHeight());
	}

	public static Point createRandom(Bounds bounds) {
		return createRandom(bounds.getLeft(), bounds.getTop(), bounds.getRight(), bounds.getBottom());
	}
	public static Point createRandom(int left, int top, int right, int bottom) {
		return new Point(MathUtil.randomRange(left, right), MathUtil.randomRange(top, bottom));
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public Point withX(int x) {
		return new Point(x, getY());
	}

	public Point withY(int y) {
		return new Point(getX(), y);
	}

	public Point add(Point point) {
		return new Point(getX() + point.getX(), getY() + point.getY());
	}

	public Point add(int x, int y) {
		return new Point(this.getX() + x, this.getY() + y);
	}

	public Point sub(Point point) {
		return new Point(getX() - point.getX(), getY() - point.getY());
	}

	public Point sub(int x, int y) {
		return new Point(this.getX() - x, this.getY() - y);
	}

	public Point scale(float x, float y) {
		return new Point((int)(this.getX() * x), (int)(this.getY() * y));
	}

	public Point invert() {
		return new Point(-getX(), -getY());
	}

	@Override
	public boolean equals(Object other) {
		return other instanceof Point && getX() == ((Point)other).getX() && getY() == ((Point)other).getY();
	}

	@Override
	public int hashCode() {
		return Objects.hash(getX(), getY());
	}

	@Override
	public String toString() {
		return String.format("%d,%d", getX(), getY());
	}

	public static Point fromString(String s) {
		int comma = s.indexOf(',');

		return new Point(Integer.parseInt(s.substring(0, comma)),
			Integer.parseInt(s.substring(comma + 1)));
	}
}
