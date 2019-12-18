package jobicade.betterhud.events;

import static jobicade.betterhud.BetterHud.*;
import static net.minecraftforge.client.GuiIngameForge.*;

import java.util.List;

import org.lwjgl.opengl.GL11;

import com.google.common.base.Predicate;

import jobicade.betterhud.BetterHud;
import jobicade.betterhud.element.HudElement;
import jobicade.betterhud.geom.Point;
import jobicade.betterhud.render.Color;
import jobicade.betterhud.render.GlSnapshot;
import jobicade.betterhud.util.GlUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceFluidMode;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public final class RenderEvents {
	private RenderEvents() {}

	public static void registerEvents() {
		MinecraftForge.EVENT_BUS.register(new RenderEvents());
	}

	@SubscribeEvent
	public void onRenderTick(RenderGameOverlayEvent.Pre event) {
		MC.profiler.startSection(MODID);

		boolean enabled = BetterHud.isEnabled();
		suppressVanilla(enabled);

		if(enabled && event.getType() == ElementType.ALL) {
			renderOverlay(event);
		}
		MC.profiler.endSection();
	}

	@SubscribeEvent
	public void worldRender(RenderWorldLastEvent event) {
		MC.profiler.startSection(MODID);

		if(BetterHud.isEnabled()) {
			Entity entity = getMouseOver(HudElement.GLOBAL.getBillboardDistance(), event.getPartialTicks());

			if(entity instanceof EntityLivingBase) {
				renderMobInfo(new RenderMobInfoEvent(event, (EntityLivingBase)entity));
			}
		}
		MC.profiler.endSection();
	}

	/**
	 * Modifies the OpenGL state for maximum compatibility with elements.
	 * This is only used for {@link #onRenderTick(net.minecraftforge.client.event.RenderGameOverlayEvent.Pre)}
	 */
	public static void beginOverlayState() {
		GlStateManager.blendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ZERO, DestFactor.ONE);
		GlStateManager.enableBlend();
		GlStateManager.disableAlphaTest();

		MC.getTextureManager().bindTexture(Gui.ICONS);
		GlStateManager.shadeModel(GL11.GL_SMOOTH);
	}

	/**
	 * Reverts the OpenGL state to the expected state at the time of the event.
	 * This is only used for {@link #onRenderTick(net.minecraftforge.client.event.RenderGameOverlayEvent.Pre))}
	 */
	public static void endOverlayState() {
		GlStateManager.blendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ZERO, DestFactor.ONE);
		GlStateManager.disableBlend();
		GlStateManager.disableAlphaTest();

		GlStateManager.bindTexture(0);
		GlStateManager.shadeModel(GL11.GL_FLAT);
	}

	/**
	 * Suppresses or unsuppresses vanilla HUD rendering.
	 * @param suppress {@code true} to suppress
	 */
	private static void suppressVanilla(boolean suppress) {
		boolean allow = !suppress;

		renderHotbar      = allow || MC.player.isSpectator();
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
		MANAGER.reset();
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

		GlStateManager.disableDepthTest();
		GlStateManager.enableBlend();
		GlStateManager.disableAlphaTest();
		Color.WHITE.apply();
		MC.getTextureManager().bindTexture(Gui.ICONS);

		GlStateManager.pushMatrix();
		GlUtil.setupBillboard(event.getEntity(), event.getPartialTicks(), HudElement.GLOBAL.getBillboardScale());

		if(HudElement.GLOBAL.isDebugMode()) {
			GlSnapshot pre = new GlSnapshot();
			HudElement.renderAll(event);
			mobInfoTracker.step(pre, new GlSnapshot());
		} else {
			HudElement.renderAll(event);
		}

		GlStateManager.popMatrix();

		MC.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		GlStateManager.enableAlphaTest();
		GlStateManager.enableDepthTest();
		GlStateManager.disableBlend();
	}

	/** Allows a custom distance
	 * @see net.minecraft.client.renderer.EntityRenderer#getMouseOver(float) */
	private static Entity getMouseOver(double distance, float partialTicks) {
		if(MC.world == null) return null;
		Entity viewEntity = MC.getRenderViewEntity();
		if(viewEntity == null) return null;

		Entity pointedEntity = null;

		MC.profiler.startSection("pick");

		RayTraceResult trace = viewEntity.rayTrace(distance, partialTicks,RayTraceFluidMode.ALWAYS);
		Vec3d eyePosition = viewEntity.getEyePosition(partialTicks);
		Vec3d lookDelta = viewEntity.getLookVec().scale(distance);

		if(trace != null) {
			distance = trace.hitVec.distanceTo(eyePosition);
		}

		AxisAlignedBB range = viewEntity.getBoundingBox().expand(lookDelta.x, lookDelta.y, lookDelta.z).grow(1, 1, 1);

		List<Entity> entitiesInRange = MC.world.getEntitiesInAABBexcluding(viewEntity, range, new Predicate<Entity>() {
			@Override
			public boolean apply(Entity entity) {
				return entity != null && entity.canBeCollidedWith();
			}
		});

		for(Entity entity : entitiesInRange) {
			AxisAlignedBB entityBox = entity.getBoundingBox().grow(entity.getCollisionBorderSize());
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
		MC.profiler.endSection();
		return pointedEntity;
	}
}
