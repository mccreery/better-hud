package jobicade.betterhud.element.settings;

import java.util.List;
import java.util.Map;

import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.util.Textures;
import net.minecraft.client.gui.AbstractGui;

public class SettingLock extends SettingBoolean {
    // Custom bounds overrides aligned bounds
    private Rect bounds;

    public SettingLock(String name) {
        super(name);
    }

    public void setRect(Rect bounds) {
        this.bounds = bounds;
    }

    @Override
    public void getGuiParts(List<AbstractGui> parts, Map<AbstractGui, Setting> callbacks, Rect bounds) {
        super.getGuiParts(parts, callbacks, this.bounds);
        toggler.setTexture(Textures.SETTINGS, 0, 60, 20);
    }

    @Override
    protected boolean shouldBreak() {
        return false;
    }
}
