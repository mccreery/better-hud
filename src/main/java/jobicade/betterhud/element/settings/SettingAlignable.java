package jobicade.betterhud.element.settings;

import static jobicade.betterhud.BetterHud.SPACER;

import java.util.List;
import java.util.Map;

import jobicade.betterhud.element.HudElement;
import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Point;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.gui.GuiElementSettings;

public abstract class SettingAlignable extends Setting {
    protected Direction alignment = Direction.CENTER;

    public SettingAlignable(HudElement<?> element, String name) {
        super(element, name);
    }

    public SettingAlignable(Setting parent, String name) {
        super(parent, name);
    }

    public void setAlignment(Direction alignment) {
        this.alignment = alignment;
    }

    @Override
    public Point getGuiParts(GuiElementSettings.Populator populator, Point origin) {
        origin = super.getGuiParts(populator, origin);

        Rect bounds = new Rect(getSize());
        bounds = bounds.anchor(new Rect(getAlignmentWidth(), bounds.getHeight()).align(origin, Direction.NORTH), alignment);

        getGuiParts(populator, bounds);
        return shouldBreak() ? origin.withY(bounds.getBottom() + SPACER) : origin;
    }

    protected int getAlignmentWidth() {
        return 300;
    }

    protected Point getSize() {
        return new Point(alignment == Direction.CENTER ? 200 : 150, 20);
    }

    protected boolean shouldBreak() {
        return alignment != Direction.WEST;
    }

    /** @see Setting#getGuiParts(List, Map, Point) */
    public abstract void getGuiParts(GuiElementSettings.Populator populator, Rect bounds);
}
