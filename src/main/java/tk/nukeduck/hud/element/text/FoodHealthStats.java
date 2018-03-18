package tk.nukeduck.hud.element.text;

import static tk.nukeduck.hud.BetterHud.MANAGER;
import static tk.nukeduck.hud.BetterHud.MC;

import java.util.Arrays;
import java.util.List;

import net.minecraftforge.fml.common.eventhandler.Event;
import tk.nukeduck.hud.element.settings.Legend;
import tk.nukeduck.hud.element.settings.SettingBoolean;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Colors;
import tk.nukeduck.hud.util.Direction;
import tk.nukeduck.hud.util.FormatUtil;

public class FoodHealthStats extends TextElement {
	private final SettingBoolean saturation = new SettingBoolean("saturation").setUnlocalizedValue(SettingBoolean.VISIBLE);

	@Override
	public void loadDefaults() {
		super.loadDefaults();

		position.set(Direction.SOUTH_EAST);
		saturation.set(true);
	}

	public FoodHealthStats() {
		super("foodHealthStats");

		settings.add(new Legend("misc"));
		settings.add(saturation);
	}

	@Override
	public boolean shouldRender(Event event) {
		return super.shouldRender(event) && MC.playerController.gameIsSurvivalOrAdventure();
	}

	@Override
	public Bounds render(Event event) {
		String health = String.valueOf(((int)MC.player.getHealth()) / 2.0f);
		String food = String.valueOf(MC.player.getFoodStats().getFoodLevel() / 2.0F);

		int center = MANAGER.getResolution().x / 2;
		int textY = MANAGER.getResolution().y - 35;
		int healthWidth = MC.fontRenderer.getStringWidth(health);

		MC.ingameGUI.drawString(MC.fontRenderer, food, center + 95, textY, Colors.WHITE);
		MC.ingameGUI.drawString(MC.fontRenderer, health, center - 95 - healthWidth, textY, Colors.WHITE);

		return super.render(event);
	}

	@Override
	protected List<String> getText() {
		if(!saturation.get()) return null;

		return Arrays.asList(saturation.getLocalizedName() + ": "
			+ FormatUtil.formatToPlaces(MC.player.getFoodStats().getSaturationLevel(), 1));
	}
}
