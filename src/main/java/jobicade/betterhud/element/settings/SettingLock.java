package jobicade.betterhud.element.settings;

import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.gui.GuiElementSettings;
import jobicade.betterhud.util.Textures;

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
    public void getGuiParts(GuiElementSettings.Populator populator, Rect bounds) {
        super.getGuiParts(populator, this.bounds);
        toggler.setTexture(Textures.SETTINGS, 0, 60, 20);
    }

    @Override
    protected boolean shouldBreak() {
        return false;
    }

    @Override
    public void updateGuiParts() {
        toggler.active = enabled();
    }
}
