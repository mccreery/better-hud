package tk.nukeduck.hud.element;

import static tk.nukeduck.hud.BetterHud.MANAGER;
import static tk.nukeduck.hud.BetterHud.MC;

import org.lwjgl.opengl.GL11;

import net.minecraftforge.client.event.RenderGameOverlayEvent;
import tk.nukeduck.hud.element.settings.Legend;
import tk.nukeduck.hud.element.settings.SettingPosition;
import tk.nukeduck.hud.element.settings.SettingSlider;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Direction;

public class HungerIndicator extends HudElement {
	private final SettingPosition position = new SettingPosition("position", Direction.CENTER, Direction.SOUTH);
	private final SettingSlider maxLimit = new SettingSlider("maxLimit", 0, 10, .5);

	@Override
	public void loadDefaults() {
		this.settings.set(true);
		position.set(Direction.SOUTH);
		maxLimit.set(9.5);
	}

	public HungerIndicator() {
		super("foodIndicator");

		settings.add(position);
		settings.add(new Legend("misc"));
		settings.add(maxLimit);
	}

	@Override
	public Bounds render(RenderGameOverlayEvent event) {
		double foodLevel = MC.player.getFoodStats().getFoodLevel();
		double foodMax = this.maxLimit.get() * 2.0;

		if(foodLevel <= foodMax) {
			Bounds bounds;
			if(position.getDirection() != null) {
				if(position.getDirection() == Direction.CENTER) {
					bounds = new Bounds(MANAGER.getResolution().x / 2 + 5, MANAGER.getResolution().y / 2 + 5, 16, 16);
				} else {
					bounds = new Bounds(MANAGER.getResolution().x / 2 + 75, MANAGER.getResolution().y - 56, 16, 16);
				}
			} else {
				bounds = position.applyTo(new Bounds(16, 16));
			}

			double speed = (foodMax - foodLevel) / foodMax * 50.0 + 2.0;
			
			double alpha = Math.sin(System.currentTimeMillis() / 3000.0 * speed) + 1.0 / 2.0;
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glColor4d(1.0, 1.0, 1.0, alpha);
			MC.getTextureManager().bindTexture(HUD_ICONS);

			/*int x = 0, y = 0;

			if(posMode.index == 1) {
				this.pos2.update(event.getResolution(), this.getBounds(event.getResolution(), null));
				x = pos2.x;
				y = pos2.y;
			} else if(pos.value == Direction.CENTER) {
				x = event.getResolution().getScaledWidth() / 2 + 5;
				y = event.getResolution().getScaledHeight() / 2 + 5;
				//RenderUtil.renderItemAlpha(ri, fr, mc.getTextureManager(), beef, width / 2 + 5, height / 2 + 5, );
			} else {
				x = event.getResolution().getScaledWidth() / 2 + 75;
				y = event.getResolution().getScaledHeight() - 56;
				//RenderUtil.renderItemAlpha(ri, fr, mc.getTextureManager(), beef, width / 2 + 75, height - 56, Math.sin(System.currentTimeMillis() % ((mc.thePlayer.getFoodStats().getFoodLevel() + 1) * 100) / 1050.0 * Math.PI));
			}*/
			MC.ingameGUI.drawTexturedModalRect(bounds.x(), bounds.y(), 0, 64, 16, 16);

			return bounds;
		}
		return null;
	}
}
