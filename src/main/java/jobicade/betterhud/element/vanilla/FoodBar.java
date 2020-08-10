package jobicade.betterhud.element.vanilla;

import static jobicade.betterhud.BetterHud.MC;

import jobicade.betterhud.element.settings.SettingBoolean;
import jobicade.betterhud.events.OverlayContext;
import jobicade.betterhud.events.OverlayHook;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.util.bars.StatBarFood;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;

public class FoodBar extends Bar {
    private SettingBoolean hideMount;

    public FoodBar() {
        super("food", new StatBarFood());

        settings.addChild(hideMount = new SettingBoolean("hideMount"));
    }

    public boolean shouldRenderPrecheck() {
        return !(hideMount.get() && MC.player.isRiding());
    }

    @Override
    public boolean shouldRender(OverlayContext context) {
        return OverlayHook.shouldRenderBars()
            && GuiIngameForge.renderFood
            && !OverlayHook.pre(context.getEvent(), ElementType.FOOD)
            && super.shouldRender(context);
    }

    @Override
    public Rect render(OverlayContext context) {
        Rect rect = super.render(context);
        OverlayHook.post(context.getEvent(), ElementType.FOOD);
        return rect;
    }
}
