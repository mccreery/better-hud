package jobicade.betterhud.element.entityinfo;

import jobicade.betterhud.element.HudElement;
import jobicade.betterhud.events.RenderMobInfoEvent;

public abstract class EntityInfo extends HudElement<RenderMobInfoEvent> {
    public EntityInfo(String name) {
        super(name);
    }
}
