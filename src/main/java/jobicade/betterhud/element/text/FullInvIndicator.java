package jobicade.betterhud.element.text;

import java.util.Arrays;
import java.util.List;

import jobicade.betterhud.element.settings.Legend;
import jobicade.betterhud.element.settings.SettingBoolean;
import jobicade.betterhud.geom.Direction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public class FullInvIndicator extends TextElement {
	private SettingBoolean offHand;

	@Override
	public void loadDefaults() {
		super.loadDefaults();

		position.setPreset(Direction.NORTH_EAST);
		offHand.set(false);
	}

	public FullInvIndicator() {
		setName("fullInvIndicator");

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
	public boolean shouldRender(RenderGameOverlayEvent context) {
		return Minecraft.getMinecraft().player.inventory.getFirstEmptyStack() == -1 &&
			(!offHand.get() || !Minecraft.getMinecraft().player.inventory.offHandInventory.get(0).isEmpty());
	}
}
