package jobicade.betterhud.element;

import static jobicade.betterhud.BetterHud.MANAGER;
import static jobicade.betterhud.BetterHud.MC;
import static jobicade.betterhud.BetterHud.SPACER;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mojang.realmsclient.gui.ChatFormatting;

import jobicade.betterhud.BetterHud;
import jobicade.betterhud.element.settings.Legend;
import jobicade.betterhud.element.settings.SettingBoolean;
import jobicade.betterhud.element.text.TextElement;
import jobicade.betterhud.events.OverlayContext;
import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.network.InventoryNameQuery;
import jobicade.betterhud.registry.HudElements;
import jobicade.betterhud.util.GlUtil;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IWorldNameable;
import net.minecraftforge.fml.common.versioning.InvalidVersionSpecificationException;
import net.minecraftforge.fml.common.versioning.VersionRange;

/**
 * @see BetterHud#onBlockBreak(net.minecraftforge.event.world.BlockEvent.BreakEvent)
 */
public class BlockViewer extends TextElement {
    private SettingBoolean showBlock, showIds, invNames;
    private RayTraceResult trace;
    private IBlockState state;
    private ItemStack stack;

    public BlockViewer() {
        super("blockViewer");

        showBlock = new SettingBoolean("showItem");
        showBlock.setValuePrefix(SettingBoolean.VISIBLE);

        showIds = new SettingBoolean("showIds");
        showIds.setValuePrefix(SettingBoolean.VISIBLE);

        invNames = new SettingBoolean("invNames");
        invNames.setEnableOn(() -> {
            VersionRange versionRange;
            try {
                versionRange = VersionRange.createFromVersionSpec("[1.4-beta,)");
            } catch (InvalidVersionSpecificationException e) {
                throw new RuntimeException(e);
            }

            return versionRange.containsVersion(BetterHud.getServerVersion());
        });

        settings.addChildren(new Legend("misc"), showBlock, showIds, invNames);
    }

    @Override
    public boolean shouldRender(OverlayContext context) {
        trace = MC.getRenderViewEntity().rayTrace(HudElements.GLOBAL.getBillboardDistance(), 1f);

        if(trace != null && trace.typeOfHit == RayTraceResult.Type.BLOCK) {
            state = MC.world.getBlockState(trace.getBlockPos());
            stack = getDisplayStack(trace, state);
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected List<String> getText() {
        String text = getBlockName(trace, state, stack);
        if(showIds.get()) text += " " + getIdString(state);

        return Arrays.asList(text);
    }

    @Override
    protected Rect getPadding() {
        int vPad = 20 - MC.fontRenderer.FONT_HEIGHT;
        int bottom = vPad / 2;
        Rect bounds = Rect.createPadding(5, vPad - bottom, 5, bottom);

        if(stack != null && showBlock.get()) {
            if(position.getContentAlignment() == Direction.EAST) {
                bounds = bounds.withRight(bounds.getRight() + 21);
            } else {
                bounds = bounds.withLeft(bounds.getLeft() - 21);
            }
        }
        return bounds;
    }

    @Override
    protected void drawBorder(Rect bounds, Rect padding, Rect margin) {
        GlUtil.drawTooltipBox(bounds);
    }

    @Override
    public Rect render(OverlayContext context) {
        GlStateManager.disableDepth();
        return super.render(context);
    }

    @Override
    protected void drawExtras(Rect bounds) {
        if(stack != null && showBlock.get()) {
            Rect stackRect = new Rect(16, 16).anchor(bounds.grow(-5, -2, -5, -2), position.getContentAlignment());
            GlUtil.renderSingleItem(stack, stackRect.getPosition());
        }
    }

    @Override
    protected Rect moveRect(Rect bounds) {
        if(position.isDirection(Direction.CENTER)) {
            return bounds.align(MANAGER.getScreen().getAnchor(Direction.CENTER).add(0, -SPACER), Direction.SOUTH);
        } else {
            return super.moveRect(bounds);
        }
    }

    /** Creates the most representative item stack for the given result.<br>
     * If the block has no {@link net.minecraft.item.ItemBlock}, it is impossible to create a stack.
     *
     * @see net.minecraftforge.common.ForgeHooks#onPickBlock(RayTraceResult, net.minecraft.entity.player.EntityPlayer, net.minecraft.world.World) */
    private ItemStack getDisplayStack(RayTraceResult trace, IBlockState state) {
        ItemStack stack = state.getBlock().getPickBlock(state, trace, MC.world, trace.getBlockPos(), MC.player);

        if(isStackEmpty(stack)) {
            // Pick block is disabled, however we can grab the information directly
            stack = new ItemStack(state.getBlock(), state.getBlock().getMetaFromState(state));

            if(isStackEmpty(stack)) { // There's no registered ItemBlock, no stack exists
                return null;
            }
        }
        return stack;
    }

    /** Chooses the best name for the given result and its related stack.
     * @param stack The direct result of {@link #getDisplayStack(RayTraceResult, IBlockState)}. May be {@code null}
     *
     * @see ItemStack#getDisplayName()
     * @see TileEntity#getDisplayName()
     * @see net.minecraft.item.ItemBlock#getUnlocalizedName(ItemStack) */
    private String getBlockName(RayTraceResult trace, IBlockState state, ItemStack stack) {
        if(state.getBlock() == Blocks.END_PORTAL) {
            return I18n.format("tile.endPortal.name");
        }

        if(invNames.get() && state.getBlock().hasTileEntity(state)) {
            TileEntity tileEntity = MC.world.getTileEntity(trace.getBlockPos());

            if(tileEntity instanceof IWorldNameable) {
                ITextComponent invName = ensureInvName(trace.getBlockPos());

                if(invName != null) {
                    return invName.getFormattedText();
                }
            }
        }

        return isStackEmpty(stack) ? state.getBlock().getLocalizedName() : stack.getDisplayName();
    }

    /** @return Information about the block's related IDs */
    private String getIdString(IBlockState state) {
        String name = Block.REGISTRY.getNameForObject(state.getBlock()).toString();
        int id = Block.getIdFromBlock(state.getBlock());
        int meta = state.getBlock().getMetaFromState(state);

        return String.format("%s(%s:%d/#%04d)", ChatFormatting.YELLOW, name, meta, id);
    }

    public static final Map<BlockPos, ITextComponent> nameCache = new HashMap<BlockPos, ITextComponent>();

    public void onNameReceived(BlockPos pos, ITextComponent name) {
        nameCache.put(pos, name);
    }

    public void onChangeWorld() {
        nameCache.clear();
    }

    /** If the client doesn't know the name of an inventory, this method
     * asks the server, then until the response is given, a generic
     * container name will be returned. When the client finds out the actual name
     * of the inventory, it will return that value */
    private static ITextComponent ensureInvName(BlockPos pos) {
        if(!nameCache.containsKey(pos)) {
            BetterHud.NET_WRAPPER.sendToServer(new InventoryNameQuery.Request(pos));
            nameCache.put(pos, null);
        }
        ITextComponent name = nameCache.get(pos);

        if(name != null) {
            return name;
        } else {
            return MC.world.getTileEntity(pos).getDisplayName();
        }
    }

    /** Considers {@code null} to be empty
     * @see ItemStack#isEmpty() */
    private static boolean isStackEmpty(ItemStack stack) {
        return stack == null || stack.isEmpty();
    }
}
