package jobicade.betterhud.events;

import jobicade.betterhud.BetterHud;
import jobicade.betterhud.geom.LayoutManager;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.client.GuiIngameForge;

public final class OverlayHook {
    // No instance
    private OverlayHook() {}

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void preOverlayEarly(RenderGameOverlayEvent.Pre event) {
        if (shouldRun(event)) {
            // Pre-rendering hotbar so no left or right height yet
            GuiIngameForge.left_height = 0;
            GuiIngameForge.right_height = 0;
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void preOverlayLate(RenderGameOverlayEvent.Pre event) {
        if (shouldRun(event)) {
            // Pre event is a valid parent as it just carries identical
            // information to its own parent
            renderGameOverlay(event);
            // Only cancel right before replacing vanilla HUD
            // Other mods get a chance to cancel the HUD altogether
            event.setCanceled(true);
        }
    }

    /**
     * @return {@code true} if Better HUD should run after {@code event}.
     */
    private static boolean shouldRun(RenderGameOverlayEvent.Pre event) {
        return BetterHud.getProxy().isModEnabled()
            && !event.isCanceled() // See comment in preOverlayEarly
            && event.getType() == ElementType.ALL; // Only run once each frame
    }

    private static final LayoutManager layoutManager = new LayoutManager();

    /**
     * Replacement drop-in for {@link GuiIngameForge#renderGameOverlay(float)}
     * Starting after the {@code Pre} event.
     */
    private static void renderGameOverlay(RenderGameOverlayEvent eventParent) {

    }
}
