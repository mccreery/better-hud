package tk.nukeduck.hud.element.vanilla;

import static tk.nukeduck.hud.BetterHud.MC;

import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import tk.nukeduck.hud.util.Bounds;

public class HealthBar extends Bar {
	public HealthBar() {
		super("health", new Bounds(16, 0, 9, 9), new Bounds(61, 0, 9, 9), new Bounds(52, 0, 9, 9));
	}

	@Override
	protected ElementType getType() {
		return ElementType.HEALTH;
	}

	@Override
	protected int getCurrent() {
		return (int)MC.player.getHealth();
	}

	@Override
	protected int getMaximum() {
		return (int)MC.player.getMaxHealth();
	}
}
