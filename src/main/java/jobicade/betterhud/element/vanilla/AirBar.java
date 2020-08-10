package jobicade.betterhud.element.vanilla;

import jobicade.betterhud.events.OverlayContext;
import jobicade.betterhud.events.OverlayHook;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.util.bars.StatBarAir;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.gui.ForgeIngameGui;

public class AirBar extends Bar {
    public AirBar() {
        super("airBar", new StatBarAir());
    }

    @Override
    public boolean shouldRender(OverlayContext context) {
        return OverlayHook.shouldRenderBars()
            && ForgeIngameGui.renderAir
            && !OverlayHook.pre(context.getEvent(), ElementType.AIR)
            && super.shouldRender(context);
    }

    @Override
    public Rect render(OverlayContext context) {
        Rect rect = super.render(context);
        OverlayHook.post(context.getEvent(), ElementType.AIR);
        return rect;
    }
}
