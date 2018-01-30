package tk.nukeduck.hud.events;

import static tk.nukeduck.hud.BetterHud.MC;

import java.util.List;

import org.lwjgl.util.Point;

import com.google.common.base.Predicate;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import tk.nukeduck.hud.BetterHud;
import tk.nukeduck.hud.element.HudElement;
import tk.nukeduck.hud.element.entityinfo.EntityInfo;
import tk.nukeduck.hud.util.GlUtil;

public class EntityInfoRenderer {
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void worldRender(RenderWorldLastEvent e) {
		if(!BetterHud.isEnabled()) return;

		Entity entity = getMouseOver(getMaxDistance(), e.getPartialTicks());

		if(entity != null && entity instanceof EntityLivingBase) {
			//GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);

			for(EntityInfo element : HudElement.ENTITY_INFO) {
				if(element.isEnabled() && distance <= element.getDistance()) {
					element.render((EntityLivingBase)entity, e.getPartialTicks());
				}
			}

			//GL11.glPopAttrib();

			//GL11.glEnable(GL11.GL_BLEND);
			//GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			//GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		}
	}

	/** @return The maximum required ray trace distance based on the enabled
	 * entity info elements */
	private static double getMaxDistance() {
		double max = 0;

		for(EntityInfo element : HudElement.ENTITY_INFO) {
			if(!element.isEnabled()) continue;

			double distance = element.getDistance();
			if(distance > max) max = distance;
		}
		return max;
	}

	private double distance = 0;

	/** Allows a custom distance
	 * @see net.minecraft.client.renderer.EntityRenderer#getMouseOver(float) */
	@SideOnly(Side.CLIENT)
	private Entity getMouseOver(double distance, float partialTicks) {
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

		List<Entity> entitiesInRange = MC.world.getEntitiesInAABBexcluding(viewEntity, range, new Predicate<Entity>() {
			@Override
			public boolean apply(Entity entity) {
				return entity != null && entity.canBeCollidedWith();
			}
		});

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

	// TODO work on this
	public static void billBoard(Entity entity, EntityPlayer player, float partialTicks) {
		Vec3d eyes = player.getPositionEyes(partialTicks);
		Vec3d origin = entity.getPositionEyes(partialTicks).addVector(0, 0.5 + entity.height, 0);

		double dx = origin.x - eyes.x;
		double dy = origin.y - eyes.y;
		double dz = origin.z - eyes.z;
		float distance = (float)Math.sqrt(dx*dx + dy*dy + dz*dz);
		float scale = (float)Math.max(1, distance / 5);

		GlStateManager.translate(dx, dy, dz);
		GlUtil.scale(scale);
		GlStateManager.rotate(-player.rotationYaw, 0, 1, 0);
		GlStateManager.rotate(180, 0, 0, 1);

		GlStateManager.disableDepth();
	}
}
