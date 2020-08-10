package jobicade.betterhud.gui;

import static jobicade.betterhud.BetterHud.MC;

import jobicade.betterhud.geom.Point;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.util.GlUtil;
import jobicade.betterhud.util.Textures;
import net.minecraft.client.gui.Gui;

public class GuiTexturedButton extends GuiActionButton {
    private Rect disabled, inactive, active;

    public GuiTexturedButton(Rect disabled) {
        this(disabled, disabled.getHeight());
    }

    public GuiTexturedButton(Rect disabled, int pitch) {
        this(disabled,
            disabled.withY(disabled.getY() + pitch),
            disabled.withY(disabled.getY() + pitch * 2));
    }

    public GuiTexturedButton(Rect disabled, Rect inactive, Rect active) {
        super("");

        this.disabled = disabled;
        this.inactive = inactive.resize(disabled.getSize());
        this.active   = active.resize(disabled.getSize());
    }

    @Override
    public GuiActionButton setBounds(Rect bounds) {
        return super.setBounds(bounds.resize(disabled.getSize()));
    }

    protected Rect getTexture() {
        switch(getHoverState(this.hovered)) {
            case 0:  return disabled;
            case 2:  return active;
            case 1:
            default: return inactive;
        }
    }

    public void setTexture(Rect disabled) {
        this.disabled = disabled;
        this.inactive = disabled.withY(disabled.getY() + disabled.getHeight());
        this.active = disabled.withY(disabled.getY() + disabled.getHeight() * 2);
    }

    /**
     * OpenGL side-effects: set texture to Gui.ICONS
     */
    @Override
    protected void drawButton(Rect bounds, Point mousePosition, float partialTicks) {
        MC.getTextureManager().bindTexture(Textures.SETTINGS);
        GlUtil.drawRect(bounds, getTexture());
        MC.getTextureManager().bindTexture(Gui.ICONS);
    }
}
