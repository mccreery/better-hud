package jobicade.betterhud.element.vanilla;

import jobicade.betterhud.element.HealIndicator;
import jobicade.betterhud.element.settings.DirectionOptions;
import jobicade.betterhud.events.OverlayContext;
import jobicade.betterhud.events.OverlayHook;
import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.util.bars.StatBarHealth;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.gui.ForgeIngameGui;

public class HealthBar extends Bar {
    public HealthBar() {
        super("health", new StatBarHealth());
    }

    /** Used by {@link HealIndicator} */
    public Direction getIndicatorSide() {
        if(!position.isCustom() && DirectionOptions.CORNERS.isValid(position.getDirection())) {
            return getContentAlignment().mirrorCol();
        } else {
            return getContentAlignment();
        }
    }

    @Override
    public boolean shouldRender(OverlayContext context) {
        return OverlayHook.shouldRenderBars()
            && ForgeIngameGui.renderHealth
            && !OverlayHook.pre(context.getEvent(), ElementType.HEALTH)
            && super.shouldRender(context);
    }

    @Override
    public Rect render(OverlayContext context) {
        Rect rect = super.render(context);
        OverlayHook.post(context.getEvent(), ElementType.HEALTH);
        return rect;
    }
}
