package tk.nukeduck.hud.util;

import net.minecraft.client.gui.ScaledResolution;

public class Point implements ISaveLoad {
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
		return Point.ZERO.sub(this);
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof Point && obj == null ? equals(ZERO) : x == ((Point)obj).x && y == ((Point)obj).y;
	}

	@Override
	public String toString() {
		return String.format("(%d, %d)", x, y);
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
