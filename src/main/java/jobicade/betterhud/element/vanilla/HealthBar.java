package jobicade.betterhud.element.vanilla;

import jobicade.betterhud.element.HealIndicator;
import jobicade.betterhud.element.settings.DirectionOptions;
import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.util.bars.StatBarHealth;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;

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
		if(!position.isCustom() && DirectionOptions.CORNERS.isValid(position.getDirection())) {
			return getContentAlignment().mirrorCol();
		} else {
			return getContentAlignment();
		}
	}

	@Override
	public boolean shouldRender(RenderGameOverlayEvent context) {
		return Minecraft.getMinecraft().playerController.shouldDrawHUD();
	}
}
