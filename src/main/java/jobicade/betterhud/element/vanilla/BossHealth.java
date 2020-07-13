package jobicade.betterhud.element.vanilla;

import jobicade.betterhud.element.OverlayElement;
import jobicade.betterhud.events.OverlayContext;
import jobicade.betterhud.geom.Rect;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.MinecraftForge;

public class BossHealth extends OverlayElement {
    public BossHealth() {
        super("bossHealth");
    }

    @Override
    public boolean shouldRender(OverlayContext context) {
        return GuiIngameForge.renderBossHealth
            && !MinecraftForge.EVENT_BUS.post(new RenderGameOverlayEvent.Pre(context.getEvent(), ElementType.BOSSHEALTH));
    }

    @Override
    public Rect render(OverlayContext context) {
        // Vanilla stores current boss bars in a private map so the size cannot
        // be determined and the bars cannot be moved
        Minecraft.getMinecraft().ingameGUI.getBossOverlay().renderBossHealth();

        MinecraftForge.EVENT_BUS.post(new RenderGameOverlayEvent.Post(context.getEvent(), ElementType.BOSSHEALTH));
        return null;
    }
}
