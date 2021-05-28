package jobicade.betterhud.geom;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.gui.ForgeIngameGui;

import java.util.HashMap;
import java.util.Map;

public class LayoutManager {
    public static final int SPACER = 5;

    private final Map<Direction, Integer> corners = new HashMap<Direction, Integer>();
    private Rect screen;

    public void reset(ScaledResolution scaledResolution) {
        reset(new Point(scaledResolution));
    }

    public void reset(Point resolution) {
        this.screen = new Rect(resolution);
        corners.clear();

        // Compatibility with bars from other mods
        ForgeIngameGui.left_height = ForgeIngameGui.right_height = SPACER + 9;
    }

    public Rect getScreen() {
        return screen;
    }

    public Rect positionBar(Rect bounds, Direction alignment, int postSpacer) {
        int offset = 0;
        int column = alignment.getCol();

        switch(column) {
            case 0: offset = ForgeIngameGui.left_height; break;
            case 1: offset = Math.max(ForgeIngameGui.left_height, ForgeIngameGui.right_height); break;
            case 2: offset = ForgeIngameGui.right_height; break;
        }
        offset -= 9;

        Rect wideRect = bounds.withWidth(182).anchor(screen.grow(-offset), Direction.SOUTH, false);
        bounds = bounds.anchor(wideRect, alignment, false);

        int newHeight = offset + bounds.getHeight() + postSpacer + 9;

        if(column > 0) ForgeIngameGui.right_height = newHeight;
        if(column < 2) ForgeIngameGui.left_height = newHeight;

        corners.put(Direction.SOUTH, newHeight - 9);
        return bounds;
    }

    public Rect position(Direction corner, Rect bounds) {
        return position(corner, bounds, false, SPACER);
    }

    public Rect position(Direction corner, Rect bounds, boolean edge, int postSpacer) {
        if(corner.getRow() == 1) {
            throw new IllegalArgumentException("Vertical centering is not allowed");
        }
        int offset = corners.getOrDefault(corner, edge ? 0 : SPACER);

        bounds = bounds.anchor(screen.grow(-SPACER, -offset, -SPACER, -offset), corner, false);
        int newOffset = offset + bounds.getHeight() + postSpacer;
        corners.put(corner, newOffset);

        if(corner == Direction.SOUTH) {
            ForgeIngameGui.left_height = ForgeIngameGui.right_height = newOffset + 9;
        }
        return bounds;
    }
}
