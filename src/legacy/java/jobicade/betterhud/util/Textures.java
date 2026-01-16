package jobicade.betterhud.util;

import jobicade.betterhud.BetterHud;
import net.minecraft.util.ResourceLocation;

/**
 * Static definitions for common texture resources.
 *
 * @see net.minecraft.client.gui.Gui#ICONS
 */
public final class Textures {
    public static final ResourceLocation WIDGETS   = new ResourceLocation("textures/gui/widgets.png");
    public static final ResourceLocation PARTICLES = new ResourceLocation("textures/particle/particles.png");
    public static final ResourceLocation HUD_ICONS = new ResourceLocation(BetterHud.MODID, "textures/gui/icons_hud.png");
    public static final ResourceLocation SETTINGS  = new ResourceLocation(BetterHud.MODID, "textures/gui/settings.png");

    private Textures() {}
}
