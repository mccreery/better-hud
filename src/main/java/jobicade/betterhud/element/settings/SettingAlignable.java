package jobicade.betterhud.element.settings;

import static jobicade.betterhud.BetterHud.SPACER;

import java.util.List;
import java.util.Map;

import net.minecraft.client.gui.Gui;
import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Point;
import jobicade.betterhud.geom.Rect;

public abstract class SettingAlignable<T> extends Setting<T> {
    protected Direction alignment;

    public SettingAlignable(String name, Direction alignment) {
        super(name);
        this.alignment = alignment;
    }

    public SettingAlignable<T> setAlignment(Direction alignment) {
        this.alignment = alignment;
        return this;
    }

    @Override
    public Point getGuiParts(List<Gui> parts, Map<Gui, Setting<?>> callbacks, Point origin) {
        origin = super.getGuiParts(parts, callbacks, origin);

        Rect bounds = new Rect(getSize());
        bounds = bounds.anchor(new Rect(getAlignmentWidth(), bounds.getHeight()).align(origin, Direction.NORTH), alignment);

        getGuiParts(parts, callbacks, bounds);
        return shouldBreak() ? origin.withY(bounds.getBottom() + SPACER) : null;
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
    public abstract void getGuiParts(List<Gui> parts, Map<Gui, Setting<?>> callbacks, Rect bounds);
}
