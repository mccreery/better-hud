package tk.nukeduck.hud.util;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.gui.ScaledResolution;

public class LayoutManager {
	public static final int SPACER = 5;

	private final Map<Direction, Integer> corners = new HashMap<Direction, Integer>();
	private ScaledResolution scaledResolution;
	private Point resolution;
	private Bounds screen;

	public void reset(ScaledResolution scaledResolution) {
		reset(new Point(scaledResolution), SPACER);
		this.scaledResolution = scaledResolution;
	}

	public void reset(Point resolution, int spacer) {
		this.resolution = resolution;
		screen = new Bounds(this.resolution).inset(SPACER);
		corners.clear();
	}

	public Point getResolution() {
		return resolution;
	}

	public ScaledResolution getScaledResolution() {
		return scaledResolution;
	}

	public <T extends Bounds> T position(Direction corner, T bounds) {
		if(corner.in(Direction.HORIZONTAL)) {
			throw new IllegalArgumentException("Vertical centering is not allowed");
		}

		int offset = corners.containsKey(corner) ? corners.get(corner) : 0;
		Bounds contracted = this.screen.inset(0, offset, 0, offset);
		corner.anchor(bounds, contracted);

		corners.put(corner, offset + bounds.height() + SPACER);
		return bounds;
	}
}
