package jobicade.betterhud.events;

import jobicade.betterhud.BetterHud;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.client.GuiIngameForge;

public class OverlayHook {
    /**
     * Updates {@link GuiIngameForge#left_height} and
     * {@link GuiIngameForge#right_height} if the hotbar is placed in its normal
     * location, possibly at a different Y position.
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onRenderTickEarly(RenderGameOverlayEvent.Pre event) {

    }

    @SubscribeEvent
    public static void onRenderTick(RenderGameOverlayEvent.Pre event) {
        if (BetterHud.getProxy().isModEnabled()
                && event.getType() == ElementType.ALL) {
            renderGameOverlay(event);
            event.setCanceled(true);
        }

        boolean enabled = BetterHud.getProxy().isModEnabled();
        suppressVanilla(enabled);

        if(enabled && event.getType() == ElementType.ALL) {
            renderOverlay(event);
        }
    }

    /**
     * Replacement drop-in for {@link GuiIngameForge#renderGameOverlay(float)}
     * Starting after the {@code Pre} event.
     */
    private static void renderGameOverlay(RenderGameOverlayEvent eventParent) {
        //
    }
}
