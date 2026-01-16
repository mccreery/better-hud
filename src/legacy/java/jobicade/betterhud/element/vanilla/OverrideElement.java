package jobicade.betterhud.element.vanilla;

import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import jobicade.betterhud.element.HudElement;
import jobicade.betterhud.element.settings.SettingPosition;
import jobicade.betterhud.events.RenderEvents;

public abstract class OverrideElement extends HudElement {
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
    public boolean shouldRender(Event event) {
        if(!(event instanceof RenderGameOverlayEvent)) {
            return false;
        }
        RenderGameOverlayEvent parent = (RenderGameOverlayEvent)event;
        Event child = new RenderGameOverlayEvent.Pre(parent, getType());

        return !safePost(child);
    }

    @Override
    protected void postRender(Event event) {
        if(event instanceof RenderGameOverlayEvent) {
            ElementType type = getType();

            if(type != null) {
                RenderGameOverlayEvent e = (RenderGameOverlayEvent)event;
                safePost(new RenderGameOverlayEvent.Post(e, type));
            }
        }
    }
}
