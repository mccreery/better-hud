package jobicade.betterhud.events;

import jobicade.betterhud.geom.LayoutManager;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public final class OverlayContext {
    private final RenderGameOverlayEvent event;
    private final LayoutManager layoutManager;

    public OverlayContext(RenderGameOverlayEvent event, LayoutManager layoutManager) {
        this.event = event;
        this.layoutManager = layoutManager;
    }

    public RenderGameOverlayEvent getEvent() {
        return event;
    }

    public float getPartialTicks() {
        return event.getPartialTicks();
    }

    public LayoutManager getLayoutManager() {
        return layoutManager;
    }
}
