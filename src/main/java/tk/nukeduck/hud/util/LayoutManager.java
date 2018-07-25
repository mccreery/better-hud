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
		screen = new Bounds(this.resolution);
		corners.clear();

		// Compatibility with bars from other mods
		GuiIngameForge.left_height = GuiIngameForge.right_height = SPACER + 9;
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
		offset -= 9;

		Bounds wideBounds = bounds.withWidth(182).anchoredTo(screen.withInset(offset), Direction.SOUTH);
		bounds = bounds.anchoredTo(wideBounds, alignment);

		int newHeight = offset + bounds.getHeight() + postSpacer + 9;

		if(column > 0) GuiIngameForge.right_height = newHeight;
		if(column < 2) GuiIngameForge.left_height = newHeight;

		corners.put(Direction.SOUTH, newHeight - 9);
		return bounds;
	}

	public Bounds position(Direction corner, Bounds bounds) {
		return position(corner, bounds, false, SPACER);
	}

	public Bounds position(Direction corner, Bounds bounds, boolean edge, int postSpacer) {
		if(corner.getRow() == 1) {
			throw new IllegalArgumentException("Vertical centering is not allowed");
		}
		int offset = corners.getOrDefault(corner, edge ? 0 : SPACER);

		bounds = bounds.anchoredTo(screen.withInset(SPACER, offset, SPACER, offset), corner);
		int newOffset = offset + bounds.getHeight() + postSpacer;
		corners.put(corner, newOffset);

		if(corner == Direction.SOUTH) {
			GuiIngameForge.left_height = GuiIngameForge.right_height = newOffset + 9;
		}
		return bounds;
	}
}
