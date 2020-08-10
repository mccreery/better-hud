package jobicade.betterhud.events;

import static jobicade.betterhud.BetterHud.MC;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;

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
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.overlay.PlayerTabOverlayGui;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.util.StringUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = BetterHud.MODID, value = Dist.CLIENT)
public final class OverlayHook {
    // No instance
    private OverlayHook() {}

    private static SnapshotTracker tracker;

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void preOverlayEarly(RenderGameOverlayEvent.Pre event) {
        // Only side effect is ForgeIngameGui.left_height and right_height
        // If the event is canceled these variables don't have much meaning
        if (shouldRun(event)) {
            // Pre-rendering hotbar so no left or right height yet
            ForgeIngameGui.left_height = 0;
            ForgeIngameGui.right_height = 0;

            // Condition changes with "hide while riding" option
            ForgeIngameGui.renderFood = OverlayElements.FOOD_BAR.shouldRenderPrecheck();
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void preOverlayLate(RenderGameOverlayEvent.Pre event) {
        if (!event.isCanceled() && shouldRun(event)) {
            GlSnapshot pre = null;
            if (HudElements.GLOBAL.isDebugMode()) {
                pre = new GlSnapshot();
            }

            // Pre event is a valid parent as it just carries identical
            // information to its own parent
            renderGameOverlay(event);
            // Only cancel right before replacing vanilla HUD
            // Other mods get a chance to cancel the HUD altogether
            event.setCanceled(true);

            if (HudElements.GLOBAL.isDebugMode()) {
                if (tracker == null) {
                    tracker = new SnapshotTracker(BetterHud.getLogger());
                }
                tracker.step(pre, new GlSnapshot());
            }
        }
    }

    /**
     * @return {@code true} if Better HUD should run after {@code event}.
     */
    private static boolean shouldRun(RenderGameOverlayEvent.Pre event) {
        return BetterHud.isModEnabled()
            && !event.isCanceled() // See comment in preOverlayEarly
            && event.getType() == ElementType.ALL; // Only run once each frame
    }

    /**
     * Replacement drop-in for {@link ForgeIngameGui#renderGameOverlay(float)}
     * Starting after the {@code Pre} event.
     */
    private static void renderGameOverlay(RenderGameOverlayEvent event) {
        final Minecraft mc = MC;
        BetterHud.MANAGER.reset(event.getWindow());
        OverlayContext context = new OverlayContext(event, BetterHud.MANAGER);

        for (OverlayElement element : OverlayElements.get().getEnabled()) {
            loadGlState();

            if (canRender(element, context)) {
                mc.getProfiler().startSection(element.getName());
                element.setLastBounds(element.render(context));
                mc.getProfiler().endSection();
            }
        }

        renderHudText(event);
        renderFpsGraph(mc, event);
        renderPlayerList(mc, event);

        RenderSystem.enableDepthTest();
        MinecraftForge.EVENT_BUS.post(new RenderGameOverlayEvent.Post(event, ElementType.ALL));
    }

    private static void loadGlState() {
        RenderSystem.disableAlphaTest();
        RenderSystem.enableBlend();
        RenderSystem.disableDepthTest();
        RenderSystem.enableTexture();

        MC.getTextureManager().bindTexture(AbstractGui.GUI_ICONS_LOCATION);
        RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.color3f(1.0f, 1.0f, 1.0f);
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
            && OverlayElements.get().getEnabled().contains(hudElement);
    }

    /**
     * @see ForgeIngameGui#renderHUDText(int, int)
     */
    private static void renderHudText(RenderGameOverlayEvent event) {
        Minecraft mc = MC;
        mc.getProfiler().startSection("forgeHudText");

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

        mc.getProfiler().endSection();

        Event postEvent = new RenderGameOverlayEvent.Post(event, ElementType.TEXT);
        MinecraftForge.EVENT_BUS.post(postEvent);
    }

    // 5 days (gametime) and 25 seconds (realtime)
    private static final long DEMO_TIME = 24000L * 5 + 500;

    private static String getDemoString(Minecraft mc) {
        if (mc.isDemo()) {
            long time = mc.world.getGameTime();

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
        Point rightAnchor = new Point(mc.getMainWindow().getScaledWidth() - 1, 1);
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
            label.setShadow(false);
            labelList.add(label);
        }
        return labelList;
    }

    /**
     * @see ForgeIngameGui#renderFPSGraph()
     */
    private static void renderFpsGraph(Minecraft mc, RenderGameOverlayEvent event) {
        if (mc.gameSettings.showDebugInfo && mc.gameSettings.showLagometer) {
            Event preEvent = new RenderGameOverlayEvent.Pre(event, ElementType.FPS_GRAPH);

            if (!MinecraftForge.EVENT_BUS.post(preEvent)) {
                // TODO lagometer
                //new GuiOverlayDebug2(mc).renderLagometer();

                Event postEvent = new RenderGameOverlayEvent.Post(event, ElementType.FPS_GRAPH);
                MinecraftForge.EVENT_BUS.post(postEvent);
            }
        }
    }

    /**
     * @see ForgeIngameGui#renderPlayerList(int, int)
     */
    private static void renderPlayerList(Minecraft mc, RenderGameOverlayEvent parentEvent) {
        PlayerTabOverlayGui tabList = mc.ingameGUI.getTabList();

        ScoreObjective scoreobjective = mc.world.getScoreboard().getObjectiveInDisplaySlot(0);
        ClientPlayNetHandler handler = mc.player.connection;

        if (mc.gameSettings.keyBindPlayerList.isKeyDown() && (
            !mc.isIntegratedServerRunning()
            || handler.getPlayerInfoMap().size() > 1
            || scoreobjective != null
        )) {
            tabList.setVisible(true);

            if (!MinecraftForge.EVENT_BUS.post(new RenderGameOverlayEvent.Pre(parentEvent, ElementType.PLAYER_LIST))) {
                tabList.render(mc.getMainWindow().getScaledWidth(), mc.world.getScoreboard(), scoreobjective);
                MinecraftForge.EVENT_BUS.post(new RenderGameOverlayEvent.Post(parentEvent, ElementType.PLAYER_LIST));
            }
        } else {
            tabList.setVisible(false);
        }
    }

    /**
     * @see ForgeIngameGui#renderGameOverlay(float)
     */
    public static boolean shouldRenderBars() {
        Minecraft mc = MC;

        return mc.playerController.shouldDrawHUD()
            && mc.getRenderViewEntity() instanceof PlayerEntity;
    }

    /**
     * @see ForgeIngameGui#pre(ElementType)
     */
    public static boolean pre(RenderGameOverlayEvent parentEvent, ElementType elementType) {
        Event event = new RenderGameOverlayEvent.Pre(parentEvent, elementType);
        return MinecraftForge.EVENT_BUS.post(event);
    }

    /**
     * @see ForgeIngameGui#post(ElementType)
     */
    public static boolean post(RenderGameOverlayEvent parentEvent, ElementType elementType) {
        Event event = new RenderGameOverlayEvent.Post(parentEvent, elementType);
        return MinecraftForge.EVENT_BUS.post(event);
    }
}
