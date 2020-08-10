package jobicade.betterhud.util;

import static jobicade.betterhud.BetterHud.MC;

import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

public class RayTraceUtil {
    /**
     * Replaces {@link GameRenderer#getMouseOver(float)}, which updates the
     * global pointed entity and relies on player reach distance. This method by
     * comparison supports any view entity and any distance, and does not update
     * any global state.
     *
     * @return The entity being looked at by the view entity or {@code null}.
     */
    public static Entity getMouseOver(Entity viewEntity, double distance, float partialTicks) {
        if (viewEntity != null && viewEntity.world != null) {
            MC.getProfiler().startSection("pick");

            Vec3d eyePosition = viewEntity.getEyePosition(partialTicks);
            RayTraceResult rayTraceResult = viewEntity.pick(distance, partialTicks, false);
            if (rayTraceResult != null) {
                distance = eyePosition.distanceTo(rayTraceResult.getHitVec());
            }

            Vec3d lookDirection = viewEntity.getLook(partialTicks);
            Vec3d ray = lookDirection.scale(distance);
            Vec3d farPosition = eyePosition.add(ray);

            AxisAlignedBB rayBB = viewEntity.getBoundingBox().expand(ray).grow(1);
            EntityRayTraceResult entityRayTraceResult = ProjectileHelper.rayTraceEntities(
                viewEntity, eyePosition, farPosition, rayBB,
                RayTraceUtil::canMouseOver, distance * distance);

            if (entityRayTraceResult != null) {
                Entity mouseOverEntity = entityRayTraceResult.getEntity();
                //Vec3d hitVec = entityRayTraceResult.getHitVec();
                //double hitDistance = eyePosition.distanceTo(hitVec);

                if (/*(hitDistance < distance || rayTraceResult == null)
                        && */(mouseOverEntity instanceof LivingEntity/* || mouseOverEntity instanceof ItemFrameEntity*/)) {
                    return mouseOverEntity;
                }
            }

            MC.getProfiler().endSection();
        }
        return null;
    }

    /**
     * @return {@code true} if {@code entity} can be moused over.
     */
    private static boolean canMouseOver(Entity entity) {
        return entity.canBeCollidedWith() && !entity.isSpectator();
    }
}
