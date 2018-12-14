package jobicade.betterhud.element.vanilla;

import static jobicade.betterhud.BetterHud.MC;

import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.Event;
import jobicade.betterhud.element.HealIndicator;
import jobicade.betterhud.util.Direction;
import jobicade.betterhud.util.Direction.Options;
import jobicade.betterhud.util.bars.StatBarHealth;

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
			return getContentAlignment().mirrorColumn();
		} else {
			return getContentAlignment();
		}
	}

	@Override
	public boolean shouldRender(Event event) {
		return MC.playerController.shouldDrawHUD() && super.shouldRender(event);
	}
}
