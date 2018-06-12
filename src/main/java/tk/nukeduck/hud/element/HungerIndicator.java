package tk.nukeduck.hud.element;

import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static tk.nukeduck.hud.BetterHud.HUD_ICONS;
import static tk.nukeduck.hud.BetterHud.MANAGER;
import static tk.nukeduck.hud.BetterHud.MC;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.eventhandler.Event;
import tk.nukeduck.hud.element.settings.Legend;
import tk.nukeduck.hud.element.settings.SettingPosition;
import tk.nukeduck.hud.element.settings.SettingSlider;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Direction;

public class HungerIndicator extends HudElement {
	private final SettingPosition position = new SettingPosition("position", Direction.CENTER, Direction.SOUTH);
	private final SettingSlider maxLimit = new SettingSlider("maxLimit", 0, 20, 1).setDisplayPlaces(1).setDisplayScale(.5);

	@Override
	public void loadDefaults() {
		super.loadDefaults();

		position.set(Direction.SOUTH);
		maxLimit.set(19);
	}

	public HungerIndicator() {
		super("foodIndicator");

		settings.add(position);
		settings.add(new Legend("misc"));
		settings.add(maxLimit);
	}

	@Override
	public boolean shouldRender(Event event) {
		return super.shouldRender(event)
			&& MC.playerController.gameIsSurvivalOrAdventure()
			&& MC.player.getFoodStats().getFoodLevel() < maxLimit.getInt() * 2;
	}

	@Override
	public Bounds render(Event event) {
		int foodLevel = MC.player.getFoodStats().getFoodLevel();
		int foodMax = this.maxLimit.getInt() * 2;

		Bounds bounds;
		if(position.getDirection() != null) {
			if(position.getDirection() == Direction.CENTER) {
				bounds = new Bounds(MANAGER.getResolution().getX() / 2 + 5, MANAGER.getResolution().getY() / 2 + 5, 16, 16);
			} else {
				bounds = new Bounds(MANAGER.getResolution().getX() / 2 + 75, MANAGER.getResolution().getY() - 56, 16, 16);
			}
		} else {
			bounds = position.applyTo(new Bounds(16, 16));
		}

		float speed = (foodMax - foodLevel) / foodMax * 50 + 2;
		float alpha = (MathHelper.sin(System.currentTimeMillis() / 3000 * speed) + 1 / 2);

		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.color(1, 1, 1, alpha);

		MC.getTextureManager().bindTexture(HUD_ICONS);
		MC.ingameGUI.drawTexturedModalRect(bounds.getX(), bounds.getY(), 0, 64, 16, 16);

		return bounds;
	}
}
