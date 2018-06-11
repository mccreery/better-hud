package tk.nukeduck.hud.element.vanilla;

import static tk.nukeduck.hud.BetterHud.MC;

import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.Event;
import tk.nukeduck.hud.util.bars.StatBarMount;

public class RidingHealth extends Bar {
	public RidingHealth() {
		super("mountHealth", new StatBarMount());
	}

	@Override
	public void loadDefaults() {
		super.loadDefaults();
		settings.priority.set(5);
		side.setIndex(1);
	}

	@Override
	protected ElementType getType() {
		return ElementType.HEALTHMOUNT;
	}

	@Override
	public boolean shouldRender(Event event) {
		return MC.playerController.shouldDrawHUD() && super.shouldRender(event);
	}
}
