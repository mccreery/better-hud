package tk.nukeduck.hud.element;

import static tk.nukeduck.hud.BetterHud.MC;

import java.util.HashMap;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import tk.nukeduck.hud.element.settings.Legend;
import tk.nukeduck.hud.element.settings.SettingBoolean;
import tk.nukeduck.hud.element.settings.SettingPosition;
import tk.nukeduck.hud.element.settings.SettingSlider;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Colors;
import tk.nukeduck.hud.util.Direction;
import tk.nukeduck.hud.util.LayoutManager;
import tk.nukeduck.hud.util.RenderUtil;

public class BlockViewer extends HudElement {
	private final SettingPosition position = new SettingPosition("position", Direction.CORNERS | Direction.flags(Direction.CENTER, Direction.NORTH));
	private final SettingBoolean showBlock = new SettingBoolean("showBlock");
	private final SettingSlider distance = new SettingSlider("distance", 7, 256, 16) {
		@Override
		public String getSliderText() {
			String value;
			if(this.value % 16 == 0) {
				int chunks = (int) (this.value / 16);
				value = I18n.format(chunks == 1 ? "betterHud.strings.chunk" : "betterHud.strings.chunks", String.valueOf(chunks));
			} else {
				value = I18n.format("betterHud.strings.distanceShort", String.valueOf((int) this.value));
			}
			return I18n.format("betterHud.menu.settingButton", this.getLocalizedName(), value);
		}
	};
	private final SettingBoolean showIds = new SettingBoolean("showIds");
	private final SettingBoolean invNames = new SettingBoolean("invNames");

	private static final HashMap<Block, ItemStack> replaceStacks = new HashMap<Block, ItemStack>();

	static {
		// TODO still needed? can something better be put in place?
		replaceStacks.put(Blocks.MOB_SPAWNER, new ItemStack(Blocks.MOB_SPAWNER));
		replaceStacks.put(Blocks.LIT_REDSTONE_ORE, new ItemStack(Blocks.REDSTONE_ORE));
		replaceStacks.put(Blocks.DRAGON_EGG, new ItemStack(Blocks.DRAGON_EGG));
		replaceStacks.put(Blocks.END_PORTAL, new ItemStack(Blocks.END_PORTAL));
	}

	@Override
	public void loadDefaults() {
		this.settings.set(true);
		position.load(Direction.NORTH_WEST);
		showBlock.set(true);
		distance.value = 256;
		showIds.set(false);
		invNames.set(true);
	}

	public BlockViewer() {
		super("blockViewer");

		settings.add(position);
		this.settings.add(new Legend("misc"));
		this.settings.add(showBlock);
		this.settings.add(invNames);
		this.settings.add(distance);
		this.settings.add(showIds);
	}

	private static final ItemStack UNKNOWN_STACK = new ItemStack(Blocks.STONE);

	@Override
	public Bounds render(RenderGameOverlayEvent event, LayoutManager manager) {
		RayTraceResult trace = MC.getRenderViewEntity().rayTrace(distance.value, 1f);
		if(trace == null || trace.typeOfHit != RayTraceResult.Type.BLOCK) return null;

		IBlockState state = MC.world.getBlockState(trace.getBlockPos());
		ItemStack stack;
		String text;

		if(replaceStacks.containsKey(state.getBlock())) {
			stack = replaceStacks.get(state.getBlock());
		} else {
			stack = state.getBlock().getPickBlock(state, trace, MC.world, trace.getBlockPos(), MC.player);
		}

		ITextComponent temp;
		if(invNames.get() && state.getBlock().hasTileEntity(state) && (temp = MC.world.getTileEntity(trace.getBlockPos()).getDisplayName()) != null) {
			text = temp.getFormattedText();
		} else if(stack != null) {
			text = stack.getDisplayName();
		} else {
			text = I18n.format("betterHud.strings.unknownBlock"); // TODO needed?
			stack = UNKNOWN_STACK;
		}

		if(showIds.get()) {
			String name = Block.REGISTRY.getNameForObject(state.getBlock()).toString();
			int id = Block.getIdFromBlock(state.getBlock());
			int meta = state.getBlock().getMetaFromState(state);

			text = String.format("%s %s(%s:%d/#%04d)", text, ChatFormatting.YELLOW, name, meta, id);
		}

		int w = MC.fontRenderer.getStringWidth(text) + 10;
		if(showBlock.get()) w += 21;

		Bounds bounds = position.applyTo(new Bounds(w, 10 + MC.fontRenderer.FONT_HEIGHT), manager);
		RenderUtil.drawTooltipBox(bounds.x(), bounds.y(), bounds.width(), bounds.height());

		if(showBlock.get()) {
			MC.ingameGUI.drawString(MC.fontRenderer, text, bounds.x() + 26, bounds.y() + 6, Colors.WHITE);

			RenderHelper.enableGUIStandardItemLighting();
			MC.getRenderItem().renderItemAndEffectIntoGUI(stack, bounds.x() + 5, bounds.y() + 2);
			RenderHelper.disableStandardItemLighting();
		} else {
			MC.ingameGUI.drawString(MC.fontRenderer, text, bounds.x() + 5, bounds.y() + 6, Colors.WHITE);
		}
		return bounds;
	}
}
