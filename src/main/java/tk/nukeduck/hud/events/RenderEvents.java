package tk.nukeduck.hud.events;

import static tk.nukeduck.hud.BetterHud.MANAGER;
import static tk.nukeduck.hud.BetterHud.MC;
import static tk.nukeduck.hud.BetterHud.MODID;
import static tk.nukeduck.hud.BetterHud.pointedEntity;

import static net.minecraftforge.client.GuiIngameForge.*;

import java.util.List;

import com.google.common.base.Predicate;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import tk.nukeduck.hud.BetterHud;
import tk.nukeduck.hud.element.HudElement;
import tk.nukeduck.hud.util.Colors;
import tk.nukeduck.hud.util.GlUtil;
import tk.nukeduck.hud.util.Point;

@SideOnly(Side.CLIENT)
public final class RenderEvents {
	private RenderEvents() {}

	public static void registerEvents() {
		MinecraftForge.EVENT_BUS.register(new RenderEvents());
	}

	@SubscribeEvent
	public void onTickStart(RenderTickEvent event) {
		if(event.phase == Phase.START) {
			HudElement.resetBounds();
		}
	}

	@SubscribeEvent
	public void onRenderTick(RenderGameOverlayEvent.Pre event) {
		final boolean disabled = !BetterHud.isEnabled();

		renderHotbar      = disabled;
		renderExperiance  = disabled;
		renderHealth      = disabled;
		renderFood        = disabled;
		renderArmor       = disabled;
		renderAir         = disabled;
		renderJumpBar     = disabled;
		renderHealthMount = disabled;

		if(disabled || event.getType() != RenderGameOverlayEvent.ElementType.ALL) {
			return;
		}

		MC.mcProfiler.startSection(MODID);
		MANAGER.reset(event.getResolution());

		HudElement.renderAll(event);

		// Expected state by vanilla GUI
		MC.getTextureManager().bindTexture(Gui.ICONS);
		MC.mcProfiler.endSection();
	}

	@SubscribeEvent
	public void worldRender(RenderWorldLastEvent event) {
		if(!BetterHud.isEnabled()) return;

		MC.mcProfiler.startSection(BetterHud.MODID);
		Entity pointed = getMouseOver(HudElement.GLOBAL.getBillboardDistance(), event.getPartialTicks());

		if(pointed != null && pointed instanceof EntityLivingBase) {
			MANAGER.reset(Point.ZERO);
			pointedEntity = (EntityLivingBase)pointed;

			GlStateManager.pushMatrix();
			GlUtil.setupBillboard(pointedEntity, event.getPartialTicks(), HudElement.GLOBAL.getBillboardScale());

			GlStateManager.disableDepth();
			GlUtil.color(Colors.WHITE);
			GlUtil.enableBlendTranslucent();

			HudElement.renderAll(event);

			GlStateManager.enableDepth();
			GlStateManager.popMatrix();
		} else {
			pointedEntity = null;
		}
		MC.mcProfiler.endSection();
	}

	/** Allows a custom distance
	 * @see net.minecraft.client.renderer.EntityRenderer#getMouseOver(float) */
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
}
