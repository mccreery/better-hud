package jobicade.betterhud.element.text;

import static jobicade.betterhud.BetterHud.MC;

import java.util.Arrays;
import java.util.List;

import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.util.MathUtil;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.eventbus.api.Event;

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
		return Arrays.asList(I18n.format("betterHud.hud.saturation", MathUtil.formatToPlaces(MC.player.getFoodStats().getSaturationLevel(), 1)));
	}
}
