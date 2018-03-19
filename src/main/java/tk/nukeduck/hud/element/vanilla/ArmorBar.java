package tk.nukeduck.hud.element.vanilla;

import static tk.nukeduck.hud.BetterHud.MC;

import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.Event;
import tk.nukeduck.hud.util.Bounds;

public class ArmorBar extends Bar {
	public ArmorBar() {
		super("armor", new Bounds(16, 9, 9, 9), new Bounds(25, 9, 9, 9), new Bounds(34, 9, 9, 9));
	}

	@Override
	public void loadDefaults() {
		super.loadDefaults();
		settings.priority.set(4);
		side.setIndex(0);
	}

	@Override
	public boolean shouldRender(Event event) {
		return super.shouldRender(event) && getCurrent() > 0;
	}

	@Override
	protected int getCurrent() {
		return MC.player.getTotalArmorValue();
	}

	@Override
	protected int getMaximum() {
		return 20;
	}

	@Override
	protected ElementType getType() {
		return ElementType.ARMOR;
	}
}
