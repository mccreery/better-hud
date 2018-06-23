package tk.nukeduck.hud.element.text;

import static tk.nukeduck.hud.BetterHud.MC;

import java.util.Arrays;
import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.common.eventhandler.Event;
import tk.nukeduck.hud.util.Direction;
import tk.nukeduck.hud.util.FormatUtil;

public class Saturation extends TextElement {
	public Saturation() {
		super("saturation");
	}

	@Override
	public void loadDefaults() {
		super.loadDefaults();
		position.setPreset(Direction.SOUTH_EAST);
	}

	@Override
	public boolean shouldRender(Event event) {
		return super.shouldRender(event) && MC.playerController.gameIsSurvivalOrAdventure();
	}

	@Override
	protected List<String> getText() {
		return Arrays.asList(I18n.format("betterHud.hud.saturation", FormatUtil.formatToPlaces(MC.player.getFoodStats().getSaturationLevel(), 1)));
	}
}
