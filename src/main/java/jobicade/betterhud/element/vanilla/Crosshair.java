package jobicade.betterhud.element.vanilla;

import static jobicade.betterhud.BetterHud.MANAGER;
import static jobicade.betterhud.BetterHud.MC;

import java.util.List;

import jobicade.betterhud.element.HudElement;
import jobicade.betterhud.element.settings.DirectionOptions;
import jobicade.betterhud.element.settings.Setting;
import jobicade.betterhud.element.settings.SettingBoolean;
import jobicade.betterhud.element.settings.SettingChoose;
import jobicade.betterhud.element.settings.SettingPosition;
import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Point;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.util.GlUtil;
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
import net.minecraftforge.eventbus.api.Event;

public class Crosshair extends OverrideElement {
	private SettingBoolean attackIndicator;
	private SettingChoose indicatorType;

	public Crosshair() {
		super("crosshair", new SettingPosition(DirectionOptions.I, DirectionOptions.NONE));

		position.setEnableOn(() -> attackIndicator.get());
	}

	@Override
	protected void addSettings(List<Setting<?>> settings) {
		super.addSettings(settings);

		settings.add(attackIndicator = new SettingBoolean(null) {
			@Override
			public Boolean get() {
				return MC.gameSettings.attackIndicator != 0;
			}

			@Override
			public void set(Boolean value) {
				MC.gameSettings.attackIndicator = value ? indicatorType.getIndex() + 1 : 0;
				MC.gameSettings.saveOptions();
			}
		});
		attackIndicator.setValuePrefix(SettingBoolean.VISIBLE).setUnlocalizedName("options.attackIndicator");

		settings.add(indicatorType = new SettingChoose(null, 2) {
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
		});
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
		return super.shouldRender(event)
			&& MC.gameSettings.thirdPersonView == 0
			&& (!MC.playerController.isSpectatorMode() || canInteract());
	}

	/** @return {@code true} if the player is looking at something that can be interacted with in spectator mode */
	private boolean canInteract() {
		if(MC.pointedEntity != null) {
			return true;
		} else {
			RayTraceResult trace = MC.objectMouseOver;
			if(trace == null || trace.type != Type.BLOCK) return false;

			BlockPos pos = trace.getBlockPos();
			IBlockState state = MC.world.getBlockState(pos);
			return state.getBlock().hasTileEntity(state) && MC.world.getTileEntity(pos) instanceof IInventory;
		}
	}

	@Override
	protected Rect render(Event event) {
		Rect bounds = null;

		if(MC.gameSettings.showDebugInfo && !MC.gameSettings.reducedDebugInfo && !MC.player.hasReducedDebug()) {
			renderAxes(MANAGER.getScreen().getAnchor(Direction.CENTER), getPartialTicks(event));
		} else {
			Rect texture = new Rect(16, 16);

			// Vanilla crosshair is offset by (1, 1) for some reason
			Rect crosshair = new Rect(texture).anchor(MANAGER.getScreen(), Direction.CENTER).translate(1, 1);

			GlStateManager.blendFunc(SourceFactor.ONE_MINUS_DST_COLOR, DestFactor.ONE_MINUS_SRC_COLOR);
			GlStateManager.enableAlphaTest();
			GlUtil.drawRect(crosshair, texture);

			if(attackIndicator.get()) {
				bounds = renderAttackIndicator();
			}
			GlUtil.blendFuncSafe(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ZERO, DestFactor.ONE);
			GlStateManager.disableAlphaTest();
		}
		return bounds;
	}

	private Rect renderAttackIndicator() {
		Rect bounds = indicatorType.getIndex() == 0 ? new Rect(16, 8) : new Rect(18, 18);

		if(position.isDirection(Direction.SOUTH)) {
			Direction primary = MC.player.getPrimaryHand() == EnumHandSide.RIGHT ? Direction.EAST : Direction.WEST;
			// Vanilla indicator is also offset by (1, 0) regardless of main hand
			bounds = bounds.align(HudElement.HOTBAR.getLastBounds().grow(5).getAnchor(primary), primary.mirrorCol()).translate(1, 0);
		} else if(position.isDirection(Direction.CENTER)) {
			bounds = bounds.align(MANAGER.getScreen().getAnchor(Direction.CENTER).add(0, 9), Direction.NORTH);
		} else {
			bounds = position.applyTo(bounds);
		}

		float attackStrength = MC.player.getCooledAttackStrength(0);

		if(indicatorType.getIndex() == 0) {
			if(attackStrength >= 1) {
				if(MC.pointedEntity != null && MC.pointedEntity instanceof EntityLivingBase && MC.player.getCooldownPeriod() > 5 && ((EntityLivingBase)MC.pointedEntity).isAlive()) {
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
		GlStateManager.translatef(center.getX(), center.getY(), 0);

		Entity entity = MC.getRenderViewEntity();
		GlStateManager.rotatef(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks, -1.0F, 0.0F, 0.0F);
		GlStateManager.rotatef(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks, 0.0F, 1.0F, 0.0F);
		GlStateManager.scalef(-1.0F, -1.0F, -1.0F);
		OpenGlHelper.renderDirections(10);

		GlStateManager.popMatrix();
	}
}
