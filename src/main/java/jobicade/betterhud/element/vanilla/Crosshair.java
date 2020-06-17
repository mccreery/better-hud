package jobicade.betterhud.element.vanilla;

import static jobicade.betterhud.BetterHud.MANAGER;

import jobicade.betterhud.element.OverlayElement;
import jobicade.betterhud.element.settings.DirectionOptions;
import jobicade.betterhud.element.settings.IStringSetting;
import jobicade.betterhud.element.settings.SettingBoolean;
import jobicade.betterhud.element.settings.SettingChoose;
import jobicade.betterhud.element.settings.SettingPosition;
import jobicade.betterhud.events.OverlayContext;
import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Point;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.registry.OverlayElements;
import jobicade.betterhud.util.GlUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.MinecraftForge;

public class Crosshair extends OverlayElement {
	private SettingPosition position;
	private SettingBoolean attackIndicator;
	private SettingChoose indicatorType;

	public Crosshair() {
		super("crosshair");

		settings.addChildren(
			position = new SettingPosition(DirectionOptions.I, DirectionOptions.NONE),
			attackIndicator = new SettingBoolean("attackIndicator") {
				@Override
				public IStringSetting getStringSetting() {
					return null;
				}

				@Override
				public String getUnlocalizedName() {
					return "options.attackIndicator";
				}

				@Override
				public boolean get() {
					return Minecraft.getMinecraft().gameSettings.attackIndicator != 0;
				}

				@Override
				public void set(boolean value) {
					Minecraft.getMinecraft().gameSettings.attackIndicator = value ? indicatorType.getIndex() + 1 : 0;
					Minecraft.getMinecraft().gameSettings.saveOptions();
				}
			}.setValuePrefix(SettingBoolean.VISIBLE),
			indicatorType = new SettingChoose(null, "mode1", "mode2") {
				@Override
				public int getIndex() {
					return Math.max(Minecraft.getMinecraft().gameSettings.attackIndicator - 1, 0);
				}

				@Override
				public void setIndex(int index) {
					if(index >= 0 && index < 2) {
						Minecraft.getMinecraft().gameSettings.attackIndicator = attackIndicator.get() ? index + 1 : 0;
					}
				}
			}.setValuePrefix("options.attack.").setEnableOn(attackIndicator::get)
		);
		position.setEnableOn(() -> attackIndicator.get());
	}

	@Override
	public void loadDefaults() {
		super.loadDefaults();

		attackIndicator.set(true);
		indicatorType.setIndex(0);
		position.setPreset(Direction.CENTER);
	}

	@Override
	public boolean shouldRender(OverlayContext context) {
		return Minecraft.getMinecraft().gameSettings.thirdPersonView == 0
			&& (!Minecraft.getMinecraft().playerController.isSpectator() || canInteract())
			&& !MinecraftForge.EVENT_BUS.post(new RenderGameOverlayEvent.Pre(context.getEvent(), ElementType.CROSSHAIRS));
	}

	/** @return {@code true} if the player is looking at something that can be interacted with in spectator mode */
	private boolean canInteract() {
		if(Minecraft.getMinecraft().pointedEntity != null) {
			return true;
		} else {
			RayTraceResult trace = Minecraft.getMinecraft().objectMouseOver;
			if(trace == null || trace.typeOfHit != Type.BLOCK) return false;

			BlockPos pos = trace.getBlockPos();
			IBlockState state = Minecraft.getMinecraft().world.getBlockState(pos);
			return state.getBlock().hasTileEntity(state) && Minecraft.getMinecraft().world.getTileEntity(pos) instanceof IInventory;
		}
	}

	@Override
	public Rect render(OverlayContext context) {
		Rect bounds = null;

		if(Minecraft.getMinecraft().gameSettings.showDebugInfo && !Minecraft.getMinecraft().gameSettings.reducedDebugInfo && !Minecraft.getMinecraft().player.hasReducedDebug()) {
			renderAxes(MANAGER.getScreen().getAnchor(Direction.CENTER), context.getPartialTicks());
		} else {
			Rect texture = new Rect(16, 16);

			// Vanilla crosshair is offset by (1, 1) for some reason
			Rect crosshair = new Rect(texture).anchor(MANAGER.getScreen(), Direction.CENTER).translate(1, 1);

			GlStateManager.blendFunc(SourceFactor.ONE_MINUS_DST_COLOR, DestFactor.ONE_MINUS_SRC_COLOR);
			GlStateManager.enableAlpha();
			GlUtil.drawRect(crosshair, texture);

			if(attackIndicator.get()) {
				bounds = renderAttackIndicator();
			}
			GlUtil.blendFuncSafe(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ZERO, DestFactor.ONE);
			GlStateManager.disableAlpha();
		}

		MinecraftForge.EVENT_BUS.post(new RenderGameOverlayEvent.Post(context.getEvent(), ElementType.CROSSHAIRS));
		return bounds;
	}

	private Rect renderAttackIndicator() {
		Rect bounds = indicatorType.getIndex() == 0 ? new Rect(16, 8) : new Rect(18, 18);

		if(position.isDirection(Direction.SOUTH)) {
			Direction primary = Minecraft.getMinecraft().player.getPrimaryHand() == EnumHandSide.RIGHT ? Direction.EAST : Direction.WEST;
			// Vanilla indicator is also offset by (1, 0) regardless of main hand
			bounds = bounds.align(OverlayElements.HOTBAR.getLastBounds().grow(5).getAnchor(primary), primary.mirrorCol()).translate(1, 0);
		} else if(position.isDirection(Direction.CENTER)) {
			bounds = bounds.align(MANAGER.getScreen().getAnchor(Direction.CENTER).add(0, 9), Direction.NORTH);
		} else {
			bounds = position.applyTo(bounds);
		}

		float attackStrength = Minecraft.getMinecraft().player.getCooledAttackStrength(0);

		if(indicatorType.getIndex() == 0) {
			if(attackStrength >= 1) {
				if (
					Minecraft.getMinecraft().pointedEntity instanceof EntityLivingBase
					&& ((EntityLivingBase)Minecraft.getMinecraft().pointedEntity).isEntityAlive()
					&& Minecraft.getMinecraft().player.getCooldownPeriod() > 5
				) {
					GlUtil.drawRect(bounds.resize(16, 16), new Rect(68, 94, 16, 16));
				}
			} else {
				GlUtil.drawTexturedProgressBar(bounds.getPosition(), new Rect(36, 94, 16, 8), new Rect(52, 94, 16, 8), attackStrength, Direction.EAST);
			}
		} else if(attackStrength < 1) {
			GlUtil.blendFuncSafe(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ZERO, DestFactor.ONE);
			GlUtil.drawTexturedProgressBar(bounds.getPosition(), new Rect(0, 94, 18, 18), new Rect(18, 94, 18, 18), attackStrength, Direction.NORTH);
		}
		return bounds;
	}

	private void renderAxes(Point center, float partialTicks) {
		GlStateManager.pushMatrix();
		GlStateManager.translate(center.getX(), center.getY(), 0);

		Entity entity = Minecraft.getMinecraft().getRenderViewEntity();
		GlStateManager.rotate(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks, -1.0F, 0.0F, 0.0F);
		GlStateManager.rotate(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks, 0.0F, 1.0F, 0.0F);
		GlStateManager.scale(-1.0F, -1.0F, -1.0F);
		OpenGlHelper.renderDirections(10);

		GlStateManager.popMatrix();
	}
}
