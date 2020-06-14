package jobicade.betterhud.element.text;

import java.util.Arrays;
import java.util.List;

import jobicade.betterhud.element.settings.Legend;
import jobicade.betterhud.element.settings.SettingBoolean;
import jobicade.betterhud.events.OverlayContext;
import jobicade.betterhud.geom.Direction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

public class FullInvIndicator extends TextElement {
	private SettingBoolean offHand;

	@Override
	public void loadDefaults() {
		super.loadDefaults();

		position.setPreset(Direction.NORTH_EAST);
		offHand.set(false);
	}

	public FullInvIndicator() {
		super("fullInvIndicator");

		settings.addChildren(
			new Legend("misc"),
			offHand = new SettingBoolean("offhand")
		);
	}

	@Override
	protected List<String> getText() {
		return Arrays.asList(I18n.format("betterHud.hud.fullInv"));
	}

	@Override
	public boolean shouldRender(OverlayContext context) {
		return Minecraft.getMinecraft().player.inventory.getFirstEmptyStack() == -1 &&
			(!offHand.get() || !Minecraft.getMinecraft().player.inventory.offHandInventory.get(0).isEmpty());
	}
}
