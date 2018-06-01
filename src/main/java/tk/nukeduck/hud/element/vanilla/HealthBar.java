package tk.nukeduck.hud.element.vanilla;

import static tk.nukeduck.hud.BetterHud.MC;

import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.Event;
import tk.nukeduck.hud.util.bars.StatBarHealth;

public class HealthBar extends Bar {
	public HealthBar() {
		super("health", new StatBarHealth());
	}

	@Override
	public void loadDefaults() {
		super.loadDefaults();
		settings.priority.set(3);
		side.setIndex(0);
	}

	@Override
	protected ElementType getType() {
		return ElementType.HEALTH;
	}

	@Override
	public boolean shouldRender(Event event) {
		return super.shouldRender(event) && MC.playerController.shouldDrawHUD();
	}
}
