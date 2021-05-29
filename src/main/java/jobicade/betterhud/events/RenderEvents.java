package jobicade.betterhud.events;

import com.google.common.base.Predicate;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import jobicade.betterhud.BetterHud;
import jobicade.betterhud.element.HudElement;
import jobicade.betterhud.geom.Point;
import jobicade.betterhud.render.Color;
import jobicade.betterhud.render.GlSnapshot;
import jobicade.betterhud.util.GlUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.container.PlayerContainer;
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
        Minecraft.getInstance().getProfiler().push(MODID);

        boolean enabled = BetterHud.isModEnabled();
        suppressVanilla(enabled);

        if(enabled && event.getType() == ElementType.ALL) {
            renderOverlay(event);
        }
        Minecraft.getInstance().getProfiler().pop();
    }

    @SubscribeEvent
    public void worldRender(RenderWorldLastEvent event) {
        Minecraft.getInstance().getProfiler().push(MODID);

        if(BetterHud.isModEnabled()) {
            Entity entity = getMouseOver(HudElement.GLOBAL.getBillboardDistance(), event.getPartialTicks());

            if(entity instanceof LivingEntity) {
                renderMobInfo(new RenderMobInfoEvent(event, (LivingEntity)entity));
            }
        }
        Minecraft.getInstance().getProfiler().pop();
    }

    /**
     * Modifies the OpenGL state for maximum compatibility with elements.
     * This is only used for {@link #onRenderTick(net.minecraftforge.client.event.RenderGameOverlayEvent.Pre)}
     */
    public static void beginOverlayState() {
        RenderSystem.blendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ZERO, DestFactor.ONE);
        RenderSystem.enableBlend();
        RenderSystem.disableAlphaTest();

        Minecraft.getInstance().getTextureManager().bind(AbstractGui.GUI_ICONS_LOCATION);
        RenderSystem.shadeModel(GL11.GL_SMOOTH);
    }

    /**
     * Reverts the OpenGL state to the expected state at the time of the event.
     * This is only used for {@link #onRenderTick(net.minecraftforge.client.event.RenderGameOverlayEvent.Pre))}
     */
    public static void endOverlayState() {
        RenderSystem.blendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ZERO, DestFactor.ONE);
        RenderSystem.disableBlend();
        RenderSystem.disableAlphaTest();

        RenderSystem.bindTexture(0);
        RenderSystem.shadeModel(GL11.GL_FLAT);
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
        MANAGER.reset(event.getWindow());
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

        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.disableAlphaTest();
        Color.WHITE.apply();
        Minecraft.getInstance().getTextureManager().bind(AbstractGui.GUI_ICONS_LOCATION);

        event.getMatrixStack().pushPose();
        GlUtil.setupBillboard(event.getMatrixStack(), event.getEntity(), event.getPartialTicks(), HudElement.GLOBAL.getBillboardScale());

        if(HudElement.GLOBAL.isDebugMode()) {
            GlSnapshot pre = new GlSnapshot();
            HudElement.renderAll(event);
            mobInfoTracker.step(pre, new GlSnapshot());
        } else {
            HudElement.renderAll(event);
        }

        event.getMatrixStack().popPose();

        Minecraft.getInstance().getTextureManager().bind(PlayerContainer.BLOCK_ATLAS);
        RenderSystem.enableAlphaTest();
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }

    private static Entity getMouseOver(double distance, float partialTicks) {
        if(Minecraft.getInstance().level == null) return null;
        Entity viewEntity = Minecraft.getInstance().getCameraEntity();
        if(viewEntity == null) return null;

        Entity pointedEntity = null;

        Minecraft.getInstance().getProfiler().push("pick");

        RayTraceResult trace = viewEntity.pick(distance, partialTicks, false);
        Vector3d eyePosition = viewEntity.getEyePosition(partialTicks);
        Vector3d lookDelta = viewEntity.getLookAngle().scale(distance);

        if(trace != null) {
            distance = trace.getLocation().distanceTo(eyePosition);
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
            boolean entityTrace = entityBox.intersects(eyePosition, eyePosition.add(lookDelta));

            if(entityBox.contains(eyePosition)) {
                if(distance >= 0) {
                    pointedEntity = entity;
                    distance = 0;
                }
            } else if(entityTrace) {
                double entityDistance = eyePosition.distanceTo(lookDelta);

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
        Minecraft.getInstance().getProfiler().pop();
        return pointedEntity;
    }
}
