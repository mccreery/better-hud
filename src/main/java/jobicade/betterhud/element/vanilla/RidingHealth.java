package jobicade.betterhud.element.vanilla;

import jobicade.betterhud.events.OverlayContext;
import jobicade.betterhud.events.OverlayHook;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.util.bars.StatBarMount;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;

public class RidingHealth extends Bar {
    public RidingHealth() {
        super("mountHealth", new StatBarMount());
    }

    @Override
    public boolean shouldRender(OverlayContext context) {
        return OverlayHook.shouldRenderBars()
            && GuiIngameForge.renderHealthMount
            && !OverlayHook.pre(context.getEvent(), ElementType.HEALTHMOUNT)
            && super.shouldRender(context);
    }

    @Override
    public Rect render(OverlayContext context) {
        Rect rect = super.render(context);
        OverlayHook.post(context.getEvent(), ElementType.HEALTHMOUNT);
        return rect;
    }
}
