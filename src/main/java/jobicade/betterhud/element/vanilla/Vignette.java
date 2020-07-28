package jobicade.betterhud.element.vanilla;

import static jobicade.betterhud.BetterHud.MANAGER;

import jobicade.betterhud.element.OverlayElement;
import jobicade.betterhud.element.settings.SettingBoolean;
import jobicade.betterhud.events.OverlayContext;
import jobicade.betterhud.events.OverlayHook;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.render.Color;
import jobicade.betterhud.util.GlUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.border.WorldBorder;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;

public class Vignette extends OverlayElement {
    private static final ResourceLocation VIGNETTE_TEX_PATH = new ResourceLocation("textures/misc/vignette.png");

    private SettingBoolean warnings;
    private float brightness = 1;

    public Vignette() {
        super("vignette");

        warnings = new SettingBoolean("warnings");
        warnings.setValuePrefix(SettingBoolean.VISIBLE);

        settings.addChild(warnings);
    }

    @Override
    public boolean shouldRender(OverlayContext context) {
        return GuiIngameForge.renderVignette
            && Minecraft.isFancyGraphicsEnabled()
            && !OverlayHook.pre(context.getEvent(), ElementType.VIGNETTE);
    }

    @Override
    public Rect render(OverlayContext context) {
        WorldBorder border = Minecraft.getMinecraft().world.getWorldBorder();

        float f = 0;
        if (warnings.get()) {
            float distance = (float)border.getClosestDistance(Minecraft.getMinecraft().player);
            float warningDistance = (float)getWarningDistance(border);

            if(distance < warningDistance) {
                f = 1 - distance / warningDistance;
            }
        }

        // Animate brightness
        brightness = brightness + (MathHelper.clamp(1 - Minecraft.getMinecraft().player.getBrightness(), 0, 1) - brightness) / 100;

        Color color;
        if(f > 0) {
            int shade = Math.round(f * 255.0f);
            color = new Color(0, shade, shade);
        } else {
            int value = Math.round(brightness * 255.0f);
            color = new Color(value, value, value);
        }

        GlUtil.blendFuncSafe(SourceFactor.ZERO, DestFactor.ONE_MINUS_SRC_COLOR, SourceFactor.ONE, DestFactor.ZERO);
        Minecraft.getMinecraft().getTextureManager().bindTexture(VIGNETTE_TEX_PATH);

        GlUtil.drawRect(MANAGER.getScreen(), new Rect(256, 256), color);

        Minecraft.getMinecraft().getTextureManager().bindTexture(Gui.ICONS);
        GlUtil.blendFuncSafe(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ZERO, DestFactor.ONE);

        OverlayHook.post(context.getEvent(), ElementType.VIGNETTE);
        return null;
    }

    /**
     * @return The distance from the world border at which a player will start
     * to see a warning.
     */
    private double getWarningDistance(WorldBorder worldBorder) {
        // The distance the border will move within the warning time
        double warningTimeDistance = worldBorder.getResizeSpeed() // meters/millis
            * worldBorder.getWarningTime() * 1000; // millis

        // Border cannot move further than the target size
        double remainingResize = Math.abs(worldBorder.getTargetSize() - worldBorder.getDiameter());
        warningTimeDistance = Math.min(warningTimeDistance, remainingResize);

        // Warn by distance and time
        // The larger distance triggers a warning first
        return Math.max(
            worldBorder.getWarningDistance(),
            warningTimeDistance
        );
    }
}
