package jobicade.betterhud.element.entityinfo;

import jobicade.betterhud.element.HudElement;
import jobicade.betterhud.events.HudPhase;
import jobicade.betterhud.events.RenderMobInfoEvent;
import net.minecraftforge.fml.common.eventhandler.Event;

public abstract class EntityInfo extends HudElement {
	protected EntityInfo(String name) {
		super(name, HudPhase.ENTITY_BILLBOARD);
	}

	@Override
	public boolean shouldRender(Event event) {
		return event instanceof RenderMobInfoEvent;
	}
}
