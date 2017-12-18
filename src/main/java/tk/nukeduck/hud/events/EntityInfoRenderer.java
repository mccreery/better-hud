package tk.nukeduck.hud.events;

import java.util.List;

import javax.annotation.Nullable;

import org.lwjgl.opengl.GL11;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import tk.nukeduck.hud.BetterHud;
import tk.nukeduck.hud.element.entityinfo.ExtraGuiElementEntityInfo;

public class EntityInfoRenderer {
	private static ResourceLocation font = new ResourceLocation("textures/font/ascii.png");

	@SubscribeEvent
	public void worldRender(RenderWorldLastEvent e) {
		if(!BetterHud.proxy.elements.globalSettings.enabled) return;

		double maxDist = 0;
		for(ExtraGuiElementEntityInfo element : BetterHud.proxy.elements.info) {
			if(element.enabled && element.distance.value > maxDist) maxDist = element.distance.value;
		}
		if(maxDist == 0) return;
		
		Entity entity = this.getMouseOver(Minecraft.getMinecraft(), e.getPartialTicks(), maxDist);
		//Entity entity = Minecraft.getMinecraft().pointedEntity;
		
		if(entity != null && entity instanceof EntityLivingBase) {
			EntityLivingBase entity2 = (EntityLivingBase) entity;
			GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
			for(ExtraGuiElementEntityInfo element : BetterHud.proxy.elements.info) {
				if(element.enabled && this.lastDistance <= element.distance.value) {
					element.renderInfo(entity2, Minecraft.getMinecraft(), e.getPartialTicks());
				}
			}
			/*if(BetterHud.proxy.elements.mobInfo.enabled && this.lastDistance <= BetterHud.proxy.elements.mobInfo.distance.value)
				BetterHud.proxy.elements.mobInfo.renderInfo(entity2, BetterHud.mc, e.partialTicks);
			if(BetterHud.proxy.elements.horseInfo.enabled && this.lastDistance <= BetterHud.proxy.elements.horseInfo.distance.value)
				BetterHud.proxy.elements.horseInfo.renderInfo(entity2, BetterHud.mc, e.partialTicks);
			if(BetterHud.proxy.elements.breedIndicator.enabled && this.lastDistance <= BetterHud.proxy.elements.breedIndicator.distance.value)
				BetterHud.proxy.elements.breedIndicator.renderInfo(entity2, BetterHud.mc, e.partialTicks);*/
			GL11.glPopAttrib();

			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			Minecraft.getMinecraft().getTextureManager().bindTexture(font); // Just in case
		}
		
		/*Vec3 vec3 = BetterHud.mc.getRenderViewEntity().getPositionEyes(1.0F);
		Vec3 vec31 = BetterHud.mc.getRenderViewEntity().getLook(1.0F);
		Vec3 vec32 = vec3.addVector(vec31.xCoord * 200, vec31.yCoord * 200, vec31.zCoord * 200);
		BetterHud.mc.getRenderViewEntity().worldObj.rayTraceBlocks(vec3, vec32, false, false, true);*/
		
		/*MovingObjectPosition mop = BetterHud.mc.getRenderViewEntity().rayTrace(200, 1.0F);
		if(mop.typeOfHit == MovingObjectType.ENTITY) {
			if(mop.entityHit instanceof EntityLivingBase) {
				EntityLivingBase entity = (EntityLivingBase) BetterHud.mc.objectMouseOver.entityHit;
				
				GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
				{
					if(HudElements.mobInfo.enabled)
						HudElements.mobInfo.renderInfo(entity, BetterHud.mc, e.partialTicks);
					if(HudElements.horseInfo.enabled)
						HudElements.horseInfo.renderInfo(entity, BetterHud.mc, e.partialTicks);
					if(HudElements.breedIndicator.enabled)
						HudElements.breedIndicator.renderInfo(entity, BetterHud.mc, e.partialTicks);
				}
				GL11.glPopAttrib();
			}
		}
		
		if(BetterHud.mc != null && BetterHud.mc.objectMouseOver != null
			&& BetterHud.mc.objectMouseOver.entityHit != null
			&& BetterHud.mc.objectMouseOver.entityHit instanceof EntityLivingBase) {
			EntityLivingBase entity = (EntityLivingBase) BetterHud.mc.objectMouseOver.entityHit;
			
			EntityPlayer player = null;
			Vec3 start = Vec3.createVectorHelper(BetterHud.mc.thePlayer.posX, BetterHud.mc.thePlayer.getEyeHeight(), BetterHud.mc.thePlayer.posZ);
			Vec3 end = Vec3.createVectorHelper(BetterHud.mc.thePlayer.posX, BetterHud.mc.thePlayer.getEyeHeight(), BetterHud.mc.thePlayer.posZ);
			Vec3 v = BetterHud.mc.thePlayer.getLookVec();
			while(start.distanceTo(end) < 200) {
				start.addVector(v.xCoord, v.yCoord, v.zCoord);
			}
			
			GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
			{
				if(HudElements.mobInfo.enabled)
					HudElements.mobInfo.renderInfo(entity, BetterHud.mc, e.partialTicks);
				if(HudElements.horseInfo.enabled)
					HudElements.horseInfo.renderInfo(entity, BetterHud.mc, e.partialTicks);
				if(HudElements.breedIndicator.enabled)
					HudElements.breedIndicator.renderInfo(entity, BetterHud.mc, e.partialTicks);
			}
			GL11.glPopAttrib();
		}*/
	}
	
	public double lastDistance = 0;
	
	public Entity getMouseOver(Minecraft mc, float partialTick, double length) {
		Entity entity = mc.getRenderViewEntity();
		
		if(entity != null) {
			if(mc.world != null) {
				double distance = length;
				Vec3d vec3 = entity.getPositionEyes(partialTick);
				
				double blockDist = -1;
				
				RayTraceResult hitBlock = entity.rayTrace(distance, partialTick);
				if(hitBlock != null) {
					blockDist = hitBlock.hitVec.distanceTo(vec3);
				}
				
				Vec3d vec31 = entity.getLook(partialTick);
				Vec3d vec32 = vec3.addVector(vec31.x * distance, vec31.y * distance, vec31.z * distance);
				Entity pointedEntity = null;
				//Vec3d vec33 = null;
				//float f1 = 1.0F;
				
				// idk
				/*AxisAlignedBB box = entity.getEntityBoundingBox();
				Vec3d include = vec31.scale(distance);
				box.union(new AxisAlignedBB(include, include));
				box.expand(f1, f1, f1);
				
				List<Entity> list = mc.world.getEntitiesWithinAABBExcludingEntity(entity, box);*/
				
				/// LIFTED FROM MC CODE (EntityRenderer)
				List<Entity> list = mc.world.getEntitiesInAABBexcluding(entity, entity.getEntityBoundingBox().expand(vec31.x * distance, vec31.y * distance, vec31.z * distance).grow(1.0D, 1.0D, 1.0D), Predicates.and(EntitySelectors.NOT_SPECTATING, new Predicate<Entity>() {
					public boolean apply(@Nullable Entity p_apply_1_) {
						return p_apply_1_ != null && p_apply_1_.canBeCollidedWith();
					}
				}));
				///
				
				double d2 = blockDist;
				
				for(int i = 0; i < list.size(); ++i) {
					Entity entity1 = (Entity) list.get(i);
					
					if(entity1.canBeCollidedWith()) {
						float f2 = entity1.getCollisionBorderSize();
						AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().expand((double) f2, (double) f2, (double) f2);
						RayTraceResult movingobjectposition = axisalignedbb.calculateIntercept(vec3, vec32);
						
						if(axisalignedbb.contains(vec3)) {
							if(d2 >= 0.0D) {
								pointedEntity = entity1;
								//vec33 = movingobjectposition == null ? vec3 : movingobjectposition.hitVec;
								this.lastDistance = 0;
								d2 = 0.0D;
							}
						} else if(movingobjectposition != null) {
							double d3 = vec3.distanceTo(movingobjectposition.hitVec);
							
							if(d3 < d2 || d2 == 0.0D) {
								if(entity1 == entity.getRidingEntity() && !entity.canRiderInteract()) {
									if(d2 == 0.0D) {
										pointedEntity = entity1;
										this.lastDistance = d3;
										//vec33 = movingobjectposition.hitVec;
									}
								} else {
									pointedEntity = entity1;
									this.lastDistance = d3;
									//vec33 = movingobjectposition.hitVec;
									d2 = d3;
								}
							}
						}
					}
				}
				return pointedEntity;
			}
		}
		return null;
	}
}