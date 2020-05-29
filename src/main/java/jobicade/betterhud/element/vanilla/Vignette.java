package jobicade.betterhud.element.vanilla;

import static jobicade.betterhud.BetterHud.MANAGER;

import java.util.List;

import jobicade.betterhud.element.settings.Setting;
import jobicade.betterhud.element.settings.SettingBoolean;
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
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;

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
	public boolean shouldRender(RenderGameOverlayEvent context) {
		return Minecraft.isFancyGraphicsEnabled();
	}

	@Override
	public Rect render(RenderGameOverlayEvent context) {
		WorldBorder border = Minecraft.getMinecraft().world.getWorldBorder();

		float distance = (float)border.getClosestDistance(Minecraft.getMinecraft().player);
		float warningDistance = (float)getWarningDistance(border);

		float f;
		if(distance < warningDistance) {
			f = 1 - distance / warningDistance;
		} else {
			f = 0;
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
