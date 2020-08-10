package jobicade.betterhud.element.vanilla;

import static jobicade.betterhud.BetterHud.MC;

import jobicade.betterhud.element.OverlayElement;
import jobicade.betterhud.events.OverlayContext;
import jobicade.betterhud.events.OverlayHook;
import jobicade.betterhud.geom.Rect;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;

public class BossHealth extends OverlayElement {
    public BossHealth() {
        super("bossHealth");
    }

    @Override
    public boolean shouldRender(OverlayContext context) {
        return GuiIngameForge.renderBossHealth
            && !OverlayHook.pre(context.getEvent(), ElementType.BOSSHEALTH);
    }

    @Override
    public Rect render(OverlayContext context) {
        // Vanilla stores current boss bars in a private map so the size cannot
        // be determined and the bars cannot be moved
        MC.ingameGUI.getBossOverlay().renderBossHealth();

        OverlayHook.post(context.getEvent(), ElementType.BOSSHEALTH);
        return null;
    }
}
