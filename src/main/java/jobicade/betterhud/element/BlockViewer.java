package jobicade.betterhud.element;

import static jobicade.betterhud.BetterHud.SPACER;
import static jobicade.betterhud.BetterHud.MANAGER;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mojang.realmsclient.gui.ChatFormatting;

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
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import net.minecraftforge.fml.common.versioning.InvalidVersionSpecificationException;
import net.minecraftforge.fml.common.versioning.VersionRange;
import jobicade.betterhud.BetterHud;
import jobicade.betterhud.element.settings.DirectionOptions;
import jobicade.betterhud.element.settings.Legend;
import jobicade.betterhud.element.settings.Setting;
import jobicade.betterhud.element.settings.SettingBoolean;
import jobicade.betterhud.element.settings.SettingPosition;
import jobicade.betterhud.element.text.TextElement;
import jobicade.betterhud.network.InventoryNameQuery;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.util.GlUtil;

/**
 * @see BetterHud#onBlockBreak(net.minecraftforge.event.world.BlockEvent.BreakEvent)
 */
public class BlockViewer extends TextElement {
    private SettingBoolean showBlock, showIds, invNames;
    private RayTraceResult trace;
    private IBlockState state;
    private ItemStack stack;

    public BlockViewer() {
        super("blockViewer", new SettingPosition(DirectionOptions.I, DirectionOptions.WEST_EAST));
    }

    @Override
    protected void addSettings(List<Setting<?>> settings) {
        super.addSettings(settings);

        settings.add(new Legend("misc"));
        settings.add(showBlock = new SettingBoolean("showItem").setValuePrefix(SettingBoolean.VISIBLE));
        settings.add(showIds = new SettingBoolean("showIds").setValuePrefix(SettingBoolean.VISIBLE));
        settings.add(invNames = new SettingBoolean("invNames") {
            @Override
            public boolean enabled() {
                VersionRange versionRange;
                try {
                    versionRange = VersionRange.createFromVersionSpec("[1.4-beta,)");
                } catch (InvalidVersionSpecificationException e) {
                    throw new RuntimeException(e);
                }

                return super.enabled() && versionRange.containsVersion(BetterHud.getServerVersion());
            }
        });
    }

    @Override
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void loadDefaults() {
        super.loadDefaults();

        position.setPreset(Direction.NORTH);
        showBlock.set(true);
        showIds.set(false);
        invNames.set(true);
    }

    @Override
    public boolean shouldRender(Event event) {
        if(!super.shouldRender(event)) return false;

        trace = Minecraft.getInstance().getCameraEntity().func_174822_a(HudElement.GLOBAL.getBillboardDistance(), 1f);

        if(trace != null && trace.field_72313_a == RayTraceResult.Type.BLOCK) {
            state = Minecraft.getInstance().level.getBlockState(trace.func_178782_a());
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
        int vPad = 20 - Minecraft.getInstance().font.lineHeight;
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
    public Rect render(Event event) {
        GlStateManager.func_179097_i();
        return super.render(event);
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
        ItemStack stack = state.getBlock().getPickBlock(state, trace, Minecraft.getInstance().level, trace.func_178782_a(), Minecraft.getInstance().player);

        if(isStackEmpty(stack)) {
            // Pick block is disabled, however we can grab the information directly
            stack = new ItemStack(state.getBlock(), state.getBlock().func_176201_c(state));

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
            return I18n.get("tile.endPortal.name");
        }

        if(invNames.get() && state.getBlock().hasTileEntity(state)) {
            TileEntity tileEntity = Minecraft.getInstance().level.getBlockEntity(trace.func_178782_a());

            if(tileEntity instanceof IWorldNameable) {
                ITextComponent invName = ensureInvName(trace.func_178782_a());

                if(invName != null) {
                    return invName.func_150254_d();
                }
            }
        }

        return isStackEmpty(stack) ? state.getBlock().func_149732_F() : stack.func_82833_r();
    }

    /** @return Information about the block's related IDs */
    private String getIdString(IBlockState state) {
        String name = Block.field_149771_c.getKey(state.getBlock()).toString();
        int id = Block.func_149682_b(state.getBlock());
        int meta = state.getBlock().func_176201_c(state);

        return String.format("%s(%s:%d/#%04d)", ChatFormatting.YELLOW, name, meta, id);
    }

    @SubscribeEvent
    public void onPlayerDisconnected(ClientDisconnectionFromServerEvent event) {
        nameCache.clear();
    }

    @SubscribeEvent
    public void onPlayerChangeDimension(PlayerChangedDimensionEvent event) {
        nameCache.clear();
    }

    public static final Map<BlockPos, ITextComponent> nameCache = new HashMap<BlockPos, ITextComponent>();

    public void onNameReceived(BlockPos pos, ITextComponent name) {
        nameCache.put(pos, name);
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
            return Minecraft.getInstance().level.getBlockEntity(pos).getDisplayName();
        }
    }

    /** Considers {@code null} to be empty
     * @see ItemStack#isEmpty() */
    private static boolean isStackEmpty(ItemStack stack) {
        return stack == null || stack.isEmpty();
    }
}
