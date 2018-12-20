package jobicade.betterhud.events;

import static jobicade.betterhud.BetterHud.MANAGER;
import static jobicade.betterhud.BetterHud.MC;
import static jobicade.betterhud.BetterHud.MODID;
import static net.minecraftforge.client.GuiIngameForge.renderAir;
import static net.minecraftforge.client.GuiIngameForge.renderArmor;
import static net.minecraftforge.client.GuiIngameForge.renderCrosshairs;
import static net.minecraftforge.client.GuiIngameForge.renderExperiance;
import static net.minecraftforge.client.GuiIngameForge.renderFood;
import static net.minecraftforge.client.GuiIngameForge.renderHealth;
import static net.minecraftforge.client.GuiIngameForge.renderHealthMount;
import static net.minecraftforge.client.GuiIngameForge.renderHelmet;
import static net.minecraftforge.client.GuiIngameForge.renderHotbar;
import static net.minecraftforge.client.GuiIngameForge.renderJumpBar;
import static net.minecraftforge.client.GuiIngameForge.renderObjective;
import static net.minecraftforge.client.GuiIngameForge.renderPortal;
import static net.minecraftforge.client.GuiIngameForge.renderVignette;

import java.util.List;

import com.google.common.base.Predicate;

import org.lwjgl.opengl.GL11;

import jobicade.betterhud.BetterHud;
import jobicade.betterhud.element.HudElement;
import jobicade.betterhud.geom.Point;
import jobicade.betterhud.render.GlSnapshot;
import jobicade.betterhud.util.GlUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class RenderEvents {
	private RenderEvents() {}

	public static void registerEvents() {
		MinecraftForge.EVENT_BUS.register(new RenderEvents());
	}

	@SubscribeEvent
	public void onRenderTick(RenderGameOverlayEvent.Pre event) {
		MC.mcProfiler.startSection(MODID);

		boolean enabled = BetterHud.isEnabled();
		suppressVanilla(enabled);

		if(enabled && event.getType() == ElementType.ALL) {
			renderOverlay(event);
		}
		MC.mcProfiler.endSection();
	}

	@SubscribeEvent
	public void worldRender(RenderWorldLastEvent event) {
		MC.mcProfiler.startSection(MODID);

		if(BetterHud.isEnabled()) {
			Entity entity = getMouseOver(HudElement.GLOBAL.getBillboardDistance(), event.getPartialTicks());

			if(entity instanceof EntityLivingBase) {
				renderMobInfo(new RenderMobInfoEvent(event, (EntityLivingBase)entity));
			}
		}
		MC.mcProfiler.endSection();
	}

	/**
	 * Modifies the OpenGL state for maximum compatibility with elements.
	 * This is only used for {@link #onRenderTick(net.minecraftforge.client.event.RenderGameOverlayEvent.Pre)}
	 */
	private static void beginOverlayState() {
		GlStateManager.enableBlend();
		MC.getTextureManager().bindTexture(Gui.ICONS);
		GlStateManager.shadeModel(GL11.GL_SMOOTH);
	}

	/**
	 * Reverts the OpenGL state to the expected state at the time of the event.
	 * This is only used for {@link #onRenderTick(net.minecraftforge.client.event.RenderGameOverlayEvent.Pre))}
	 */
	private static void endOverlayState() {
		GlStateManager.disableBlend();
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

		GlStateManager.disableDepth();
		GlStateManager.enableBlend();

		GlStateManager.pushMatrix();
		GlUtil.setupBillboard(event.getEntity(), event.getPartialTicks(), HudElement.GLOBAL.getBillboardScale());
		HudElement.renderAll(event);
		GlStateManager.popMatrix();

		GlStateManager.enableDepth();
		GlStateManager.disableBlend();
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
