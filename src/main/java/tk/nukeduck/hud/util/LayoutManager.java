package tk.nukeduck.hud.util;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.gui.ScaledResolution;

public class LayoutManager {
	public static final int SPACER = 5;

	private final Map<Direction, Integer> corners = new HashMap<Direction, Integer>();
	private final Point resolution;
	private final Bounds bounds;

	public LayoutManager(ScaledResolution resolution) {
		this.resolution = new Point(resolution);
		bounds = new Bounds(this.resolution).inset(SPACER);
	}

	public Point getResolution() {
		return resolution;
	}

	/** @param size The generated bounds are guaranteed to be this size
	 * @return The bounds to draw an element in {@code corner} of the screen */
	@Deprecated
	public Bounds getBounds(Direction corner, Point size) {
		return position(corner, new Bounds(size));
	}

	public <T extends Bounds> T position(Direction corner, T bounds) {
		if(corner.in(Direction.HORIZONTAL)) {
			throw new IllegalArgumentException("Vertical centering is not allowed");
		}
		corner.anchor(bounds, this.bounds);

		int y = bounds.y();
		if(corner.in(Direction.TOP)) {
			y += get(corner);
		} else {
			y -= get(corner);
		}
		bounds.y(y);
		add(corner, bounds.height() + SPACER);

		return bounds;
	}

	private int get(Direction corner) {
		return corners.containsKey(corner) ? corners.get(corner) : 0;
	}

	private void add(Direction corner, int value) {
		corners.put(corner, get(corner) + value);
	}
}
