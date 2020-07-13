package jobicade.betterhud.events;

import jobicade.betterhud.BetterHud;
import jobicade.betterhud.element.OverlayElement;
import jobicade.betterhud.registry.HudElements;
import jobicade.betterhud.registry.OverlayElements;
import jobicade.betterhud.render.GlSnapshot;
import jobicade.betterhud.render.GlStateManagerManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@EventBusSubscriber(value = { Side.CLIENT }, modid = BetterHud.MODID)
public final class OverlayHook {
    // No instance
    private OverlayHook() {}

    private static SnapshotTracker tracker;

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void preOverlayEarly(RenderGameOverlayEvent.Pre event) {
        // Only side effect is GuiIngameForge.left_height and right_height
        // If the event is canceled these variables don't have much meaning
        if (shouldRun(event)) {
            // Pre-rendering hotbar so no left or right height yet
            GuiIngameForge.left_height = 0;
            GuiIngameForge.right_height = 0;

            // Condition changes with "hide while riding" option
            GuiIngameForge.renderFood = OverlayElements.FOOD_BAR.shouldRenderPrecheck();
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void preOverlayLate(RenderGameOverlayEvent.Pre event) {
        if (!event.isCanceled() && shouldRun(event)) {
            if (tracker == null) {
                tracker = new SnapshotTracker(BetterHud.getLogger());
            }
            GlSnapshot pre = new GlSnapshot();

            // Pre event is a valid parent as it just carries identical
            // information to its own parent
            renderGameOverlay(event);
            // Only cancel right before replacing vanilla HUD
            // Other mods get a chance to cancel the HUD altogether
            event.setCanceled(true);

            tracker.step(pre, new GlSnapshot());
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

    /**
     * Replacement drop-in for {@link GuiIngameForge#renderGameOverlay(float)}
     * Starting after the {@code Pre} event.
     */
    private static void renderGameOverlay(RenderGameOverlayEvent event) {
        // TODO not here
        BetterHud.MANAGER.reset(event.getResolution());
        OverlayContext context = new OverlayContext(event, BetterHud.MANAGER);

        for (OverlayElement element : BetterHud.getProxy().getEnabled(OverlayElements.get())) {
            loadGlState();

            if (canRender(element, context)) {
                Minecraft.getMinecraft().mcProfiler.startSection(element.getName());
                element.render(context);
                Minecraft.getMinecraft().mcProfiler.endSection();
            }
        }

        GlStateManager.enableDepth();
        MinecraftForge.EVENT_BUS.post(new RenderGameOverlayEvent.Post(event, ElementType.ALL));
    }

    private static void loadGlState() {
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.enableTexture2D();

        Minecraft.getMinecraft().getTextureManager().bindTexture(Gui.ICONS);
        GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.color(1.0f, 1.0f, 1.0f);
        RenderHelper.disableStandardItemLighting();

        if (HudElements.GLOBAL.isDebugMode()) {
            GlStateManagerManager.fixCorruptFlags();
        }
    }

    private static boolean canRender(OverlayElement hudElement, OverlayContext context) {
        return hudElement.getServerDependency().containsVersion(BetterHud.getServerVersion())
            && hudElement.shouldRender(context);
    }

    /**
     * @return {@code true} if all conditions for rendering {@code hudElement}
     * are currently satisfied.
     */
    public static boolean shouldRender(OverlayElement hudElement, OverlayContext context) {
        return canRender(hudElement, context)
            && BetterHud.getProxy().getEnabled(OverlayElements.get()).contains(hudElement);
    }

    /**
     * @see GuiIngameForge#renderGameOverlay(float)
     */
    public static boolean shouldRenderBars() {
        Minecraft mc = Minecraft.getMinecraft();

        return mc.playerController.shouldDrawHUD()
            && mc.getRenderViewEntity() instanceof EntityPlayer;
    }

    /**
     * @see GuiIngameForge#pre(ElementType)
     */
    public static boolean pre(RenderGameOverlayEvent parentEvent, ElementType elementType) {
        Event event = new RenderGameOverlayEvent.Pre(parentEvent, elementType);
        return MinecraftForge.EVENT_BUS.post(event);
    }

    /**
     * @see GuiIngameForge#post(ElementType)
     */
    public static boolean post(RenderGameOverlayEvent parentEvent, ElementType elementType) {
        Event event = new RenderGameOverlayEvent.Post(parentEvent, elementType);
        return MinecraftForge.EVENT_BUS.post(event);
    }
}
