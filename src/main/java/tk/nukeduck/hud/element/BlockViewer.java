package tk.nukeduck.hud.element;

import static tk.nukeduck.hud.BetterHud.MC;

import java.util.HashMap;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import tk.nukeduck.hud.BetterHud;
import tk.nukeduck.hud.element.settings.SettingAnchoredPosition;
import tk.nukeduck.hud.element.settings.SettingAnchor;
import tk.nukeduck.hud.element.settings.SettingBoolean;
import tk.nukeduck.hud.element.settings.Divider;
import tk.nukeduck.hud.element.settings.SettingMode;
import tk.nukeduck.hud.element.settings.SettingPosition;
import tk.nukeduck.hud.element.settings.SettingPosition.Position;
import tk.nukeduck.hud.element.settings.SettingSlider;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.LayoutManager;
import tk.nukeduck.hud.util.RenderUtil;
import tk.nukeduck.hud.util.StringManager;
import tk.nukeduck.hud.util.constants.Colors;

public class BlockViewer extends HudElement {
	private SettingMode posMode;
	private SettingPosition pos;
	private SettingAnchoredPosition pos2;
	private SettingAnchor anchor;
	private SettingBoolean showBlock;
	private SettingSlider distance;
	private SettingBoolean showIds;
	//private ElementSettingBoolean invNames;
	
	private HashMap<Block, ItemStack> replaceStacks = new HashMap<Block, ItemStack>();
	
	@Override
	public void loadDefaults() {
		this.enabled = true;
		posMode.index = 0;
		pos.value = Position.TOP_CENTER;
		anchor.value = Position.TOP_LEFT.getFlag();
		pos2.x = 5;
		pos2.y = 5;
		showBlock.value = true;
		distance.value = 256;
		showIds.value = false;
		//invNames.value = true;
	}
	
	@Override
	public Bounds getBounds(ScaledResolution resolution) {
		return this.bounds;
	}
	
	public BlockViewer() {
		super("blockViewer");
		this.replaceStacks.put(Blocks.MOB_SPAWNER, new ItemStack(Blocks.MOB_SPAWNER));
		this.replaceStacks.put(Blocks.LIT_REDSTONE_ORE, new ItemStack(Blocks.REDSTONE_ORE));
		this.replaceStacks.put(Blocks.DRAGON_EGG, new ItemStack(Blocks.DRAGON_EGG));
		this.replaceStacks.put(Blocks.END_PORTAL, new ItemStack(Blocks.END_PORTAL));
		
		this.settings.add(new Divider("position"));
		this.settings.add(posMode = new SettingMode("posMode", new String[] {"setPos", "absolute"}));
		this.settings.add(pos = new SettingPosition("position", Position.combine(Position.TOP_LEFT, Position.TOP_CENTER, Position.TOP_RIGHT, Position.BOTTOM_LEFT, Position.BOTTOM_RIGHT, Position.MIDDLE_CENTER)) {
			@Override
			public boolean getEnabled() {
				return posMode.index == 0;
			}
		});
		this.anchor = new SettingAnchor("anchor");
		this.settings.add(pos2 = new SettingAnchoredPosition("position2", anchor) {
			@Override
			public boolean getEnabled() {
				return posMode.index == 1;
			}
		});
		this.settings.add(anchor);
		this.settings.add(new Divider("misc"));
		this.settings.add(showBlock = new SettingBoolean("showBlock"));
		/*this.settings.add(invNames = new ElementSettingBoolean("invNames"));
		this.settings.add(new ElementSettingText("invNames"));*/
		this.settings.add(distance = new SettingSlider("distance", 6, 256) {
			@Override
			public String getSliderText() {
				if(this.value % 16 == 0) {
					int chunks = (int) (this.value / 16);
					String chunkString = I18n.format(chunks == 1 ? "betterHud.strings.chunk" : "betterHud.strings.chunks", String.valueOf(chunks));
					return I18n.format("betterHud.menu.settingButton", this.getLocalizedName(), chunkString);
				} else {
					String distanceString = I18n.format("betterHud.strings.distanceShort", String.valueOf((int) this.value));
					return I18n.format("betterHud.menu.settingButton", this.getLocalizedName(), distanceString);
				}
			}
		});
		distance.accuracy = 16;
		this.settings.add(showIds = new SettingBoolean("showIds"));
	}
	
	public void update() {}
	
	private Bounds bounds = Bounds.EMPTY;
	
	@Override
	public void render(RenderGameOverlayEvent event, StringManager stringManager, LayoutManager layoutManager) {
		RayTraceResult mop = MC.getRenderViewEntity().rayTrace(distance.value, 1.0F);
		
		//IBlockState te = MC.theWorld.getBlockState(mop.getBlockPos());
		//ItemStack stack = te.getBlock().getPickBlock(mop, MC.theWorld, mop.getBlockPos());
		
		/*if(te.getBlock() == Blocks.double_plant) {
			IBlockState te2 = MC.theWorld.getBlockState(mop.getBlockPos().down());
			if(te2.getBlock() == Blocks.double_plant) {
				te = te2;
			}
		}*/
		
		if(mop != null && mop.typeOfHit == RayTraceResult.Type.BLOCK) {
			if(MC.world.isAirBlock(mop.getBlockPos())) return;
			
			IBlockState te = MC.world.getBlockState(mop.getBlockPos());
			
			ItemStack stack = te.getBlock().getPickBlock(te, mop, MC.world, mop.getBlockPos(), MC.player);
			for(Block block : this.replaceStacks.keySet()) {
				if(te.getBlock().equals(block)) {
					stack = this.replaceStacks.get(block);
					break;
				}
			}
			
			String l;
			try {
				l = stack.getDisplayName();
			} catch(NullPointerException e) {
				try {
					l = te.getBlock().getLocalizedName();
					stack = new ItemStack(Blocks.STONE);
				} catch(Exception ex) {
					l = I18n.format("betterHud.strings.unknownBlock");
					stack = new ItemStack(Blocks.STONE);
				}
			}
			
			if(showIds.value) {
				String name = Block.REGISTRY.getNameForObject(te.getBlock()).toString();
				String id = String.format("%04d", Block.getIdFromBlock(te.getBlock()));
				String meta = String.valueOf(te.getBlock().getMetaFromState(te));
				l += " " + ChatFormatting.YELLOW + I18n.format("betterHud.strings.brackets", name + "/#" + id + ":" + meta);
			}
			
			/*if(invNames.value) {
    			TileEntity a = MC.theWorld.getTileEntity(mop.getBlockPos());
    			if(a != null && a instanceof TileEntityChest) {
    				ILockableContainer c = ((BlockChest) te.getBlock()).getLockableContainer(MC.theWorld, mop.getBlockPos());
    				l = c.getDisplayName().getUnformattedText();
    			} else if(a != null && a instanceof IWorldNameable) {
    				IChatComponent guiName = ((IWorldNameable) a).getDisplayName();
    				l = guiName.getUnformattedText();
    			} else if(a != null) {
    				l = a.getClass().toString();
    			}
			}*/
			
			int w = MC.fontRenderer.getStringWidth(l) + 10;
			if(showBlock.value) w += 21;
			
			int x = event.getResolution().getScaledWidth() / 2 - w - 5;
			int y = event.getResolution().getScaledHeight() / 2 - MC.fontRenderer.FONT_HEIGHT - 15;
			
			if(posMode.index == 1) {
				this.bounds = new Bounds(x, y, w, 11 + MC.fontRenderer.FONT_HEIGHT);
				pos2.update(event.getResolution(), this.bounds);
				x = pos2.x;
				y = pos2.y;
			} else if(pos.value == Position.TOP_LEFT) {
				x = 5;
				y = layoutManager.get(pos.value);
				layoutManager.add(10 + MC.fontRenderer.FONT_HEIGHT, pos.value);
			} else if(pos.value == Position.TOP_CENTER) {
				x = (event.getResolution().getScaledWidth() - w) / 2;
				y = BetterHud.proxy.elements.biome.enabled ? 60 : 50;
			} else if(pos.value == Position.TOP_RIGHT) {
				x = event.getResolution().getScaledWidth() - 5 - w;
				y = layoutManager.get(pos.value);
				layoutManager.add(10 + MC.fontRenderer.FONT_HEIGHT, pos.value);
			} else if(pos.value == Position.BOTTOM_LEFT) {
				x = 5;
				y = event.getResolution().getScaledHeight() - layoutManager.get(pos.value) - 10 - MC.fontRenderer.FONT_HEIGHT;
				layoutManager.add(10 + MC.fontRenderer.FONT_HEIGHT, pos.value);
			} else if(pos.value == Position.BOTTOM_RIGHT) {
				x = event.getResolution().getScaledWidth() - 5 - w;
				y = event.getResolution().getScaledHeight() - layoutManager.get(pos.value) - 10 - MC.fontRenderer.FONT_HEIGHT;
				layoutManager.add(10 + MC.fontRenderer.FONT_HEIGHT, pos.value);
			}
			if(posMode.index != 1) this.bounds = new Bounds(x, y, w, 11 + MC.fontRenderer.FONT_HEIGHT);
			
			RenderUtil.drawTooltipBox(x, y, w, 11 + MC.fontRenderer.FONT_HEIGHT);
			//renderBox(x, y, w, 11 + MC.fontRendererObj.FONT_HEIGHT, Colors.fromARGB(58, 0, 0, 0));
			
			if(showBlock.value) {
				MC.ingameGUI.drawString(MC.fontRenderer, l, x + 26, y + 6, Colors.WHITE);
				RenderHelper.enableGUIStandardItemLighting();
				MC.getRenderItem().renderItemAndEffectIntoGUI(stack, x + 5, y + 2);
				RenderHelper.disableStandardItemLighting();
			} else {
				MC.ingameGUI.drawString(MC.fontRenderer, l, x + 5, y + 6, Colors.WHITE);
			}
		}
	}
	
	/*public void renderBox(int x, int y, int width, int height, int color) {
		Tessellator tes = Tessellator.getInstance();

		RenderUtil.renderQuad(tes, x + 1,         y,     width - 2, height,         color);
		RenderUtil.renderQuad(tes, x,             y + 1, 1,         height - 2, color);
		RenderUtil.renderQuad(tes, x + width - 1, y + 1, x + width, y + height - 1, color);
		
		RenderUtil.drawRect(x + 1, y + 1, x + (width - 1), y + 2, color);
		RenderUtil.drawRect(x + 1, y + height - 2, x + (width - 1), y + height - 1, color);
		
		RenderUtil.drawRect(x + 1, y + 2, x + 2, y + height - 2, color);
		RenderUtil.drawRect(x + (width - 2), y + 2, x + (width - 1), y + height - 2, color);
		
		/////////////////////////////////////////

		RenderUtil.drawRect(x + 1, y, x + width - 1, y + height, color);
		RenderUtil.drawRect(x, y + 1, x + 1, y + height - 1, color);
		RenderUtil.drawRect(x + width - 1, y + 1, x + width, y + height - 1, color);
		
		RenderUtil.drawRect(x + 1, y + 1, x + (width - 1), y + 2, color);
		RenderUtil.drawRect(x + 1, y + height - 2, x + (width - 1), y + height - 1, color);
		
		RenderUtil.drawRect(x + 1, y + 2, x + 2, y + height - 2, color);
		RenderUtil.drawRect(x + (width - 2), y + 2, x + (width - 1), y + height - 2, color);
	}*/

	@Override
	public boolean shouldProfile() {
		return true;
	}
}