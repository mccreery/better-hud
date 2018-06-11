package tk.nukeduck.hud.element.vanilla;

import static tk.nukeduck.hud.BetterHud.ICONS;
import static tk.nukeduck.hud.BetterHud.MANAGER;
import static tk.nukeduck.hud.BetterHud.MC;
import static tk.nukeduck.hud.BetterHud.SPACER;

import net.minecraft.block.state.IBlockState;
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
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.Event;
import tk.nukeduck.hud.BetterHud;
import tk.nukeduck.hud.element.HudElement;
import tk.nukeduck.hud.element.settings.SettingBoolean;
import tk.nukeduck.hud.element.settings.SettingChoose;
import tk.nukeduck.hud.element.settings.SettingPosition;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Direction;
import tk.nukeduck.hud.util.GlUtil;
import tk.nukeduck.hud.util.Point;

public class Crosshair extends OverrideElement {
	private final SettingBoolean attackIndicator = (SettingBoolean)new SettingBoolean(null) {
		@Override
		public Boolean get() {
			return MC.gameSettings.attackIndicator != 0;
		}

		@Override
		public void set(Boolean value) {
			MC.gameSettings.attackIndicator = value ? indicatorType.getIndex() + 1 : 0;
			MC.gameSettings.saveOptions();
		}
	}.setUnlocalizedValue(SettingBoolean.VISIBLE).setUnlocalizedName("options.attackIndicator");

	private final SettingChoose indicatorType = (SettingChoose)new SettingChoose(null, 2) {
		@Override
		public boolean enabled() {
			return super.enabled() && attackIndicator.get();
		}

		@Override
		public int getIndex() {
			return Math.max(MC.gameSettings.attackIndicator - 1, 0);
		}

		@Override
		public void setIndex(int index) {
			if(index >= 0 && index < 2) {
				MC.gameSettings.attackIndicator = attackIndicator.get() ? index + 1 : 0;
			}
		}

		@Override
		protected String getUnlocalizedValue() {
			return "options.attack." + modes[getIndex()];
		}
	};

	private final SettingPosition position = new SettingPosition("position", Direction.CORNERS | Direction.VERTICAL) {
		@Override
		public boolean enabled() {
			return super.enabled() && indicatorType.enabled();
		}
	};

	public Crosshair() {
		super("crosshair");

		settings.add(attackIndicator);
		settings.add(indicatorType);
		settings.add(position);
	}

	@Override
	public void loadDefaults() {
		super.loadDefaults();

		attackIndicator.set(true);
		indicatorType.setIndex(0);
		position.set(Direction.CENTER);
	}

	@Override
	protected ElementType getType() {
		return ElementType.CROSSHAIRS;
	}

	@Override
	public boolean shouldRender(Event event) {
		return MC.gameSettings.thirdPersonView == 0
			&& (!MC.playerController.isSpectator() || canInteract())
			&& super.shouldRender(event);
	}

	/** @return {@code true} if the player is looking at something that can be interacted with in spectator mode */
	private boolean canInteract() {
		if(MC.pointedEntity != null) {
			return true;
		} else {
			RayTraceResult trace = MC.objectMouseOver;
			if(trace == null || trace.typeOfHit != Type.BLOCK) return false;

			BlockPos pos = trace.getBlockPos();
			IBlockState state = MC.world.getBlockState(pos);
			return state.getBlock().hasTileEntity(state) && MC.world.getTileEntity(pos) instanceof IInventory;
		}
	}

	@Override
	protected Bounds render(Event event) {
		Point resolution = MANAGER.getResolution();

		if(MC.gameSettings.showDebugInfo && !MC.gameSettings.reducedDebugInfo && !MC.player.hasReducedDebug()) {
			renderAxes(resolution.scale(0.5f, 0.5f), getPartialTicks(event));
		} else {
			GlStateManager.enableAlpha();
			GlStateManager.tryBlendFuncSeparate(SourceFactor.ONE_MINUS_DST_COLOR, DestFactor.ONE_MINUS_SRC_COLOR, SourceFactor.ONE, DestFactor.ZERO);

			Bounds texture = new Bounds(16, 16);
			Point position = Direction.CENTER.anchor(new Bounds(texture), MANAGER.getScreen()).position;

			MC.getTextureManager().bindTexture(BetterHud.ICONS);
			GlUtil.drawTexturedModalRect(position, texture);

			GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);

			if(attackIndicator.get()) {
				return renderAttackIndicator();
			}
		}
		return null;
	}

	private Bounds renderAttackIndicator() {
		Bounds bounds = indicatorType.getIndex() == 0 ? new Bounds(16, 8) : new Bounds(18, 18);

		if(position.getDirection() == Direction.SOUTH) {
			Direction primary = MC.player.getPrimaryHand() == EnumHandSide.RIGHT ? Direction.EAST : Direction.WEST;
			primary.mirrorColumn().align(bounds, primary.getAnchor(HudElement.HOTBAR.getLastBounds().pad(SPACER)));
		} else if(position.getDirection() == Direction.CENTER) {
			bounds.position(Direction.CENTER, new Point(0, SPACER), Direction.NORTH);
		} else {
			position.applyTo(bounds);
		}

		float attackStrength = MC.player.getCooledAttackStrength(0);

		if(indicatorType.getIndex() == 0) {
			if(attackStrength >= 1) {
				if(MC.pointedEntity != null && MC.pointedEntity instanceof EntityLivingBase && MC.player.getCooldownPeriod() > 5 && ((EntityLivingBase)MC.pointedEntity).isEntityAlive()) {
					MC.getTextureManager().bindTexture(ICONS);
					GlUtil.drawTexturedModalRect(bounds.position, new Bounds(68, 94, 16, 8));
				}
			} else {
				MC.getTextureManager().bindTexture(ICONS);
				GlUtil.drawTexturedProgressBar(bounds.position, new Bounds(36, 94, 16, 8), new Bounds(52, 94, 16, 8), attackStrength, Direction.EAST);
			}
		} else if(attackStrength < 1) {
			GlUtil.drawTexturedProgressBar(bounds.position, new Bounds(0, 94, 18, 18), new Bounds(18, 94, 18, 18), attackStrength, Direction.NORTH);
		}
		return bounds;
	}

	private void renderAxes(Point center, float partialTicks) {
		GlStateManager.pushMatrix();
		GlStateManager.translate(center.x, center.y, 0);

		Entity entity = MC.getRenderViewEntity();
		GlStateManager.rotate(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks, -1.0F, 0.0F, 0.0F);
		GlStateManager.rotate(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks, 0.0F, 1.0F, 0.0F);
		GlStateManager.scale(-1.0F, -1.0F, -1.0F);
		OpenGlHelper.renderDirections(10);

		GlStateManager.popMatrix();
	}
}
