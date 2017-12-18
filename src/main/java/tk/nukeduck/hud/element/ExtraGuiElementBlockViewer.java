package tk.nukeduck.hud.element;

import java.util.HashMap;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;
import tk.nukeduck.hud.BetterHud;
import tk.nukeduck.hud.element.settings.ElementSettingAbsolutePosition;
import tk.nukeduck.hud.element.settings.ElementSettingAbsolutePositionAnchored;
import tk.nukeduck.hud.element.settings.ElementSettingAnchor;
import tk.nukeduck.hud.element.settings.ElementSettingBoolean;
import tk.nukeduck.hud.element.settings.ElementSettingDivider;
import tk.nukeduck.hud.element.settings.ElementSettingMode;
import tk.nukeduck.hud.element.settings.ElementSettingPosition;
import tk.nukeduck.hud.element.settings.ElementSettingPosition.Position;
import tk.nukeduck.hud.element.settings.ElementSettingSlider;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.FormatUtil;
import tk.nukeduck.hud.util.LayoutManager;
import tk.nukeduck.hud.util.RenderUtil;
import tk.nukeduck.hud.util.StringManager;

public class ExtraGuiElementBlockViewer extends ExtraGuiElement {
	private ElementSettingMode posMode;
	private ElementSettingPosition pos;
	private ElementSettingAbsolutePositionAnchored pos2;
	private ElementSettingAnchor anchor;
	private ElementSettingBoolean showBlock;
	private ElementSettingSlider distance;
	private ElementSettingBoolean showIds;
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
	public String getName() {
		return "blockViewer";
	}
	
	@Override
	public Bounds getBounds(ScaledResolution resolution) {
		return this.bounds;
	}
	
	public ExtraGuiElementBlockViewer() {
		this.replaceStacks.put(Blocks.MOB_SPAWNER, new ItemStack(Blocks.MOB_SPAWNER));
		this.replaceStacks.put(Blocks.LIT_REDSTONE_ORE, new ItemStack(Blocks.REDSTONE_ORE));
		this.replaceStacks.put(Blocks.DRAGON_EGG, new ItemStack(Blocks.DRAGON_EGG));
		this.replaceStacks.put(Blocks.END_PORTAL, new ItemStack(Blocks.END_PORTAL));
		
		this.settings.add(new ElementSettingDivider("position"));
		this.settings.add(posMode = new ElementSettingMode("posMode", new String[] {"setPos", "absolute"}));
		this.settings.add(pos = new ElementSettingPosition("position", Position.combine(Position.TOP_LEFT, Position.TOP_CENTER, Position.TOP_RIGHT, Position.BOTTOM_LEFT, Position.BOTTOM_RIGHT, Position.MIDDLE_CENTER)) {
			@Override
			public boolean getEnabled() {
				return posMode.index == 0;
			}
		});
		this.anchor = new ElementSettingAnchor("anchor");
		this.settings.add(pos2 = new ElementSettingAbsolutePositionAnchored("position2", anchor) {
			@Override
			public boolean getEnabled() {
				return posMode.index == 1;
			}
		});
		this.settings.add(anchor);
		this.settings.add(new ElementSettingDivider("misc"));
		this.settings.add(showBlock = new ElementSettingBoolean("showBlock"));
		/*this.settings.add(invNames = new ElementSettingBoolean("invNames"));
		this.settings.add(new ElementSettingText("invNames"));*/
		this.settings.add(distance = new ElementSettingSlider("distance", 6, 256) {
			@Override
			public String getSliderText() {
				if(this.value % 16 == 0) {
					int chunks = (int) (this.value / 16);
					String chunkString = FormatUtil.translatePre(chunks == 1 ? "strings.chunk" : "strings.chunks", String.valueOf(chunks));
					return FormatUtil.translatePre("menu.settingButton", this.getLocalizedName(), chunkString);
				} else {
					String distanceString = FormatUtil.translatePre("strings.distanceShort", String.valueOf((int) this.value));
					return FormatUtil.translatePre("menu.settingButton", this.getLocalizedName(), distanceString);
				}
			}
		});
		distance.accuracy = 16;
		this.settings.add(showIds = new ElementSettingBoolean("showIds"));
	}
	
	public void update(Minecraft mc) {}
	
	private Bounds bounds = Bounds.EMPTY;
	
	@Override
	public void render(Minecraft mc, ScaledResolution resolution, StringManager stringManager, LayoutManager layoutManager) {
		RayTraceResult mop = mc.getRenderViewEntity().rayTrace(distance.value, 1.0F);
		
		//IBlockState te = mc.theWorld.getBlockState(mop.getBlockPos());
		//ItemStack stack = te.getBlock().getPickBlock(mop, mc.theWorld, mop.getBlockPos());
		
		/*if(te.getBlock() == Blocks.double_plant) {
			IBlockState te2 = mc.theWorld.getBlockState(mop.getBlockPos().down());
			if(te2.getBlock() == Blocks.double_plant) {
				te = te2;
			}
		}*/
		
		if(mop != null && mop.typeOfHit == RayTraceResult.Type.BLOCK) {
			if(mc.theWorld.isAirBlock(mop.getBlockPos())) return;
			
			IBlockState te = mc.theWorld.getBlockState(mop.getBlockPos());
			
			ItemStack stack = te.getBlock().getPickBlock(te, mop, mc.theWorld, mop.getBlockPos(), mc.thePlayer);
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
					l = FormatUtil.translatePre("strings.unknownBlock");
					stack = new ItemStack(Blocks.STONE);
				}
			}
			
			if(showIds.value) {
				String name = Block.REGISTRY.getNameForObject(te.getBlock()).toString();
				String id = String.format("%04d", Block.getIdFromBlock(te.getBlock()));
				String meta = String.valueOf(te.getBlock().getMetaFromState(te));
				l += " " + ChatFormatting.YELLOW + FormatUtil.translatePre("strings.brackets", name + "/#" + id + ":" + meta);
			}
			
			/*if(invNames.value) {
    			TileEntity a = mc.theWorld.getTileEntity(mop.getBlockPos());
    			if(a != null && a instanceof TileEntityChest) {
    				ILockableContainer c = ((BlockChest) te.getBlock()).getLockableContainer(mc.theWorld, mop.getBlockPos());
    				l = c.getDisplayName().getUnformattedText();
    			} else if(a != null && a instanceof IWorldNameable) {
    				IChatComponent guiName = ((IWorldNameable) a).getDisplayName();
    				l = guiName.getUnformattedText();
    			} else if(a != null) {
    				l = a.getClass().toString();
    			}
			}*/
			
			int w = mc.fontRendererObj.getStringWidth(l) + 10;
			if(showBlock.value) w += 21;
			
			int x = resolution.getScaledWidth() / 2 - w - 5;
			int y = resolution.getScaledHeight() / 2 - mc.fontRendererObj.FONT_HEIGHT - 15;
			
			if(posMode.index == 1) {
				this.bounds = new Bounds(x, y, w, 11 + mc.fontRendererObj.FONT_HEIGHT);
				pos2.update(resolution, this.bounds);
				x = pos2.x;
				y = pos2.y;
			} else if(pos.value == Position.TOP_LEFT) {
				x = 5;
				y = layoutManager.get(pos.value);
				layoutManager.add(10 + mc.fontRendererObj.FONT_HEIGHT, pos.value);
			} else if(pos.value == Position.TOP_CENTER) {
				x = (resolution.getScaledWidth() - w) / 2;
				y = BetterHud.proxy.elements.biome.enabled ? 60 : 50;
			} else if(pos.value == Position.TOP_RIGHT) {
				x = resolution.getScaledWidth() - 5 - w;
				y = layoutManager.get(pos.value);
				layoutManager.add(10 + mc.fontRendererObj.FONT_HEIGHT, pos.value);
			} else if(pos.value == Position.BOTTOM_LEFT) {
				x = 5;
				y = resolution.getScaledHeight() - layoutManager.get(pos.value) - 10 - mc.fontRendererObj.FONT_HEIGHT;
				layoutManager.add(10 + mc.fontRendererObj.FONT_HEIGHT, pos.value);
			} else if(pos.value == Position.BOTTOM_RIGHT) {
				x = resolution.getScaledWidth() - 5 - w;
				y = resolution.getScaledHeight() - layoutManager.get(pos.value) - 10 - mc.fontRendererObj.FONT_HEIGHT;
				layoutManager.add(10 + mc.fontRendererObj.FONT_HEIGHT, pos.value);
			}
			if(posMode.index != 1) this.bounds = new Bounds(x, y, w, 11 + mc.fontRendererObj.FONT_HEIGHT);
			
			renderBox(x, y, w, 11 + mc.fontRendererObj.FONT_HEIGHT, RenderUtil.colorARGB(58, 0, 0, 0));
			
			if(showBlock.value) {
				mc.ingameGUI.drawString(mc.fontRendererObj, l, x + 26, y + 6, RenderUtil.colorRGB(255, 255, 255));
				RenderHelper.enableGUIStandardItemLighting();
				mc.getRenderItem().renderItemAndEffectIntoGUI(stack, x + 5, y + 2);
				RenderHelper.disableStandardItemLighting();
			} else {
				mc.ingameGUI.drawString(mc.fontRendererObj, l, x + 5, y + 6, RenderUtil.colorRGB(255, 255, 255));
			}
		}
	}
	
	public void renderBox(int x, int y, int width, int height, int color) {
		RenderUtil.drawRect(x + 1, y, x + width - 1, y + height, color);
		RenderUtil.drawRect(x, y + 1, x + 1, y + height - 1, color);
		RenderUtil.drawRect(x + width - 1, y + 1, x + width, y + height - 1, color);
		
		RenderUtil.drawRect(x + 1, y + 1, x + (width - 1), y + 2, color);
		RenderUtil.drawRect(x + 1, y + height - 2, x + (width - 1), y + height - 1, color);
		
		RenderUtil.drawRect(x + 1, y + 2, x + 2, y + height - 2, color);
		RenderUtil.drawRect(x + (width - 2), y + 2, x + (width - 1), y + height - 2, color);
	}

	@Override
	public boolean shouldProfile() {
		return true;
	}
}