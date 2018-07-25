package tk.nukeduck.hud.element;

import static tk.nukeduck.hud.BetterHud.HUD_ICONS;
import static tk.nukeduck.hud.BetterHud.MC;
import static tk.nukeduck.hud.BetterHud.SPACER;

import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.common.eventhandler.Event;
import tk.nukeduck.hud.element.settings.Legend;
import tk.nukeduck.hud.element.settings.SettingChoose;
import tk.nukeduck.hud.element.settings.SettingPosition;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Colors;
import tk.nukeduck.hud.util.Direction;
import tk.nukeduck.hud.util.Direction.Options;
import tk.nukeduck.hud.util.GlUtil;

public class HealIndicator extends HudElement {
	private final SettingPosition position = new SettingPosition("position", Options.NONE, Options.NONE);
	private final SettingChoose mode = new SettingChoose(2);

	@Override
	public void loadDefaults() {
		super.loadDefaults();

		position.setPreset(Direction.NORTH_WEST);
		mode.setIndex(1);
	}

	public HealIndicator() {
		super("healIndicator");

		settings.add(position);
		this.settings.add(new Legend("misc"));
		this.settings.add(mode);
	}

	@Override
	public Bounds render(Event event) {
			String healIndicator = I18n.format("betterHud.strings.healIndicator");
			Bounds bounds = mode.getIndex() == 0 ? new Bounds(MC.fontRenderer.getStringWidth(healIndicator), MC.fontRenderer.FONT_HEIGHT) : new Bounds(9, 9);

			if(position.isCustom()) {
				bounds = position.applyTo(bounds);
			} else {
				bounds = bounds.anchoredTo(HudElement.HEALTH.getLastBounds()
					.withPadding(bounds.getWidth() + SPACER, 0, bounds.getWidth() + SPACER, 0),
					HudElement.HEALTH.getIndicatorSide());
			}

			GlUtil.color(Colors.WHITE);

			if(mode.getIndex() == 0) {
				MC.ingameGUI.drawString(MC.fontRenderer, healIndicator, bounds.getX(), bounds.getY(), Colors.GREEN);
			} else {
				MC.getTextureManager().bindTexture(HUD_ICONS);
				MC.ingameGUI.drawTexturedModalRect(bounds.getX(), bounds.getY(), 0, 80, 9, 9);
			}

			GlUtil.color(Colors.WHITE);
			return bounds;
	}

	/** @see net.minecraft.util.FoodStats#onUpdate(net.minecraft.entity.player.EntityPlayer) */
	@Override
	public boolean shouldRender(Event event) {
		return super.shouldRender(event)
			&& MC.playerController.gameIsSurvivalOrAdventure()
			&& MC.world.getGameRules().getBoolean("naturalRegeneration")
			&& MC.player.getFoodStats().getFoodLevel() >= 18
			&& MC.player.shouldHeal();
	}
}
