package tk.nukeduck.hud.element.vanilla;

import static tk.nukeduck.hud.BetterHud.MANAGER;
import static tk.nukeduck.hud.BetterHud.MC;
import static tk.nukeduck.hud.BetterHud.SPACER;
import static tk.nukeduck.hud.util.mode.GlMode.DEFAULT;
import static tk.nukeduck.hud.util.mode.GlMode.INVERT;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
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
import tk.nukeduck.hud.element.HudElement;
import tk.nukeduck.hud.element.settings.SettingBoolean;
import tk.nukeduck.hud.element.settings.SettingChoose;
import tk.nukeduck.hud.element.settings.SettingPosition;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Direction;
import tk.nukeduck.hud.util.Direction.Options;
import tk.nukeduck.hud.util.GlUtil;
import tk.nukeduck.hud.util.Point;
import tk.nukeduck.hud.util.mode.GlMode;

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

	public Crosshair() {
		super("crosshair", new SettingPosition(Options.I, Options.NONE));

		position.setEnableOn(() -> attackIndicator.get());
		settings.add(attackIndicator);
		settings.add(indicatorType);
	}

	@Override
	public void loadDefaults() {
		super.loadDefaults();

		attackIndicator.set(true);
		indicatorType.setIndex(0);
		position.setPreset(Direction.CENTER);
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
	protected GlMode getMode() {
		return INVERT;
	}

	@Override
	protected Bounds render(Event event) {
		if(MC.gameSettings.showDebugInfo && !MC.gameSettings.reducedDebugInfo && !MC.player.hasReducedDebug()) {
			renderAxes(MANAGER.getScreen().getAnchor(Direction.CENTER), getPartialTicks(event));
		} else {
			Bounds texture = new Bounds(16, 16);
			Point position = new Bounds(texture).anchor(MANAGER.getScreen(), Direction.CENTER).getPosition();

			GlUtil.drawTexturedModalRect(position, texture);

			if(attackIndicator.get()) {
				return renderAttackIndicator();
			}
		}
		return null;
	}

	private Bounds renderAttackIndicator() {
		Bounds bounds = indicatorType.getIndex() == 0 ? new Bounds(16, 8) : new Bounds(18, 18);

		if(position.isDirection(Direction.SOUTH)) {
			Direction primary = MC.player.getPrimaryHand() == EnumHandSide.RIGHT ? Direction.EAST : Direction.WEST;
			bounds = bounds.align(HudElement.HOTBAR.getLastBounds().grow(SPACER).getAnchor(primary), primary.mirrorColumn());
		} else if(position.isDirection(Direction.CENTER)) {
			bounds = bounds.positioned(Direction.CENTER, new Point(0, SPACER), Direction.NORTH);
		} else {
			bounds = position.applyTo(bounds);
		}

		float attackStrength = MC.player.getCooledAttackStrength(0);

		if(indicatorType.getIndex() == 0) {
			if(attackStrength >= 1) {
				if(MC.pointedEntity != null && MC.pointedEntity instanceof EntityLivingBase && MC.player.getCooldownPeriod() > 5 && ((EntityLivingBase)MC.pointedEntity).isEntityAlive()) {
					GlUtil.drawTexturedModalRect(bounds.getPosition(), new Bounds(68, 94, 16, 8));
				}
			} else {
				GlUtil.drawTexturedProgressBar(bounds.getPosition(), new Bounds(36, 94, 16, 8), new Bounds(52, 94, 16, 8), attackStrength, Direction.EAST);
			}
		} else if(attackStrength < 1) {
			GlUtil.drawTexturedProgressBar(bounds.getPosition(), new Bounds(0, 94, 18, 18), new Bounds(18, 94, 18, 18), attackStrength, Direction.NORTH);
		}
		return bounds;
	}

	private void renderAxes(Point center, float partialTicks) {
		GlMode.push(DEFAULT);
		GlStateManager.pushMatrix();
		GlStateManager.translate(center.getX(), center.getY(), 0);

		Entity entity = MC.getRenderViewEntity();
		GlStateManager.rotate(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks, -1.0F, 0.0F, 0.0F);
		GlStateManager.rotate(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks, 0.0F, 1.0F, 0.0F);
		GlStateManager.scale(-1.0F, -1.0F, -1.0F);
		OpenGlHelper.renderDirections(10);

		GlStateManager.popMatrix();
		GlMode.pop();
	}
}
