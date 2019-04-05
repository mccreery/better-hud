package jobicade.betterhud.element.vanilla;

import static jobicade.betterhud.BetterHud.MC;

import jobicade.betterhud.util.bars.StatBarArmor;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.eventbus.api.Event;

public class ArmorBar extends Bar {
	public ArmorBar() {
		super("armor", new StatBarArmor());
	}

	@Override
	public void loadDefaults() {
		super.loadDefaults();
		settings.priority.set(4);
		side.setIndex(0);
	}

	@Override
	protected ElementType getType() {
		return ElementType.ARMOR;
	}

	@Override
	public boolean shouldRender(Event event) {
		return MC.playerController.shouldDrawHUD() && super.shouldRender(event);
	}
}
