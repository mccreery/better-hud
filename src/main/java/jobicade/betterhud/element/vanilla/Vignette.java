package jobicade.betterhud.element.vanilla;

import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import jobicade.betterhud.element.settings.Setting;
import jobicade.betterhud.element.settings.SettingBoolean;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.render.Color;
import jobicade.betterhud.util.GlUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.border.WorldBorder;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.eventbus.api.Event;

import java.util.List;

import static jobicade.betterhud.BetterHud.MANAGER;

public class Vignette extends OverrideElement {
    private static final ResourceLocation VIGNETTE_TEX_PATH = new ResourceLocation("textures/misc/vignette.png");

    private SettingBoolean warnings;
    private float brightness = 1;

    public Vignette() {
        super("vignette");
    }

    @Override
    protected void addSettings(List<Setting<?>> settings) {
        super.addSettings(settings);
        settings.add(warnings = new SettingBoolean("warnings").setValuePrefix(SettingBoolean.VISIBLE));
    }

    @Override
    public void loadDefaults() {
        super.loadDefaults();
        warnings.set(true);
    }

    @Override
    protected ElementType getType() {
        return ElementType.VIGNETTE;
    }

    @Override
    public boolean shouldRender(Event event) {
        return Minecraft.useFancyGraphics() && super.shouldRender(event);
    }

    @Override
    protected Rect render(Event event) {
        WorldBorder border = Minecraft.getInstance().level.getWorldBorder();

        float distance = (float)border.getDistanceToBorder(Minecraft.getInstance().player);
        float warningDistance = (float)getWarningDistance(border);

        float f;
        if(distance < warningDistance) {
            f = 1 - distance / warningDistance;
        } else {
            f = 0;
        }

        // Animate brightness
        brightness = brightness + (MathHelper.clamp(1 - Minecraft.getInstance().player.getBrightness(), 0, 1) - brightness) / 100;

        Color color;
        if(f > 0) {
            int shade = Math.round(f * 255.0f);
            color = new Color(0, shade, shade);
        } else {
            int value = Math.round(brightness * 255.0f);
            color = new Color(value, value, value);
        }

        GlUtil.blendFuncSafe(SourceFactor.ZERO, DestFactor.ONE_MINUS_SRC_COLOR, SourceFactor.ONE, DestFactor.ZERO);
        Minecraft.getInstance().getTextureManager().bind(VIGNETTE_TEX_PATH);

        GlUtil.drawRect(MANAGER.getScreen(), new Rect(256, 256), color);

        Minecraft.getInstance().getTextureManager().bind(AbstractGui.field_110324_m);
        GlUtil.blendFuncSafe(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ZERO, DestFactor.ONE);
        return null;
    }

    /**
     * @return The distance from the world border at which a player will start
     * to see a warning.
     */
    private double getWarningDistance(WorldBorder worldBorder) {
        // The distance the border will move within the warning time
        double warningTimeDistance = worldBorder.getLerpSpeed() // meters/millis
            * worldBorder.getWarningTime() * 1000; // millis

        // Border cannot move further than the target size
        double remainingResize = Math.abs(worldBorder.getLerpTarget() - worldBorder.getSize());
        warningTimeDistance = Math.min(warningTimeDistance, remainingResize);

        // Warn by distance and time
        // The larger distance triggers a warning first
        return Math.max(
            worldBorder.getWarningBlocks(),
            warningTimeDistance
        );
    }
}
