package jobicade.betterhud.element;

import net.minecraftforge.client.event.RenderGameOverlayEvent;

public abstract class OverlayElement extends HudElement<RenderGameOverlayEvent> {
    public OverlayElement(String name) {
        super(name);
    }
}
