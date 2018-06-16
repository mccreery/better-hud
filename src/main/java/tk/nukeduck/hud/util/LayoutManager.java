package tk.nukeduck.hud.util;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.GuiIngameForge;

public class LayoutManager {
	public static final int SPACER = 5;

	private final Map<Direction, Integer> corners = new HashMap<Direction, Integer>();
	private ScaledResolution scaledResolution;
	private Point resolution;
	private Bounds screen;

	public void reset(ScaledResolution scaledResolution) {
		reset(new Point(scaledResolution));
		this.scaledResolution = scaledResolution;
	}

	public void reset(Point resolution) {
		this.resolution = resolution;
		screen = new Bounds(this.resolution).withInset(SPACER); // TODO move inset
		corners.clear();

		// Compatibility with bars from other mods
		GuiIngameForge.left_height = GuiIngameForge.right_height = SPACER + 9;
	}

	public Point getResolution() {
		return resolution;
	}

	public ScaledResolution getScaledResolution() {
		return scaledResolution;
	}

	public Bounds getScreen() {
		return screen;
	}

	public Bounds positionBar(Bounds bounds, Direction alignment, int postSpacer) {
		int offset;

		if(alignment.in(Direction.LEFT)) {
			offset = GuiIngameForge.left_height;
		} else if(alignment.in(Direction.RIGHT)) {
			offset = GuiIngameForge.right_height;
		} else {
			offset = Math.max(GuiIngameForge.left_height, GuiIngameForge.right_height);
		}
		offset -= 9 + SPACER;

		Bounds wideBounds = Direction.SOUTH.anchor(bounds.withWidth(182), screen.withInset(offset));
		bounds = alignment.anchor(bounds, wideBounds);

		int newHeight = offset + bounds.getHeight() + postSpacer + SPACER + 9;
		if(!alignment.in(Direction.LEFT)) {
			GuiIngameForge.right_height = newHeight;
		}
		if(!alignment.in(Direction.RIGHT)) {
			GuiIngameForge.left_height = newHeight;
		}
		corners.put(Direction.SOUTH, Math.max(GuiIngameForge.left_height, GuiIngameForge.right_height) - 9 - SPACER);

		return bounds;
	}

	public Bounds position(Direction corner, Bounds bounds) {
		return position(corner, bounds, false, SPACER);
	}

	public Bounds position(Direction corner, Bounds bounds, boolean edge, int postSpacer) {
		if(corner.in(Direction.HORIZONTAL)) {
			throw new IllegalArgumentException("Vertical centering is not allowed");
		}
		int offset = corners.getOrDefault(corner, edge ? -SPACER : 0);

		bounds = corner.anchor(bounds, screen.withInset(0, offset, 0, offset));
		int newOffset = offset + bounds.getHeight() + postSpacer;
		corners.put(corner, newOffset);

		if(corner == Direction.SOUTH) {
			GuiIngameForge.left_height = GuiIngameForge.right_height = newOffset + SPACER + 9;
		}
		return bounds;
	}
}
