package jobicade.betterhud.element.vanilla;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.Event;
import jobicade.betterhud.element.settings.Setting;
import jobicade.betterhud.element.settings.SettingBoolean;
import jobicade.betterhud.util.bars.StatBarFood;

public class FoodBar extends Bar {
	private SettingBoolean hideMount;

	public FoodBar() {
		super("food", new StatBarFood());
	}

	@Override
	protected void addSettings(List<Setting<?>> settings) {
		super.addSettings(settings);
		settings.add(hideMount = new SettingBoolean("hideMount"));
	}

	@Override
	public void loadDefaults() {
		super.loadDefaults();

		settings.priority.set(3);
		side.setIndex(1);
		hideMount.set(true);
	}

	@Override
	protected ElementType getType() {
		return ElementType.FOOD;
	}

	@Override
	public boolean shouldRender(Event event) {
		return Minecraft.getMinecraft().playerController.shouldDrawHUD()
			&& (!hideMount.get() || !Minecraft.getMinecraft().player.isRidingHorse())
			&& super.shouldRender(event);
	}
}
