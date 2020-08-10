package jobicade.betterhud.element.vanilla;

import static jobicade.betterhud.BetterHud.MC;

import jobicade.betterhud.element.OverlayElement;
import jobicade.betterhud.events.OverlayContext;
import jobicade.betterhud.events.OverlayHook;
import jobicade.betterhud.geom.Rect;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.gui.ForgeIngameGui;

public class BossHealth extends OverlayElement {
    public BossHealth() {
        super("bossHealth");
    }

    @Override
    public boolean shouldRender(OverlayContext context) {
        return ForgeIngameGui.renderBossHealth
            && !OverlayHook.pre(context.getEvent(), ElementType.BOSSHEALTH);
    }

    @Override
    public Rect render(OverlayContext context) {
        // Vanilla stores current boss bars in a private map so the size cannot
        // be determined and the bars cannot be moved
        MC.ingameGUI.getBossOverlay().render();

        OverlayHook.post(context.getEvent(), ElementType.BOSSHEALTH);
        return null;
    }
}
