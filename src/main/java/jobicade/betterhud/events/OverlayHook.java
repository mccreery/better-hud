package jobicade.betterhud.events;

import java.util.ArrayList;
import java.util.List;

import jobicade.betterhud.BetterHud;
import jobicade.betterhud.element.OverlayElement;
import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Point;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.registry.HudElements;
import jobicade.betterhud.registry.OverlayElements;
import jobicade.betterhud.render.Color;
import jobicade.betterhud.render.GlSnapshot;
import jobicade.betterhud.render.GlStateManagerManager;
import jobicade.betterhud.render.Grid;
import jobicade.betterhud.render.Label;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.StringUtils;
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

        renderHudText(event);
        renderFpsGraph(event);

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
     * @see GuiIngameForge#renderHUDText(int, int)
     */
    private static void renderHudText(RenderGameOverlayEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        mc.mcProfiler.startSection("forgeHudText");

        // Text event takes ArrayList, not List
        ArrayList<String> leftList = new ArrayList<>();
        ArrayList<String> rightList = new ArrayList<>();

        String demoString = getDemoString(mc);
        if (demoString != null) {
            rightList.add(demoString);
        }

        if (mc.gameSettings.showDebugInfo) {
            Event preEvent = new RenderGameOverlayEvent.Pre(event, ElementType.DEBUG);

            if (!MinecraftForge.EVENT_BUS.post(preEvent)) {
                GuiOverlayDebug2 overlay = new GuiOverlayDebug2(mc);
                leftList.addAll(overlay.getDebugInfoLeft());
                rightList.addAll(overlay.getDebugInfoRight());

                Event postEvent = new RenderGameOverlayEvent.Post(event, ElementType.DEBUG);
                MinecraftForge.EVENT_BUS.post(postEvent);
            }
        }

        RenderGameOverlayEvent.Text textEvent = new RenderGameOverlayEvent.Text(
            event, leftList, rightList);

        if (!MinecraftForge.EVENT_BUS.post(textEvent)) {
            renderLists(mc, leftList, rightList);
        }

        mc.mcProfiler.endSection();

        Event postEvent = new RenderGameOverlayEvent.Post(event, ElementType.TEXT);
        MinecraftForge.EVENT_BUS.post(postEvent);
    }

    // 5 days (gametime) and 25 seconds (realtime)
    private static final long DEMO_TIME = 24000L * 5 + 500;

    private static String getDemoString(Minecraft mc) {
        if (mc.isDemo()) {
            long time = mc.world.getTotalWorldTime();

            if (time >= DEMO_TIME) {
                return I18n.format("demo.demoExpired");
            } else {
                String elapsed = StringUtils.ticksToElapsedTime((int)(DEMO_TIME - time));
                return I18n.format("demo.remainingTime", elapsed);
            }
        } else {
            return null;
        }
    }

    private static void renderLists(Minecraft mc, List<String> leftList, List<String> rightList) {
        renderLabels(leftList, new Point(1, 1), Direction.NORTH_WEST);
        Point rightAnchor = new Point(new ScaledResolution(mc).getScaledWidth() - 1, 1);
        renderLabels(rightList, rightAnchor, Direction.NORTH_EAST);
    }

    private static void renderLabels(List<String> stringList, Point anchor, Direction corner) {
        List<Label> labelList = getLabels(stringList);
        Grid<?> grid = new Grid<>(new Point(1, labelList.size()), labelList);

        grid.setCellAlignment(corner);
        grid.setBounds(new Rect(grid.getPreferredSize()).align(anchor, corner));
        grid.render();
    }

    private static List<Label> getLabels(List<String> stringList) {
        List<Label> labelList = new ArrayList<>();

        for (String line : stringList) {
            Label label = new Label(line);
            label.setBackground(new Color(0x90505050));
            labelList.add(label);
        }
        return labelList;
    }

    /**
     * @see GuiIngameForge#renderFPSGraph()
     */
    private static void renderFpsGraph(RenderGameOverlayEvent event) {
        Minecraft mc = Minecraft.getMinecraft();

        if (mc.gameSettings.showDebugInfo && mc.gameSettings.showLagometer) {
            Event preEvent = new RenderGameOverlayEvent.Pre(event, ElementType.FPS_GRAPH);

            if (!MinecraftForge.EVENT_BUS.post(preEvent)) {
                new GuiOverlayDebug2(mc).renderLagometer();

                Event postEvent = new RenderGameOverlayEvent.Post(event, ElementType.FPS_GRAPH);
                MinecraftForge.EVENT_BUS.post(postEvent);
            }
        }
    }
}
