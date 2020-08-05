package jobicade.betterhud.element.vanilla;

import jobicade.betterhud.events.OverlayContext;
import jobicade.betterhud.events.OverlayHook;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.util.bars.StatBarArmor;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;

public class ArmorBar extends Bar {
    public ArmorBar() {
        super("armor", new StatBarArmor());
    }

    @Override
    public boolean shouldRender(OverlayContext context) {
        return OverlayHook.shouldRenderBars()
            && GuiIngameForge.renderArmor
            && !OverlayHook.pre(context.getEvent(), ElementType.ARMOR)
            && super.shouldRender(context);
    }

    @Override
    public Rect render(OverlayContext context) {
        Rect rect = super.render(context);
        OverlayHook.post(context.getEvent(), ElementType.ARMOR);
        return rect;
    }
}
