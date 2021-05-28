package jobicade.betterhud.element.settings;

import jobicade.betterhud.geom.Point;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.gui.GuiTexturedButton;
import net.minecraft.client.gui.AbstractGui;

import java.util.List;
import java.util.Map;

public class SettingLock extends SettingBoolean {
    private Rect bounds;

    public SettingLock(String name) {
        super(name);
    }

    public void setRect(Rect bounds) {
        this.bounds = bounds;
    }

    @Override
    public void getGuiParts(List<AbstractGui> parts, Map<AbstractGui, Setting<?>> callbacks, Rect bounds) {
        toggler = new GuiTexturedButton(new Rect(0, 60, 20, 10)).setBounds(bounds).setCallback(b -> toggle());
        parts.add(toggler);
        callbacks.put(toggler, this);
    }

    @Override
    public Point getGuiParts(List<AbstractGui> parts, Map<AbstractGui, Setting<?>> callbacks, Point origin) {
        getGuiParts(parts, callbacks, bounds);
        return null;
    }
}
