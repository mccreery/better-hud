package jobicade.betterhud.element.vanilla;

import static jobicade.betterhud.BetterHud.MC;

import jobicade.betterhud.element.settings.SettingBoolean;
import jobicade.betterhud.events.OverlayContext;
import jobicade.betterhud.events.OverlayHook;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.util.bars.StatBarFood;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.gui.ForgeIngameGui;

public class FoodBar extends Bar {
    private SettingBoolean hideMount;

    public FoodBar() {
        super("food", new StatBarFood());

        hideMount = new SettingBoolean("hideMount");
        addSetting(hideMount);
    }

    public boolean shouldRenderPrecheck() {
        return !(hideMount.get() && MC.player.isPassenger());
    }

    @Override
    public boolean shouldRender(OverlayContext context) {
        return OverlayHook.shouldRenderBars()
            && ForgeIngameGui.renderFood
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
