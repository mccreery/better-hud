package tk.nukeduck.hud.element.vanilla;

import static tk.nukeduck.hud.BetterHud.MC;

import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import tk.nukeduck.hud.element.HudElement;
import tk.nukeduck.hud.util.Bounds;

public abstract class OverrideElement extends HudElement {
	protected OverrideElement(String name) {
		super(name);
	}

	protected abstract ElementType getType();

	@Override
	public boolean shouldRender(Event event) {
		return super.shouldRender(event) && !MinecraftForge.EVENT_BUS.post(
			new RenderGameOverlayEvent.Pre((RenderGameOverlayEvent)event, getType()));
	}

	@Override
	public void tryRender(Event event) {
		if(isEnabled() && shouldRender(event)) {
			MC.mcProfiler.startSection(name);

			Bounds bounds = render(event);
			if(bounds != null) {
				lastBounds = bounds;
			}
			MinecraftForge.EVENT_BUS.post(new RenderGameOverlayEvent.Post((RenderGameOverlayEvent)event, getType()));

			MC.mcProfiler.endSection();
		}
	}
}
