package jobicade.betterhud.element.text;

import java.util.Arrays;
import java.util.List;

import jobicade.betterhud.events.OverlayContext;
import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.util.MathUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

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
	public boolean shouldRender(OverlayContext context) {
		return Minecraft.getMinecraft().playerController.gameIsSurvivalOrAdventure();
	}

	@Override
	protected List<String> getText() {
		return Arrays.asList(I18n.format("betterHud.hud.saturation", MathUtil.formatToPlaces(Minecraft.getMinecraft().player.getFoodStats().getSaturationLevel(), 1)));
	}
}
