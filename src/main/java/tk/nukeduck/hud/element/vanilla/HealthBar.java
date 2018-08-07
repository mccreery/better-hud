package tk.nukeduck.hud.element.vanilla;

import static tk.nukeduck.hud.BetterHud.MC;

import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.Event;
import tk.nukeduck.hud.element.HealIndicator;
import tk.nukeduck.hud.util.Direction;
import tk.nukeduck.hud.util.Direction.Options;
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

	/** Used by {@link HealIndicator} */
	public Direction getIndicatorSide() {
		if(!position.isCustom() && Options.CORNERS.isValid(position.getDirection())) {
			return super.getContentAlignment().mirrorColumn();
		} else {
			return super.getContentAlignment();
		}
	}

	@Override
	public boolean shouldRender(Event event) {
		return MC.playerController.shouldDrawHUD() && super.shouldRender(event);
	}
}
