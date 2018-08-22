package tk.nukeduck.hud.element.vanilla;

import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import tk.nukeduck.hud.element.HudElement;

public abstract class OverrideElement extends HudElement {
	protected OverrideElement(String name) {
		super(name);
	}

	protected abstract ElementType getType();

	@Override
	public boolean shouldRender(Event event) {
		if(!super.shouldRender(event)) return false;

		ElementType type = getType();
		return type == null || !MinecraftForge.EVENT_BUS.post(
			new RenderGameOverlayEvent.Pre((RenderGameOverlayEvent)event, getType()));
	}

	@Override
	protected void postRender(Event event) {
		if(event instanceof RenderGameOverlayEvent) {
			ElementType type = getType();

			if(type != null) {
				RenderGameOverlayEvent e = (RenderGameOverlayEvent)event;
				MinecraftForge.EVENT_BUS.post(new RenderGameOverlayEvent.Post(e, type));
			}
		}
	}
}
