package jobicade.betterhud.element.vanilla;

import jobicade.betterhud.element.HealIndicator;
import jobicade.betterhud.element.settings.DirectionOptions;
import jobicade.betterhud.events.OverlayContext;
import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.util.bars.StatBarHealth;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.MinecraftForge;

public class HealthBar extends Bar {
	public HealthBar() {
		super("health", new StatBarHealth());
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
	public boolean shouldRender(OverlayContext context) {
		return super.shouldRender(context)
			&& Minecraft.getMinecraft().playerController.shouldDrawHUD()
			&& !MinecraftForge.EVENT_BUS.post(new RenderGameOverlayEvent.Pre(context.getEvent(), ElementType.HEALTH));
	}

	@Override
	public Rect render(OverlayContext context) {
		Rect rect = super.render(context);
		MinecraftForge.EVENT_BUS.post(new RenderGameOverlayEvent.Post(context.getEvent(), ElementType.HEALTH));
		return rect;
	}
}
