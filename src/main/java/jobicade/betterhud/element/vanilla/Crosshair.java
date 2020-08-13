package jobicade.betterhud.element.vanilla;

import static jobicade.betterhud.BetterHud.MANAGER;
import static jobicade.betterhud.BetterHud.MC;

import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;

import jobicade.betterhud.element.OverlayElement;
import jobicade.betterhud.element.settings.DirectionOptions;
import jobicade.betterhud.element.settings.SettingBoolean;
import jobicade.betterhud.element.settings.SettingChoose;
import jobicade.betterhud.element.settings.SettingPosition;
import jobicade.betterhud.events.OverlayContext;
import jobicade.betterhud.events.OverlayHook;
import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Point;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.registry.OverlayElements;
import jobicade.betterhud.util.GlUtil;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.settings.AttackIndicatorStatus;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.gui.ForgeIngameGui;

public class Crosshair extends OverlayElement {
    private SettingPosition position;
    private SettingBoolean attackIndicator;
    private SettingChoose indicatorType;

    public Crosshair() {
        super("crosshair");

        position = new SettingPosition(DirectionOptions.I, DirectionOptions.NONE);

        attackIndicator = new SettingBoolean(null) {
            @Override
            public boolean get() {
                return MC.gameSettings.attackIndicator != AttackIndicatorStatus.OFF;
            }

            @Override
            public void set(boolean value) {
                MC.gameSettings.attackIndicator = AttackIndicatorStatus.byId(value ? indicatorType.getIndex() + 1 : 0);
                MC.gameSettings.saveOptions();
            }

            @Override
            public String getUnlocalizedName() {
                return "options.attackIndicator";
            }
        };
        attackIndicator.setValuePrefix(SettingBoolean.VISIBLE);
        position.setEnableOn(attackIndicator::get);

        indicatorType = new SettingChoose(null, 2) {
            @Override
            public int getIndex() {
                return Math.max(MC.gameSettings.attackIndicator.getId() - 1, 0);
            }

            @Override
            public void setIndex(int index) {
                if(index >= 0 && index < 2) {
                    MC.gameSettings.attackIndicator = AttackIndicatorStatus.byId(attackIndicator.get() ? index + 1 : 0);
                }
            }

            @Override
            protected String getUnlocalizedValue() {
                return "options.attack." + modes[getIndex()];
            }
        };
        indicatorType.setEnableOn(attackIndicator::get);

        settings.addChildren(position, attackIndicator, indicatorType);
    }

    @Override
    public boolean shouldRender(OverlayContext context) {
        return ForgeIngameGui.renderCrosshairs
            && !OverlayHook.pre(context.getEvent(), ElementType.CROSSHAIRS)
            && MC.gameSettings.thirdPersonView == 0
            && (!MC.playerController.isSpectatorMode() || canInteract());
    }

    /** @return {@code true} if the player is looking at something that can be interacted with in spectator mode */
    private boolean canInteract() {
        if(MC.pointedEntity != null) {
            return true;
        } else {
            BlockRayTraceResult trace = (BlockRayTraceResult)MC.objectMouseOver;
            if(trace == null || trace.getType() != RayTraceResult.Type.BLOCK) return false;

            BlockPos pos = trace.getPos();
            BlockState state = MC.world.getBlockState(pos);
            return state.getBlock().hasTileEntity(state) && MC.world.getTileEntity(pos) instanceof IInventory;
        }
    }

    @Override
    public Rect render(OverlayContext context) {
        Rect bounds = null;

        if(MC.gameSettings.showDebugInfo && !MC.gameSettings.reducedDebugInfo && !MC.player.hasReducedDebug()) {
            renderAxes(MANAGER.getScreen().getAnchor(Direction.CENTER), context.getPartialTicks());
        } else {
            Rect texture = new Rect(16, 16);

            // Vanilla crosshair is offset by (1, 1) for some reason
            Rect crosshair = new Rect(texture).anchor(MANAGER.getScreen(), Direction.CENTER).translate(1, 1);

            RenderSystem.blendFunc(SourceFactor.ONE_MINUS_DST_COLOR, DestFactor.ONE_MINUS_SRC_COLOR);
            RenderSystem.enableAlphaTest();
            GlUtil.drawRect(crosshair, texture);

            if(attackIndicator.get()) {
                bounds = renderAttackIndicator();
            }
            GlUtil.blendFuncSafe(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ZERO, DestFactor.ONE);
            RenderSystem.disableAlphaTest();
        }

        OverlayHook.post(context.getEvent(), ElementType.CROSSHAIRS);
        return bounds;
    }

    private Rect renderAttackIndicator() {
        Rect bounds = indicatorType.getIndex() == 0 ? new Rect(16, 8) : new Rect(18, 18);

        if(position.isDirection(Direction.SOUTH)) {
            Direction primary = MC.player.getPrimaryHand() == HandSide.RIGHT ? Direction.EAST : Direction.WEST;
            // Vanilla indicator is also offset by (1, 0) regardless of main hand
            bounds = bounds.align(OverlayElements.HOTBAR.getLastBounds().grow(5).getAnchor(primary), primary.mirrorCol()).translate(1, 0);
        } else if(position.isDirection(Direction.CENTER)) {
            bounds = bounds.align(MANAGER.getScreen().getAnchor(Direction.CENTER).add(0, 9), Direction.NORTH);
        } else {
            bounds = position.applyTo(bounds);
        }

        float attackStrength = MC.player.getCooledAttackStrength(0);

        if(indicatorType.getIndex() == 0) {
            if(attackStrength >= 1) {
                if (
                    MC.pointedEntity instanceof LivingEntity
                    && ((LivingEntity)MC.pointedEntity).isAlive()
                    && MC.player.getCooldownPeriod() > 5
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
        RenderSystem.pushMatrix();
        RenderSystem.translatef(center.getX(), center.getY(), MC.ingameGUI.getBlitOffset());

        ActiveRenderInfo activerenderinfo = MC.gameRenderer.getActiveRenderInfo();
        RenderSystem.rotatef(activerenderinfo.getPitch(), -1.0F, 0.0F, 0.0F);
        RenderSystem.rotatef(activerenderinfo.getYaw(), 0.0F, 1.0F, 0.0F);
        RenderSystem.scalef(-1.0F, -1.0F, -1.0F);
        RenderSystem.renderCrosshair(10);

        RenderSystem.popMatrix();
    }
}
