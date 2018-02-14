package tk.nukeduck.hud.element;

import static tk.nukeduck.hud.BetterHud.HUD_ICONS;
import static tk.nukeduck.hud.BetterHud.MANAGER;
import static tk.nukeduck.hud.BetterHud.MC;

import net.minecraft.client.resources.I18n;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import tk.nukeduck.hud.element.settings.Legend;
import tk.nukeduck.hud.element.settings.SettingChoose;
import tk.nukeduck.hud.element.settings.SettingPosition;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Colors;
import tk.nukeduck.hud.util.Direction;

public class HealIndicator extends HudElement {
	private final SettingPosition position = new SettingPosition("position", 0);
	private final SettingChoose mode = new SettingChoose(2);

	@Override
	public void loadDefaults() {
		this.settings.set(true);
		position.set(Direction.NORTH_WEST);
		mode.setIndex(1);
	}

	public HealIndicator() {
		super("healIndicator");

		settings.add(position);
		this.settings.add(new Legend("misc"));
		this.settings.add(mode);
	}

	@Override
	public Bounds render(RenderGameOverlayEvent event) {
			String healIndicator = I18n.format("betterHud.strings.healIndicator");
			Bounds bounds = mode.getIndex() == 0 ? new Bounds(MC.fontRenderer.getStringWidth(healIndicator), MC.fontRenderer.FONT_HEIGHT) : new Bounds(9, 9);

			if(position.isAbsolute()) {
				position.applyTo(bounds);
			} else {
				bounds.position = MANAGER.getResolution().scale(.5f, 1).sub(90, 50);
			}

			if(mode.getIndex() == 0) {
				MC.ingameGUI.drawString(MC.fontRenderer, healIndicator, bounds.x(), bounds.y(), Colors.GREEN);
			} else {
				MC.getTextureManager().bindTexture(HUD_ICONS);
				MC.ingameGUI.drawTexturedModalRect(bounds.x(), bounds.y(), 0, 80, 9, 9);
			}
			return bounds;
	}

	/** @see net.minecraft.util.FoodStats#onUpdate(net.minecraft.entity.player.EntityPlayer) */
	@Override
	public boolean shouldRender() {
		return MC.world.getGameRules().getBoolean("naturalRegeneration") && MC.player.getFoodStats().getFoodLevel() >= 18 && MC.player.shouldHeal();
	}
}
