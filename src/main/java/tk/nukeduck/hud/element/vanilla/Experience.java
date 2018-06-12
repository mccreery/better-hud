package tk.nukeduck.hud.element.vanilla;

import static tk.nukeduck.hud.BetterHud.ICONS;
import static tk.nukeduck.hud.BetterHud.MANAGER;
import static tk.nukeduck.hud.BetterHud.MC;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.Event;
import tk.nukeduck.hud.element.settings.SettingBoolean;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Colors;
import tk.nukeduck.hud.util.Direction;
import tk.nukeduck.hud.util.GlUtil;
import tk.nukeduck.hud.util.Point;

public class Experience extends OverrideElement {
	private final SettingBoolean hideMount = new SettingBoolean("hideMount");

	public Experience() {
		super("experience");
		hideMount.set(true);
	}

	@Override
	protected ElementType getType() {
		return ElementType.EXPERIENCE;
	}

	@Override
	public boolean shouldRender(Event event) {
		return MC.playerController.shouldDrawHUD()
			&& (!hideMount.get() || !MC.player.isRidingHorse())
			&& super.shouldRender(event);
	}

	@Override
	public void loadDefaults() {
		super.loadDefaults();
		settings.priority.set(1);
	}

	@Override
	protected Bounds render(Event event) {
		MC.getTextureManager().bindTexture(ICONS);

		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.disableBlend();

		Bounds bgTexture = new Bounds(0, 64, 182, 5);
		Bounds fgTexture = new Bounds(0, 69, 182, 5);

		Bounds barBounds = MANAGER.position(Direction.SOUTH, new Bounds(bgTexture), false, 1);
		GlUtil.drawTexturedProgressBar(barBounds.getPosition(), bgTexture, fgTexture, MC.player.experience, Direction.EAST);

		if(MC.player.experienceLevel > 0) {
			String numberText = String.valueOf(MC.player.experienceLevel);
			Point numberPosition = Direction.NORTH.anchor(new Bounds(GlUtil.getStringSize(numberText)), barBounds).getPosition().sub(0, 6);

			GlUtil.drawBorderedString(numberText, numberPosition.getX(), numberPosition.getY(), Colors.fromRGB(128, 255, 32));
		}

		GlStateManager.enableBlend();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

		return barBounds;
	}
}
