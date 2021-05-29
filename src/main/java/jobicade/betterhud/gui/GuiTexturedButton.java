package jobicade.betterhud.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import jobicade.betterhud.geom.Point;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.util.GlUtil;
import jobicade.betterhud.util.Textures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;

public class GuiTexturedButton extends GuiActionButton {
    private final Rect disabled, inactive, active;

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
        switch(getYImage(this.isHovered)) {
            case 0:  return disabled;
            case 2:  return active;
            case 1:
            default: return inactive;
        }
    }

    /**
     * OpenGL side-effects: set texture to Gui.ICONS
     */
    @Override
    protected void drawButton(MatrixStack matrixStack, Rect bounds, Point mousePosition, float partialTicks) {
        Minecraft.getInstance().getTextureManager().bind(Textures.SETTINGS);
        GlUtil.drawRect(bounds, getTexture());
        Minecraft.getInstance().getTextureManager().bind(AbstractGui.GUI_ICONS_LOCATION);
    }
}
