package jobicade.betterhud.events;

import com.google.common.base.Predicate;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import jobicade.betterhud.BetterHud;
import jobicade.betterhud.element.HudElement;
import jobicade.betterhud.geom.Point;
import jobicade.betterhud.render.Color;
import jobicade.betterhud.render.GlSnapshot;
import jobicade.betterhud.util.GlUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.util.List;

import static jobicade.betterhud.BetterHud.MANAGER;
import static jobicade.betterhud.BetterHud.MODID;
import static net.minecraftforge.client.gui.ForgeIngameGui.*;

public final class RenderEvents {
    @SubscribeEvent
    public void onRenderTick(RenderGameOverlayEvent.Pre event) {
        Minecraft.getInstance().profiler.push(MODID);

        boolean enabled = BetterHud.getProxy().isModEnabled();
        suppressVanilla(enabled);

        if(enabled && event.getType() == ElementType.ALL) {
            renderOverlay(event);
        }
        Minecraft.getInstance().profiler.pop();
    }

    @SubscribeEvent
    public void worldRender(RenderWorldLastEvent event) {
        Minecraft.getInstance().profiler.push(MODID);

        if(BetterHud.getProxy().isModEnabled()) {
            Entity entity = getMouseOver(HudElement.GLOBAL.getBillboardDistance(), event.getPartialTicks());

            if(entity instanceof LivingEntity) {
                renderMobInfo(new RenderMobInfoEvent(event, (LivingEntity)entity));
            }
        }
        Minecraft.getInstance().profiler.pop();
    }

    /**
     * Modifies the OpenGL state for maximum compatibility with elements.
     * This is only used for {@link #onRenderTick(net.minecraftforge.client.event.RenderGameOverlayEvent.Pre)}
     */
    public static void beginOverlayState() {
        GlStateManager.func_187428_a(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ZERO, DestFactor.ONE);
        GlStateManager.func_179147_l();
        GlStateManager.func_179118_c();

        Minecraft.getInstance().getTextureManager().bind(AbstractGui.field_110324_m);
        GlStateManager.func_179103_j(GL11.GL_SMOOTH);
    }

    /**
     * Reverts the OpenGL state to the expected state at the time of the event.
     * This is only used for {@link #onRenderTick(net.minecraftforge.client.event.RenderGameOverlayEvent.Pre))}
     */
    public static void endOverlayState() {
        GlStateManager.func_187428_a(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ZERO, DestFactor.ONE);
        GlStateManager.func_179084_k();
        GlStateManager.func_179118_c();

        GlStateManager.func_179144_i(0);
        GlStateManager.func_179103_j(GL11.GL_FLAT);
    }

    /**
     * Suppresses or unsuppresses vanilla HUD rendering.
     * @param suppress {@code true} to suppress
     */
    private static void suppressVanilla(boolean suppress) {
        boolean allow = !suppress;

        renderHotbar      = allow || Minecraft.getInstance().player.isSpectator();
        renderExperiance  = allow;
        renderHealth      = allow;
        renderArmor       = allow;
        renderAir         = allow;
        renderHelmet      = allow;
        renderVignette    = allow;
        renderObjective   = allow;
        renderCrosshairs  = allow;
        renderPortal      = allow;

        // Vanilla puts preconditions in these
        renderFood        = renderFood && allow;
        renderJumpBar     = renderJumpBar && allow;
        renderHealthMount = renderHealthMount && allow;
    }

    private final SnapshotTracker overlayTracker = new SnapshotTracker(BetterHud.getLogger());
    private final SnapshotTracker mobInfoTracker = new SnapshotTracker(BetterHud.getLogger());

    /**
     * Renders overlay (normal HUD) elements to the screen.
     */
    private void renderOverlay(RenderGameOverlayEvent.Pre event) {
        MANAGER.reset(event.getResolution());
        beginOverlayState();

        if(HudElement.GLOBAL.isDebugMode()) {
            GlSnapshot pre = new GlSnapshot();
            HudElement.renderAll(event);
            overlayTracker.step(pre, new GlSnapshot());
        } else {
            HudElement.renderAll(event);
        }
        endOverlayState();
    }

    /**
     * Renders mob info elements to the screen.
     */
    private void renderMobInfo(RenderMobInfoEvent event) {
        MANAGER.reset(Point.zero());

        GlStateManager.func_179097_i();
        GlStateManager.func_179147_l();
        GlStateManager.func_179118_c();
        Color.WHITE.apply();
        Minecraft.getInstance().getTextureManager().bind(AbstractGui.field_110324_m);

        GlStateManager.func_179094_E();
        GlUtil.setupBillboard(event.getEntity(), event.getPartialTicks(), HudElement.GLOBAL.getBillboardScale());

        if(HudElement.GLOBAL.isDebugMode()) {
            GlSnapshot pre = new GlSnapshot();
            HudElement.renderAll(event);
            mobInfoTracker.step(pre, new GlSnapshot());
        } else {
            HudElement.renderAll(event);
        }

        GlStateManager.func_179121_F();

        Minecraft.getInstance().getTextureManager().bind(TextureMap.LOCATION_BLOCKS);
        GlStateManager.func_179141_d();
        GlStateManager.func_179126_j();
        GlStateManager.func_179084_k();
    }

    /** Allows a custom distance
     * @see net.minecraft.client.renderer.EntityRenderer#getMouseOver(float) */
    private static Entity getMouseOver(double distance, float partialTicks) {
        if(Minecraft.getInstance().level == null) return null;
        Entity viewEntity = Minecraft.getInstance().getCameraEntity();
        if(viewEntity == null) return null;

        Entity pointedEntity = null;

        Minecraft.getInstance().profiler.push("pick");

        RayTraceResult trace = viewEntity.func_174822_a(distance, partialTicks);
        Vector3d eyePosition = viewEntity.getEyePosition(partialTicks);
        Vector3d lookDelta = viewEntity.getLookAngle().scale(distance);

        if(trace != null) {
            distance = trace.location.distanceTo(eyePosition);
        }

        AxisAlignedBB range = viewEntity.getBoundingBox().expandTowards(lookDelta.x, lookDelta.y, lookDelta.z).inflate(1, 1, 1);

        List<Entity> entitiesInRange = Minecraft.getInstance().level.getEntities(viewEntity, range, new Predicate<Entity>() {
            @Override
            public boolean apply(Entity entity) {
                return entity != null && entity.isPickable();
            }
        });

        for(Entity entity : entitiesInRange) {
            AxisAlignedBB entityBox = entity.getBoundingBox().inflate(entity.getPickRadius());
            RayTraceResult entityTrace = entityBox.func_72327_a(eyePosition, eyePosition.add(lookDelta));

            if(entityBox.contains(eyePosition)) {
                if(distance >= 0) {
                    pointedEntity = entity;
                    distance = 0;
                }
            } else if(entityTrace != null) {
                double entityDistance = eyePosition.distanceTo(entityTrace.location);

                if(entityDistance < distance || distance == 0) {
                    if(entity.getRootVehicle() == viewEntity.getRootVehicle() && !entity.canRiderInteract()) {
                        if(distance == 0) {
                            pointedEntity = entity;
                        }
                    } else {
                        pointedEntity = entity;
                        distance = entityDistance;
                    }
                }
            }
        }
        Minecraft.getInstance().profiler.pop();
        return pointedEntity;
    }
}
