package jobicade.betterhud.element.vanilla;

import jobicade.betterhud.element.OverlayElement;
import jobicade.betterhud.element.settings.SettingPosition;
import jobicade.betterhud.events.RenderEvents;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;

public abstract class OverrideElement extends OverlayElement {
	protected OverrideElement(String name) {
		super(name);
	}

	protected OverrideElement(String name, SettingPosition position) {
		super(name, position);
	}

	protected abstract ElementType getType();

	private static boolean safePost(Event event) {
		//RenderEvents.endOverlayState(); // Never disable blend
		boolean cancel = MinecraftForge.EVENT_BUS.post(event);
		RenderEvents.beginOverlayState();

		return cancel;
	}

	@Override
	public boolean shouldRender(RenderGameOverlayEvent context) {
		Event child = new RenderGameOverlayEvent.Pre(context, getType());

		return !safePost(child);
	}

	@Override
	protected void postRender(RenderGameOverlayEvent context) {
		ElementType type = getType();

		if(type != null) {
			safePost(new RenderGameOverlayEvent.Post(context, type));
		}
	}
}
