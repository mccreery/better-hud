package jobicade.betterhud.element;

import static jobicade.betterhud.BetterHud.HUD_ICONS;
import static jobicade.betterhud.BetterHud.MC;
import static jobicade.betterhud.BetterHud.SPACER;

import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.common.eventhandler.Event;
import jobicade.betterhud.element.settings.Legend;
import jobicade.betterhud.element.settings.Setting;
import jobicade.betterhud.element.settings.SettingChoose;
import jobicade.betterhud.util.geom.Rect;
import jobicade.betterhud.util.render.Color;
import jobicade.betterhud.util.geom.Direction;

public class HealIndicator extends HudElement {
	private SettingChoose mode;

	@Override
	public void loadDefaults() {
		super.loadDefaults();

		position.setPreset(Direction.NORTH_WEST);
		mode.setIndex(1);
	}

	public HealIndicator() {
		super("healIndicator");
	}

	@Override
	protected void addSettings(List<Setting<?>> settings) {
		super.addSettings(settings);
		settings.add(new Legend("misc"));
		settings.add(mode = new SettingChoose(2));
	}

	@Override
	public Rect render(Event event) {
			String healIndicator = I18n.format("betterHud.strings.healIndicator");
			Rect bounds = mode.getIndex() == 0 ? new Rect(MC.fontRenderer.getStringWidth(healIndicator), MC.fontRenderer.FONT_HEIGHT) : new Rect(9, 9);

			if(position.isCustom()) {
				bounds = position.applyTo(bounds);
			} else {
				Direction side = HudElement.HEALTH.getIndicatorSide();
				bounds = bounds.align(HudElement.HEALTH.getLastRect().grow(SPACER, 0, SPACER, 0).getAnchor(side), side.mirrorCol());
			}

			if(mode.getIndex() == 0) {
				MC.ingameGUI.drawString(MC.fontRenderer, healIndicator, bounds.getX(), bounds.getY(), Color.GREEN.getPacked());
			} else {
				MC.getTextureManager().bindTexture(HUD_ICONS);
				MC.ingameGUI.drawTexturedModalRect(bounds.getX(), bounds.getY(), 0, 80, 9, 9);
			}
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
