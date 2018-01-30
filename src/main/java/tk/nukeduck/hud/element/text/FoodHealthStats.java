package tk.nukeduck.hud.element.text;

import static tk.nukeduck.hud.BetterHud.MC;

import net.minecraftforge.client.event.RenderGameOverlayEvent;
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
		saturation.set(true);
		position.set(Direction.SOUTH_EAST);
	}

	public FoodHealthStats() {
		super("foodHealthStats");

		settings.add(new Legend("misc"));
		settings.add(saturation);
		settings.add(new Legend("fhNotice"));
	}

	@Override
	public Bounds render(RenderGameOverlayEvent event) {
		String health = String.valueOf(((int)MC.player.getHealth()) / 2.0f);
		String food = String.valueOf(MC.player.getFoodStats().getFoodLevel() / 2.0F);

		int center = event.getResolution().getScaledWidth() / 2;
		int textY = event.getResolution().getScaledHeight() - 35;
		int healthWidth = MC.fontRenderer.getStringWidth(health);

		MC.ingameGUI.drawString(MC.fontRenderer, food, center + 95, textY, Colors.WHITE);
		MC.ingameGUI.drawString(MC.fontRenderer, health, center - 95 - healthWidth, textY, Colors.WHITE);

		return super.render(event);
	}

	@Override
	protected String[] getText() {
		if(saturation.get()) {
			return new String[] {
				saturation.getLocalizedName() + ": " + FormatUtil.formatToPlaces(MC.player.getFoodStats().getSaturationLevel(), 1)
			};
		} else {
			return new String[0];
		}
	}
}
