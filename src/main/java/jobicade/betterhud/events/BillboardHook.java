package jobicade.betterhud.events;

import static jobicade.betterhud.BetterHud.MC;

import java.util.List;

import com.mojang.blaze3d.platform.GlStateManager;

import jobicade.betterhud.BetterHud;
import jobicade.betterhud.element.entityinfo.BillboardElement;
import jobicade.betterhud.geom.Point;
import jobicade.betterhud.registry.BillboardElements;
import jobicade.betterhud.registry.HudElements;
import jobicade.betterhud.render.Color;
import jobicade.betterhud.render.GlSnapshot;
import jobicade.betterhud.util.GlUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = BetterHud.MODID)
public class BillboardHook {
    private static SnapshotTracker tracker;

    @SubscribeEvent
    public static void worldRender(RenderWorldLastEvent event) {
        MC.mcProfiler.startSection(BetterHud.MODID);

        if(BetterHud.getProxy().isModEnabled()) {
            Entity entity = getMouseOver(HudElements.GLOBAL.getBillboardDistance(), event.getPartialTicks());

            if(entity instanceof EntityLivingBase) {
                renderMobInfo(new BillboardContext(event, (EntityLivingBase)entity));
            }
        }
        MC.mcProfiler.endSection();
    }

    /**
    * Renders mob info elements to the screen.
    */
    private static void renderMobInfo(BillboardContext event) {
        BetterHud.MANAGER.reset(Point.zero());

        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        Color.WHITE.apply();
        MC.getTextureManager().bindTexture(Gui.ICONS);

        GlStateManager.pushMatrix();
        GlUtil.setupBillboard(event.getPointedEntity(), event.getPartialTicks(), HudElements.GLOBAL.getBillboardScale());

        GlSnapshot pre = null;
        if (HudElements.GLOBAL.isDebugMode()) {
            if (tracker == null) {
                tracker = new SnapshotTracker(BetterHud.getLogger());
            }
            pre = new GlSnapshot();
        }

        for (BillboardElement element : BetterHud.getProxy().getEnabled(BillboardElements.get())) {
            if (element.getServerDependency().containsVersion(BetterHud.getServerVersion())
                    && element.shouldRender(event)) {
                MC.mcProfiler.startSection(element.getName());
                element.render(event);
                MC.mcProfiler.endSection();
            }
        }

        if (HudElements.GLOBAL.isDebugMode()) {
            tracker.step(pre, new GlSnapshot());
        }

        GlStateManager.popMatrix();

        MC.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        GlStateManager.enableAlpha();
        GlStateManager.enableDepth();
        GlStateManager.disableBlend();
    }

    /**
     * Allows a custom distance
     * @see net.minecraft.client.renderer.EntityRenderer#getMouseOver(float)
     */
    private static Entity getMouseOver(double distance, float partialTicks) {
        if(MC.world == null) return null;
        Entity viewEntity = MC.getRenderViewEntity();
        if(viewEntity == null) return null;

        Entity pointedEntity = null;

        MC.mcProfiler.startSection("pick");

        RayTraceResult trace = viewEntity.rayTrace(distance, partialTicks);
        Vec3d eyePosition = viewEntity.getPositionEyes(partialTicks);
        Vec3d lookDelta = viewEntity.getLookVec().scale(distance);

        if(trace != null) {
            distance = trace.hitVec.distanceTo(eyePosition);
        }

        AxisAlignedBB range = viewEntity.getEntityBoundingBox().expand(lookDelta.x, lookDelta.y, lookDelta.z).grow(1, 1, 1);

        List<Entity> entitiesInRange = MC.world.getEntitiesInAABBexcluding(viewEntity, range, e -> e != null && e.canBeCollidedWith());

        for(Entity entity : entitiesInRange) {
            AxisAlignedBB entityBox = entity.getEntityBoundingBox().grow(entity.getCollisionBorderSize());
            RayTraceResult entityTrace = entityBox.calculateIntercept(eyePosition, eyePosition.add(lookDelta));

            if(entityBox.contains(eyePosition)) {
                if(distance >= 0) {
                    pointedEntity = entity;
                    distance = 0;
                }
            } else if(entityTrace != null) {
                double entityDistance = eyePosition.distanceTo(entityTrace.hitVec);

                if(entityDistance < distance || distance == 0) {
                    if(entity.getLowestRidingEntity() == viewEntity.getLowestRidingEntity() && !entity.canRiderInteract()) {
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
        MC.mcProfiler.endSection();
        return pointedEntity;
    }
}
