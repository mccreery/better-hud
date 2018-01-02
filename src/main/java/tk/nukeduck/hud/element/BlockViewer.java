package tk.nukeduck.hud.element;

import static tk.nukeduck.hud.BetterHud.MC;
import static tk.nukeduck.hud.BetterHud.SPACER;

import java.util.HashMap;
import java.util.Map;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IWorldNameable;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import tk.nukeduck.hud.BetterHud;
import tk.nukeduck.hud.element.settings.Legend;
import tk.nukeduck.hud.element.settings.SettingBoolean;
import tk.nukeduck.hud.element.settings.SettingPosition;
import tk.nukeduck.hud.element.settings.SettingSlider;
import tk.nukeduck.hud.network.InventoryNameQuery;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Colors;
import tk.nukeduck.hud.util.Direction;
import tk.nukeduck.hud.util.FuncsUtil;
import tk.nukeduck.hud.util.LayoutManager;
import tk.nukeduck.hud.util.Point;
import tk.nukeduck.hud.util.RenderUtil;

// TODO can this be a TextElement?
public class BlockViewer extends HudElement {
	private final SettingPosition position = new SettingPosition("position", Direction.CORNERS | Direction.flags(Direction.CENTER, Direction.NORTH));
	private final SettingBoolean showBlock = new SettingBoolean("showBlock");
	private final SettingSlider distance = new SettingSlider("distance", 1, 16, 1).setUnlocalizedValue("betterHud.strings.chunks");
	private final SettingBoolean showIds = new SettingBoolean("showIds");
	private final SettingBoolean invNames = new SettingBoolean("invNames");

	public BlockViewer() {
		super("blockViewer");

		settings.add(position);
		settings.add(new Legend("misc"));
		settings.add(showBlock);
		settings.add(invNames);
		settings.add(distance);
		settings.add(showIds);
	}

	@Override
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	public void loadDefaults() {
		settings.set(true);
		position.load(Direction.NORTH_WEST);
		showBlock.set(true);
		distance.value = 16;
		showIds.set(false);
		invNames.set(true);
	}

	@Override
	public Bounds render(RenderGameOverlayEvent event, LayoutManager manager) {
		RayTraceResult trace = MC.getRenderViewEntity().rayTrace(distance.value * 16, 1f);
		if(trace == null || trace.typeOfHit != RayTraceResult.Type.BLOCK) return null;

		IBlockState state = MC.world.getBlockState(trace.getBlockPos());
		ItemStack stack = getDisplayStack(trace, state);
		String text = getBlockName(trace, state, stack);

		if(showIds.get()) text += " " + getIdString(state);

		int w = MC.fontRenderer.getStringWidth(text) + 10;
		if(stack != null && showBlock.get()) w += 21;

		Bounds bounds = new Bounds(w, 10 + MC.fontRenderer.FONT_HEIGHT);
		if(position.getDirection() == Direction.CENTER) {
			bounds.position = new Point(manager.getResolution().x / 2 + SPACER, manager.getResolution().y / 2 + SPACER);
		} else {
			bounds = position.applyTo(new Bounds(w, 10 + MC.fontRenderer.FONT_HEIGHT), manager);
		}
		RenderUtil.drawTooltipBox(bounds.x(), bounds.y(), bounds.width(), bounds.height());

		if(stack != null && showBlock.get()) {
			MC.ingameGUI.drawString(MC.fontRenderer, text, bounds.x() + 26, bounds.y() + 6, Colors.WHITE);

			RenderHelper.enableGUIStandardItemLighting();
			MC.getRenderItem().renderItemAndEffectIntoGUI(stack, bounds.x() + 5, bounds.y() + 2);
			RenderHelper.disableStandardItemLighting();
		} else {
			MC.ingameGUI.drawString(MC.fontRenderer, text, bounds.x() + 5, bounds.y() + 6, Colors.WHITE);
		}
		return bounds;
	}

	/** Creates the most representative item stack for the given result.<br>
	 * If the block has no {@link net.minecraft.item.ItemBlock}, it is impossible to create a stack.
	 *
	 * @see net.minecraftforge.common.ForgeHooks#onPickBlock(RayTraceResult, net.minecraft.entity.player.EntityPlayer, net.minecraft.world.World) */
	private static ItemStack getDisplayStack(RayTraceResult trace, IBlockState state) {
		ItemStack stack = state.getBlock().getPickBlock(state, trace, MC.world, trace.getBlockPos(), MC.player);

		if(FuncsUtil.isStackEmpty(stack)) {
			// Pick block is disabled, however we can grab the information directly
			stack = new ItemStack(state.getBlock(), state.getBlock().getMetaFromState(state));

			if(FuncsUtil.isStackEmpty(stack)) { // There's no registered ItemBlock, no stack exists
				return null;
			}
		}
		// At this point stack contains some item

		if(state.getBlock().hasTileEntity(state)) { // Tile entity data can change rendering or display name
			TileEntity tileEntity = MC.world.getTileEntity(trace.getBlockPos());

			if(tileEntity != null) {
				MC.storeTEInStack(stack, tileEntity);
				System.out.print(stack.getTagCompound() + "\n");
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
		
		return FuncsUtil.isStackEmpty(stack) ? state.getBlock().getLocalizedName() : stack.getDisplayName();
	}

	/** @return Information about the block's related IDs */
	private String getIdString(IBlockState state) {
		String name = Block.REGISTRY.getNameForObject(state.getBlock()).toString();
		int id = Block.getIdFromBlock(state.getBlock());
		int meta = state.getBlock().getMetaFromState(state);

		return String.format("%s(%s:%d/#%04d)", ChatFormatting.YELLOW, name, meta, id);
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onPlayerDisconnected(ClientDisconnectionFromServerEvent event) {
		nameCache.clear();
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onPlayerChangeDimension(PlayerChangedDimensionEvent event) {
		nameCache.clear();
	}

	@SideOnly(Side.SERVER)
	@SubscribeEvent
	public void onBlockBreak(BlockEvent.BreakEvent event) {
		//System.out.println("Clearing block " + event.getPos());
		BetterHud.NET_WRAPPER.sendToDimension(new InventoryNameQuery.Response(event.getPos(), null), event.getWorld().provider.getDimension());
	}

	public static final Map<BlockPos, ITextComponent> nameCache = new HashMap<BlockPos, ITextComponent>();

	public void onNameReceived(BlockPos pos, ITextComponent name) {
		nameCache.put(pos, name);
	}

	/** If the client doesn't know the name of an inventory, this method
	 * asks the server, then until the response is given, a generic
	 * container name will be returned. When the client finds out the actual name
	 * of the inventory, it will return that value */
	private ITextComponent ensureInvName(BlockPos pos) {
		if(!nameCache.containsKey(pos)) {
			//System.out.println("Asking about block " + pos);
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
}
