package jobicade.betterhud.element.text;

import java.util.Arrays;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.common.eventhandler.Event;
import jobicade.betterhud.element.settings.Legend;
import jobicade.betterhud.element.settings.Setting;
import jobicade.betterhud.element.settings.SettingBoolean;
import jobicade.betterhud.geom.Direction;

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
	}

	@Override
	protected void addSettings(List<Setting<?>> settings) {
		super.addSettings(settings);
		settings.add(new Legend("misc"));
		settings.add(offHand = new SettingBoolean("offhand"));
	}

	@Override
	protected List<String> getText() {
		return Arrays.asList(I18n.format("betterHud.hud.fullInv"));
	}

	@Override
	public boolean shouldRender(Event event) {
		return super.shouldRender(event) && Minecraft.getMinecraft().player.inventory.getFirstEmptyStack() == -1 &&
			(!offHand.get() || !Minecraft.getMinecraft().player.inventory.offHandInventory.get(0).isEmpty());
	}
}
