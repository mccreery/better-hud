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
		int offset = 0;
		int column = alignment.getColumn();

		switch(column) {
			case 0: offset = GuiIngameForge.left_height; break;
			case 1: offset = Math.max(GuiIngameForge.left_height, GuiIngameForge.right_height); break;
			case 2: offset = GuiIngameForge.right_height; break;
		}
		offset -= 9 + SPACER;

		Bounds wideBounds = Direction.SOUTH.anchor(bounds.withWidth(182), screen.withInset(offset));
		bounds = alignment.anchor(bounds, wideBounds);

		int newHeight = offset + bounds.getHeight() + postSpacer + SPACER + 9;

		if(column > 0) GuiIngameForge.right_height = newHeight;
		if(column < 2) GuiIngameForge.left_height = newHeight;

		corners.put(Direction.SOUTH, newHeight - SPACER - 9);
		return bounds;
	}

	public Bounds position(Direction corner, Bounds bounds) {
		return position(corner, bounds, false, SPACER);
	}

	public Bounds position(Direction corner, Bounds bounds, boolean edge, int postSpacer) {
		if(corner.getRow() == 1) {
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
