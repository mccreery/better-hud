package tk.nukeduck.hud.util;

import java.util.Objects;

import net.minecraft.client.gui.ScaledResolution;
import tk.nukeduck.hud.util.ISaveLoad.IGetSet;

public class Point implements IGetSet<Point> {
	public static final Point ZERO = new Point(0, 0);

	public int x, y;

	public Point() {
		this(0, 0);
	}

	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public Point(Point point) {
		this(point.x, point.y);
	}

	public Point(ScaledResolution resolution) {
		this(resolution.getScaledWidth(), resolution.getScaledHeight());
	}

	public Point add(Point point) {
		return new Point(x + point.x, y + point.y);
	}

	public Point add(int x, int y) {
		return new Point(this.x + x, this.y + y);
	}

	public Point sub(Point point) {
		return new Point(x - point.x, y - point.y);
	}

	public Point sub(int x, int y) {
		return new Point(this.x - x, this.y - y);
	}

	public Point scale(float x, float y) {
		return new Point((int)(this.x * x), (int)(this.y * y));
	}

	public Point invert() {
		return new Point(-x, -y);
	}

	@Override
	public boolean equals(Object other) {
		return other instanceof Point && x == ((Point)other).x && y == ((Point)other).y;
	}

	@Override
	public int hashCode() {
		return Objects.hash(x, y);
	}

	@Override
	public String toString() {
		return String.format("(%d, %d)", x, y);
	}

	@Override
	public Point get() {
		return this;
	}

	@Override
	public void set(Point value) {
		this.x = value.x;
		this.y = value.y;
	}

	@Override
	public String save() {
		return x + "," + y;
	}

	@Override
	public void load(String save) {
		int comma = save.indexOf(',');

		x = Integer.parseInt(save.substring(0, comma));
		y = Integer.parseInt(save.substring(comma + 1));
	}
}
