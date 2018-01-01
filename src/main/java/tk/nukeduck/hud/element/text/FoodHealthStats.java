package tk.nukeduck.hud.element.text;

import static tk.nukeduck.hud.BetterHud.MC;

import net.minecraft.client.resources.I18n;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import tk.nukeduck.hud.element.settings.Legend;
import tk.nukeduck.hud.element.settings.SettingBoolean;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Colors;
import tk.nukeduck.hud.util.Direction;
import tk.nukeduck.hud.util.LayoutManager;

public class FoodHealthStats extends TextElement {
	private final SettingBoolean saturation = new SettingBoolean("saturation");

	@Override
	public void loadDefaults() {
		super.loadDefaults();
		saturation.set(true);
		position.load(Direction.SOUTH_EAST);
	}

	public FoodHealthStats() {
		super("foodHealthStats");

		settings.add(new Legend("misc"));
		settings.add(saturation);
		settings.add(new Legend("fhNotice"));
	}

	@Override
	public Bounds render(RenderGameOverlayEvent event, LayoutManager manager) {
		String health = String.valueOf(((int)MC.player.getHealth()) / 2.0f);
		String food = String.valueOf(MC.player.getFoodStats().getFoodLevel() / 2.0F);

		int center = event.getResolution().getScaledWidth() / 2;
		int textY = event.getResolution().getScaledHeight() - 35;
		int healthWidth = MC.fontRenderer.getStringWidth(health);

		MC.ingameGUI.drawString(MC.fontRenderer, food, center + 95, textY, Colors.WHITE);
		MC.ingameGUI.drawString(MC.fontRenderer, health, center - 95 - healthWidth, textY, Colors.WHITE);

		return super.render(event, manager);
	}

	@Override
	protected String[] getText() {
		if(!this.saturation.get()) return new String[] {};
		return new String[] {I18n.format("betterHud.strings.saturation",
			Math.round(MC.player.getFoodStats().getSaturationLevel() * 10) / 10.0f)};
	}
}
